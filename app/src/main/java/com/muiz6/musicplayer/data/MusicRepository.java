package com.muiz6.musicplayer.data;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat.BrowserRoot;
import androidx.media.MediaBrowserServiceCompat.Result;

import com.google.gson.Gson;
import com.muiz6.musicplayer.BuildConfig;
import com.muiz6.musicplayer.data.db.AudioDao;
import com.muiz6.musicplayer.data.db.AudioDatabase;
import com.muiz6.musicplayer.data.db.pojos.AlbumPojo;
import com.muiz6.musicplayer.data.db.pojos.ArtistPojo;
import com.muiz6.musicplayer.data.db.pojos.GenrePojo;
import com.muiz6.musicplayer.data.db.pojos.SongPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MusicRepository implements AudioDatabase.Callback {

	public static final String MEDIA_ID_ROOT;
	public static final String KEY_EXTRAS_MEDIA_CATEGORY = BuildConfig.APPLICATION_ID + ".mediaType";
	public static final String MEDIA_ID_SONGS;
	public static final String MEDIA_ID_ALBUMS;
	public static final String MEDIA_ID_ARTISTS;
	public static final String MEDIA_ID_GENRES;

	private static final Gson _GSON = new Gson();
	private final Handler _handler = new Handler(Looper.getMainLooper());
	private final Map<String, _LoadLevel1> _mapLoadLevel1 = new HashMap<>();
	private final Map<String, _LoadLevel2> _mapLoadLevel2 = new HashMap<>();
	private final AudioDatabase _db;
	private final AudioDao _dao;
	private Listener _listener;

	static {
		MEDIA_ID_ROOT = _GSON.toJson(new MediaIdPojo(MediaIdPojo.CATEGORY_ROOT,
				MediaIdPojo.VALUE_ROOT, 0, null, null));
		MEDIA_ID_SONGS = _GSON.toJson(new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
				MediaIdPojo.VALUE_LIBRARY_SONGS, 0 ,null, null));
		MEDIA_ID_ALBUMS = _GSON.toJson(new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
				MediaIdPojo.VALUE_LIBRARY_ALBUMS, 0 ,null, null));
		MEDIA_ID_ARTISTS = _GSON.toJson(new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
				MediaIdPojo.VALUE_LIBRARY_ARTISTS, 0 ,null, null));
		MEDIA_ID_GENRES = _GSON.toJson(new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
				MediaIdPojo.VALUE_LIBRARY_GENRES, 0 ,null, null));
	}

	@Inject
	public MusicRepository(@Named("Application") Context context, AudioDatabase db) {
		_db = db;
		_dao = db.getAudioDao();

		// populate map collections to use with adapter pattern
		_populateMapLevel1();
		_populateMapLevel2();
	}

	@Override
	public void onCompletion(boolean success) {
		if (_listener != null) {
			_handler.post(new Runnable() {

				@Override
				public void run() {

					// notify everyone that all data has changed
					// just notifying root id is not working
					_listener.notifyChildrenChanged(MEDIA_ID_SONGS);
					_listener.notifyChildrenChanged(MEDIA_ID_ALBUMS);
					_listener.notifyChildrenChanged(MEDIA_ID_ARTISTS);
					_listener.notifyChildrenChanged(MEDIA_ID_GENRES);
				}
			});
		}
	}

	@NonNull
	public static BrowserRoot getBrowserRoot() {
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	/**
	 * @param mediaId media id of playable only media item,
	 * @return Get index of a playable only media item in queue, returns 0 if mediaid is invalid
	 */
	public static int getIndexFromMediaId(@NonNull String mediaId) {
		final MediaIdPojo objMediaId = _GSON.fromJson(mediaId, MediaIdPojo.class);
		if (objMediaId != null) {
			return objMediaId.getIndex();
		}
		return 0;
	}

	/**
	 * Gets title of media item associated with a browsable media id
	 * @param parentId media id of a browsable media item
	 * @return title of browsable media item
	 */
	@NonNull
	public static String getTitleFromMediaId(@NonNull String parentId) {
		final MediaIdPojo objMediaId = _GSON.fromJson(parentId, MediaIdPojo.class);
		if (objMediaId != null) {
			final String  value = objMediaId.getValue();
			if (value != null) {
				return value;
			}
		}
		return parentId;
	}

	@Nullable
	public static MediaIdPojo getMediaId(@NonNull String mediaId) {
		return _GSON.fromJson(mediaId, MediaIdPojo.class);
	}

	public void loadChildren(@NonNull String parentMediaId,
			@NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
		boolean flagHandled = false;
		final MediaIdPojo objMediaId = _GSON.fromJson(parentMediaId, MediaIdPojo.class);
		if (objMediaId != null) {
			final String category = objMediaId.getCategory();
			final String value = objMediaId.getValue();
			switch (category) {
				case MediaIdPojo.CATEGORY_ROOT:
					_browseRoot(result);
					flagHandled = true;
					break;
				case MediaIdPojo.CATEGORY_LIBRARY:
					final _LoadLevel1 action = _mapLoadLevel1.get(value);
					if (action != null) {
						action.action(result);
						flagHandled = true;
					}
					break;
				case MediaIdPojo.CATEGORY_ALBUM:
				case MediaIdPojo.CATEGORY_ARTIST:
				case MediaIdPojo.CATEGORY_GENRE:
					final _LoadLevel2 action2 = _mapLoadLevel2.get(category);
					if (action2 != null) {
						action2.action(value, result);
						flagHandled = true;
					}
			}
		}
		if (!flagHandled) {
			result.sendResult(Collections.<MediaItem>emptyList());
		}
	}

	public void scanMusicLibrary() {
		_db.scanMusicLibrary();
	}

	public void setListener(@Nullable Listener listener) {
		_listener = listener;
		if (_listener != null) {
			_db.setCallback(this);
		}
		else {
			_db.setCallback(null);
		}
	}

	private static void _browseRoot(@NonNull final Result<List<MediaItem>> result) {
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				final List<MediaItem> rootItems = new ArrayList<>();
				rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_SONGS)
						.setTitle("All Songs")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_ALBUMS)
						.setTitle("Albums")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_ARTISTS)
						.setTitle("Artists")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_GENRES)
						.setTitle("Genres")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				result.sendResult(rootItems);
			}
		}).start();
	}

	public static List<MediaItem> getSongMediaItems(List<SongPojo> list) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
		int i = 0;
		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_SONG);
		for (final SongPojo audio : list) {
			objMediaId.setValue(audio.getTitle());
			objMediaId.setIndex(i++);
			final Bundle extras = new Bundle();
			extras.putString(KEY_EXTRAS_MEDIA_CATEGORY, MediaIdPojo.CATEGORY_SONG);
			extras.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
					audio.getTitle());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			extras.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, audio.getDuration());
			final MediaDescriptionCompat description = builder
					.setMediaId(_GSON.toJson(objMediaId))
					.setMediaUri(Uri.parse(audio.getPath()))
					.setTitle(audio.getDisplayName())
					.setSubtitle(audio.getArtist())
					.setExtras(extras)
					.build();
			final MediaBrowserCompat.MediaItem mediaItem =
					new MediaBrowserCompat.MediaItem(description,
							MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
			newList.add(mediaItem);
		}
		return newList;
	}

	private static List<MediaItem> _getArtistMediaItems(List<ArtistPojo> list) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();

		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_ARTIST);

		for (final ArtistPojo audio : list) {
			final Bundle extras = new Bundle();

			objMediaId.setValue(audio.getArtist());

			extras.putString(KEY_EXTRAS_MEDIA_CATEGORY, MediaIdPojo.CATEGORY_ARTIST);
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			final MediaDescriptionCompat description = builder
					.setMediaId(_GSON.toJson(objMediaId))
					.setTitle(audio.getArtist())
					.setExtras(extras)
					.build();
			final MediaBrowserCompat.MediaItem mediaItem =
					new MediaBrowserCompat.MediaItem(description,
							MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
			newList.add(mediaItem);
		}
		return newList;
	}

	private static List<MediaItem> _getAlbumMediaItems(List<AlbumPojo> list) {

		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();

		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_ALBUM);

		for (final AlbumPojo audio : list) {
			objMediaId.setValue(audio.getAlbum());

			final Bundle extras = new Bundle();
			extras.putString(KEY_EXTRAS_MEDIA_CATEGORY, MediaIdPojo.CATEGORY_ALBUM);
			extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audio.getAlbum());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			final MediaDescriptionCompat description = builder
					.setMediaId(_GSON.toJson(objMediaId))
					.setTitle(audio.getAlbum())
					.setSubtitle(audio.getArtist())
					.setExtras(extras)
					.build();
			final MediaBrowserCompat.MediaItem mediaItem =
					new MediaBrowserCompat.MediaItem(description,
							MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
			newList.add(mediaItem);
		}
		return newList;
	}

	private static List<MediaItem> _getGenreList(List<GenrePojo> list) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();

		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_GENRE);

		for (final GenrePojo audio : list) {
			objMediaId.setValue(audio.getGenre());

			final Bundle extras = new Bundle();
			extras.putString(MusicRepository.KEY_EXTRAS_MEDIA_CATEGORY,
					MediaIdPojo.CATEGORY_GENRE);
			extras.putString(MediaMetadataCompat.METADATA_KEY_GENRE, audio.getGenre());
			final MediaDescriptionCompat description = builder
					.setMediaId(_GSON.toJson(objMediaId))
					.setTitle(audio.getGenre())
					.setExtras(extras)
					.build();
			final MediaBrowserCompat.MediaItem mediaItem =
					new MediaBrowserCompat.MediaItem(description,
							MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
			newList.add(mediaItem);
		}
		return newList;
	}

	private void _populateMapLevel1() {
		final _LoadLevel1 loadLevel1Songs = new _LoadLevel1() {

			@Override
			public void action(@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<MediaItem> songItems = getSongMediaItems(_dao.getAllSongList());
						result.sendResult(songItems);
					}
				}).start();
			}
		};
		final _LoadLevel1 loadLevel1Albums = new _LoadLevel1() {

			@Override
			public void action(@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_getAlbumMediaItems(_dao.getAllAlbumList()));
					}
				}).start();
			}
		};
		final _LoadLevel1 loadLevel1Artists = new _LoadLevel1() {

			@Override
			public void action(@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_getArtistMediaItems(_dao.getAllArtistList()));
					}
				}).start();
			}
		};
		final _LoadLevel1 loadLevel1Genres = new _LoadLevel1() {

			@Override
			public void action(@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_getGenreList(_dao.getGenreList()));
					}
				}).start();
			}
		};
		_mapLoadLevel1.put(MediaIdPojo.VALUE_LIBRARY_SONGS, loadLevel1Songs);
		_mapLoadLevel1.put(MediaIdPojo.VALUE_LIBRARY_ALBUMS, loadLevel1Albums);
		_mapLoadLevel1.put(MediaIdPojo.VALUE_LIBRARY_ARTISTS, loadLevel1Artists);
		_mapLoadLevel1.put(MediaIdPojo.VALUE_LIBRARY_GENRES, loadLevel1Genres);
	}

	private void _populateMapLevel2() {
		final _LoadLevel2 actionBrowseArtist = new _LoadLevel2() {

			@Override
			public void action(@NonNull final String value,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<MediaItem> resultList = new ArrayList<>();
						resultList.addAll(getSongMediaItems(_dao.getSongListByArtist(value)));
						resultList.addAll(_getAlbumMediaItems(_dao.getAlbumListByArtist(value)));
						result.sendResult(resultList);
					}
				}).start();
			}
		};
		final _LoadLevel2 actionBrowseGenre = new _LoadLevel2() {

			@Override
			public void action(@NonNull final String value,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<MediaItem> resultList = new ArrayList<>();
						resultList.addAll(getSongMediaItems(_dao.getSongListByGenre(value)));
						resultList.addAll(_getAlbumMediaItems(_dao.getAlbumListByGenre(value)));
						result.sendResult(resultList);
					}
				}).start();
			}
		};
		final _LoadLevel2 actionBrowseAlbum = new _LoadLevel2() {

			@Override
			public void action(@NonNull final String value,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(getSongMediaItems(_dao.getSongListByAlbum(value)));
					}
				}).start();
			}
		};
		_mapLoadLevel2.put(MediaIdPojo.CATEGORY_ALBUM, actionBrowseAlbum);
		_mapLoadLevel2.put(MediaIdPojo.CATEGORY_ARTIST, actionBrowseArtist);
		_mapLoadLevel2.put(MediaIdPojo.CATEGORY_GENRE, actionBrowseGenre);
	}

	public interface Listener {

		void notifyChildrenChanged(String string);
	}

	// action associated for level 1 of the tree hierarchy
	// loading all songs, all alums etc.
	private interface _LoadLevel1 {
		void action(@NonNull Result<List<MediaItem>> result);
	}

	// action associated for level 2 of the tree hierarchy
	// loading one album, artist etc.
	private interface _LoadLevel2 {
		void action(@NonNull String value, @NonNull Result<List<MediaItem>> result);
	}
}
