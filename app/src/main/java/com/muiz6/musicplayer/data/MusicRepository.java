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
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.MediaBrowserServiceCompat.BrowserRoot;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.muiz6.musicplayer.BuildConfig;
import com.muiz6.musicplayer.data.db.AlbumPojo;
import com.muiz6.musicplayer.data.db.ArtistPojo;
import com.muiz6.musicplayer.data.db.AudioDao;
import com.muiz6.musicplayer.data.db.AudioDatabase;
import com.muiz6.musicplayer.data.db.GenrePojo;
import com.muiz6.musicplayer.data.db.SongPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MusicRepository {

	public static final String MEDIA_ID_ROOT = BuildConfig.APPLICATION_ID + ".mediaItemRoot";
	public static final String MEDIA_ID_SONGS = BuildConfig.APPLICATION_ID + ".songs";
	public static final String MEDIA_ID_ALBUMS = BuildConfig.APPLICATION_ID + ".albums";
	public static final String MEDIA_ID_ARTISTS = BuildConfig.APPLICATION_ID + ".artists";
	public static final String MEDIA_ID_GENRES = BuildConfig.APPLICATION_ID + ".genres";
	public static final Character SEPARATOR_MEDIA_ID = '.';
	public static final String KEY_EXTRAS_MEDIA_TYPE = BuildConfig.APPLICATION_ID + ".mediaType";
	public static final String MEDIA_TYPE_SONG = BuildConfig.APPLICATION_ID + ".mediaType.Song";
	public static final String MEDIA_TYPE_ARTIST = BuildConfig.APPLICATION_ID + ".mediaType.Artist";
	public static final String MEDIA_TYPE_ALBUM = BuildConfig.APPLICATION_ID + ".mediaType.Album";
	public static final String MEDIA_TYPE_GENRE = BuildConfig.APPLICATION_ID + ".mediaType.Genre";

	// todo remove this from here
	private final MutableLiveData<List<MediaItem>> _allSongList =
			new MutableLiveData<>(Collections.<MediaItem>emptyList());
	private final Handler _handler = new Handler(Looper.getMainLooper());
	private final Context _context;
	private final AudioDao _dao;

	@Inject
	public MusicRepository(@Named("Application") Context context,
			AudioDatabase db) {
		_context = context;
		_dao = db.getAudioDao();
	}

	@NonNull
	public static BrowserRoot getBrowserRoot() {
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	public static int getIndexFromMediaId(@NonNull String mediaId) {
		return Integer.parseInt(mediaId.substring(mediaId.lastIndexOf('.') + 1));
	}

	@NonNull
	public static String getArtistFromArtistMediaId(@NonNull String mediaId) {
		return mediaId.substring(mediaId.lastIndexOf(SEPARATOR_MEDIA_ID) + 1);
	}

	@NonNull
	public static String getGenreFromGenreMediaId(@NonNull String mediaId) {
		return mediaId.substring(mediaId.lastIndexOf(SEPARATOR_MEDIA_ID) + 1);
	}

	@Nullable
	public MediaItem getMediaItemById(String mediaId) {
		for (final MediaItem i : _allSongList.getValue()) {
			if (i.getMediaId().equals(mediaId)) {
				return i;
			}
		}

		// else
		return null;
	}

	@Nullable
	public MediaSource[] getQueueBytMediaId(String mediaId) {

		// todo: use media id to provide queue
		if (_allSongList.getValue().size() > 0) {
			final MediaSource[] sources = new MediaSource[_allSongList.getValue().size()];
			final DataSource.Factory dataSourceFactory =
					new DefaultDataSourceFactory(_context, "exoplayer-codelab");
			final MediaSourceFactory factory = new ProgressiveMediaSource.Factory(dataSourceFactory);
			int i = 0;
			for (final MediaBrowserCompat.MediaItem mediaItem : _allSongList.getValue()) {
				sources[i++] = factory.createMediaSource(mediaItem.getDescription().getMediaUri());
			}
			return sources;
		}
		return null;
	}

	// todo: remove this from here
	public MediaSessionConnector.QueueNavigator getQueueNavigator(MediaSessionCompat session) {
		return new TimelineQueueNavigator(session) {

			@Override
			public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
				return _allSongList.getValue().get(windowIndex).getDescription();
			}
		};
	}

	public void sendResult(String parentMediaId,
			@NonNull MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>> result) {

		// if accessing from root
		if (parentMediaId.equals(MusicRepository.MEDIA_ID_ROOT)) {
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
		else if (parentMediaId.equals(MEDIA_ID_SONGS)) {
			_sendAllSongList(result);
		}
		else if (parentMediaId.equals(MEDIA_ID_ALBUMS)) {
			_sendAlbumList(result);
		}
		else if (parentMediaId.equals(MEDIA_ID_ARTISTS)) {
			_sendArtistList(result);
		}
		else if (parentMediaId.equals(MEDIA_ID_GENRES)) {
			_sendGenreList(result);
		}
		else if (parentMediaId.startsWith(MEDIA_ID_ARTISTS)) {
			_browseArtist(parentMediaId, result);
		}
		else if (parentMediaId.startsWith(MEDIA_ID_GENRES)) {
			_browseGenre(parentMediaId, result);
		}
		else if (parentMediaId.startsWith(MEDIA_ID_ALBUMS)) {
			_browseAlbum(parentMediaId, result);
		}
		else {
			result.sendResult(null);
		}
	}

	public void searchArtistsByGenre(final String genre,
			final MediaBrowserCompat.SearchCallback callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<MediaItem> artistMediaItems =
						_getArtistMediaItems(_dao.getArtistListByGenre(genre));
				_handler.post(new Runnable() {

					@Override
					public void run() {
						callback.onSearchResult(genre, null, artistMediaItems);
					}
				});
			}
		}).start();
	}

	private static List<MediaItem> _getSongMediaItems(List<SongPojo> list) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
		int i = 0;
		for (final SongPojo audio : list) {
			final Bundle extras = new Bundle();
			extras.putString(KEY_EXTRAS_MEDIA_TYPE, MEDIA_TYPE_SONG);
			extras.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
					audio.getTitle());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			extras.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, audio.getDuration());
			final MediaDescriptionCompat description = builder
					.setMediaId(MusicRepository.MEDIA_ID_SONGS
							+ MusicRepository.SEPARATOR_MEDIA_ID
							+ i++)
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
		int i = 0;
		for (final ArtistPojo audio : list) {
			final Bundle extras = new Bundle();
			extras.putString(KEY_EXTRAS_MEDIA_TYPE, MEDIA_ID_ARTISTS);
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			final MediaDescriptionCompat description = builder
					.setMediaId(MusicRepository.MEDIA_ID_ARTISTS
							+ MusicRepository.SEPARATOR_MEDIA_ID
							+ i++
							+ MusicRepository.SEPARATOR_MEDIA_ID
							+ audio.getArtist())
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
		int i = 0;
		for (final AlbumPojo audio : list) {
			final Bundle extras = new Bundle();
			extras.putString(KEY_EXTRAS_MEDIA_TYPE, MEDIA_TYPE_ALBUM);
			extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audio.getAlbum());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			final MediaDescriptionCompat description = builder
					.setMediaId(MEDIA_ID_ALBUMS
							+ SEPARATOR_MEDIA_ID
							+ i++
							+ SEPARATOR_MEDIA_ID
							+ audio.getAlbum())
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

	private void _sendAllSongList(final MediaBrowserServiceCompat.Result<List<MediaItem>> result) {
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<MediaItem> songItems = _getSongMediaItems(_dao.getAllSongList());
				_allSongList.postValue(songItems);
				result.sendResult(songItems);
			}
		}).start();
	}

	private void _sendAlbumList(final MediaBrowserServiceCompat.Result<List<MediaItem>> result) {
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				result.sendResult(_getAlbumMediaItems(_dao.getAllAlbumList()));
			}
		}).start();
	}

	private void _sendArtistList(final MediaBrowserServiceCompat.Result<List<MediaItem>> result) {
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				result.sendResult(_getArtistMediaItems(_dao.getAllArtistList()));
			}
		}).start();
	}

	private void _sendGenreList(final MediaBrowserServiceCompat.Result<List<MediaItem>> result) {
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<GenrePojo> list = _dao.getGenreList();
				final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				int i = 0;
				for (final GenrePojo audio : list) {
					final Bundle extras = new Bundle();
					extras.putString(MediaMetadataCompat.METADATA_KEY_GENRE, audio.getGenre());
					final MediaDescriptionCompat description = builder
							.setMediaId(MusicRepository.MEDIA_ID_GENRES
									+ MusicRepository.SEPARATOR_MEDIA_ID
									+ i++
									+ MusicRepository.SEPARATOR_MEDIA_ID
									+ audio.getGenre())
							.setTitle(audio.getGenre())
							.setExtras(extras)
							.build();
					final MediaBrowserCompat.MediaItem mediaItem =
							new MediaBrowserCompat.MediaItem(description,
									MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
					newList.add(mediaItem);
				}
				result.sendResult(newList);
			}
		}).start();
	}

	private void _browseArtist(@NonNull String parentId,
			@NonNull final MediaBrowserServiceCompat.Result<List<MediaItem>> result) {
		final String artist = getArtistFromArtistMediaId(parentId);
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<MediaItem> resultList = new ArrayList<>();
				resultList.addAll(_getSongMediaItems(_dao.getSongListByArtist(artist)));
				resultList.addAll(_getAlbumMediaItems(_dao.getAlbumListByArtist(artist)));
				result.sendResult(resultList);
			}
		}).start();
	}

	private void _browseGenre(@NonNull String parentId,
			@NonNull final MediaBrowserServiceCompat.Result<List<MediaItem>> result) {
		final String genre = getGenreFromGenreMediaId(parentId);
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<MediaItem> resultList = new ArrayList<>();
				resultList.addAll(_getSongMediaItems(_dao.getSongListByGenre(genre)));
				resultList.addAll(_getAlbumMediaItems(_dao.getAlbumListByGenre(genre)));
				result.sendResult(resultList);
			}
		}).start();
	}

	private void _browseAlbum(@NonNull String parentId,
			@NonNull final MediaBrowserServiceCompat.Result<List<MediaItem>> result) {
		final String album = getArtistFromArtistMediaId(parentId); // same impl
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				result.sendResult(_getSongMediaItems(_dao.getSongListByAlbum(album)));
			}
		}).start();
	}
}
