package com.muiz6.musicplayer.data;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
import com.muiz6.musicplayer.data.db.AudioDatabase;
import com.muiz6.musicplayer.data.db.AudioEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MusicRepository implements
		MediaSessionConnector.MediaMetadataProvider {

	public static final String MEDIA_ID_ROOT = BuildConfig.APPLICATION_ID + ".mediaItemRoot";
	public static final String MEDIA_ID_ALL_SONGS = BuildConfig.APPLICATION_ID + ".allSongs";
	public static final String MEDIA_ID_ALBUMS = BuildConfig.APPLICATION_ID + ".albums";
	public static final String MEDIA_ID_ARTISTS = BuildConfig.APPLICATION_ID + ".artists";
	public static final String MEDIA_ID_GENRES = BuildConfig.APPLICATION_ID + ".genres";
	public static final Character SEPARATOR_MEDIA_ID = '.';

	private static final MediaDescriptionCompat.Builder _DESC_BUILDER =
			new MediaDescriptionCompat.Builder();
	private final MutableLiveData<List<MediaItem>> _allSongList =
			new MutableLiveData<>(Collections.<MediaItem>emptyList());
	private final MutableLiveData<List<MediaItem>> _albumList =
			new MutableLiveData<>(Collections.<MediaItem>emptyList());
	private final Set<Listener> _listenerSet = new HashSet<>();
	private final Observer<List<MediaItem>> _songListObserver =
			new DataObserver(MEDIA_ID_ALL_SONGS, _listenerSet);
	private final Observer<List<MediaItem>> _albumListObserver =
			new DataObserver(MEDIA_ID_ALBUMS, _listenerSet);
	private final Context _context;
	private final AudioDatabase _db;

	@Inject
	public MusicRepository(@Named("Application") Context context, AudioDatabase db) {
		_context = context;
		_db = db;
		final List<AudioEntity> dbSongList = db.getAudioDao().getAllAudio().getValue();
		if (dbSongList != null) {
			_allSongList.postValue(_convert(dbSongList, MediaItem.FLAG_PLAYABLE));
		}
		db.getAudioDao().getAllAudio().observeForever(new Observer<List<AudioEntity>>() {

			@Override
			public void onChanged(List<AudioEntity> audioList) {
				if (audioList != null) {
					_allSongList.postValue(_convert(audioList, MediaItem.FLAG_PLAYABLE));
				}
			}
		});

		// fetch music library when provider is created
		// todo: maybe a memory leak
		// final Thread thread = new Thread(this);
		// thread.start();
	}

	@NonNull
	@Override
	public MediaMetadataCompat getMetadata(@NonNull Player player) {
		int index = player.getCurrentWindowIndex();
		final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
		final MediaItem mediaItem = _allSongList.getValue().get(index);
		final CharSequence title = mediaItem.getDescription().getTitle();
		final CharSequence artist = mediaItem.getDescription().getSubtitle();
		final CharSequence album = mediaItem.getDescription().getDescription();
		if (title != null) {
			builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title.toString());
		}
		if (artist != null) {
			builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist.toString());
		}
		if (album != null) {
			builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, album.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album.toString());
		}
		return builder.build();
	}

	public static BrowserRoot getBrowserRoot() {
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	public static int getIndexFromMediaId(String mediaId) {
		return Integer.parseInt(mediaId.substring(mediaId.lastIndexOf('.') + 1));
	}

	/**
	 * @param mediaId id of the parent media item
	 * @return list of child media items, empty if parent item is not browsable or invalid
	 */
	public List<MediaItem> getChildren(String mediaId) {

		// if accessing from root
		if (mediaId.equals(MusicRepository.MEDIA_ID_ROOT)) {
			final List<MediaItem> rootItems = new ArrayList<>();
			rootItems.add(new MediaItem(_getMediaDescription(MEDIA_ID_ALL_SONGS,
					"All Songs",
					null,
					null,
					null), MediaItem.FLAG_BROWSABLE));
			return rootItems;
		}
		else if (mediaId.equals(MEDIA_ID_ALL_SONGS)) {
			return _allSongList.getValue();
		}
		else if (mediaId.equals(MEDIA_ID_ALBUMS)) {
			return _albumList.getValue();
		}

		// todo: fix this
		else {
			return _allSongList.getValue();
		}

		// else
		// return new ArrayList<>();
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

	// following method
	// belong to
	// MediaSessionConnector.MediaMetadataProvider

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
				sources[i] = factory
						.createMediaSource(mediaItem.getDescription().getMediaUri());
				i++;
			}
			return sources;
		}
		return null;
	}

	public MediaSessionConnector.QueueNavigator getQueueNavigator(MediaSessionCompat session) {
		return new TimelineQueueNavigator(session) {

			@NonNull
			@Override
			public MediaDescriptionCompat getMediaDescription(@NonNull Player player,
					int windowIndex) {
				return _allSongList.getValue().get(windowIndex).getDescription();
			}
		};
	}

	public void addListener(Listener listener) {
		_listenerSet.add(listener);

		// start observing when first listener is added
		if (!_listenerSet.isEmpty()) {
			_allSongList.observeForever(_songListObserver);
			_albumList.observeForever(_albumListObserver);
		}
	}

	public void removeListener(Listener listener) {
		_listenerSet.remove(listener);

		// stop observing when all listeners are removed
		if (_listenerSet.isEmpty()) {
			_allSongList.removeObserver(_songListObserver);
			_albumList.removeObserver(_albumListObserver);
		}
	}

	private static MediaDescriptionCompat _getMediaDescription(String mediaId,
			String title,
			@Nullable Uri mediaUri,
			@Nullable String subtitle,
			@Nullable String description) {
		return _DESC_BUILDER

				// call to reset if set externally
				.setExtras(null)
				.setIconUri(null)
				.setIconBitmap(null)

				// set actual parameters
				.setMediaId(mediaId)
				.setTitle(title)
				.setSubtitle(subtitle)
				.setMediaUri(mediaUri)
				.setDescription(description)
				.build();
	}

	private void _fetchSongList() {
		// final List<MediaItem> newSongList = new ArrayList<>();
		// final Set<String> albumSet = new HashSet<>();
		// final List<MediaItem> newAlbumList = new ArrayList<>();
		//
		// final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		// final String[] projection = {MediaStore.Audio.AudioColumns.DATA,
		// 		MediaStore.Audio.AudioColumns.TITLE,
		// 		MediaStore.Audio.AudioColumns.ALBUM,
		// 		MediaStore.Audio.ArtistColumns.ARTIST};
		// final Cursor cursor = _context.getContentResolver().query(uri,
		// 		projection,
		// 		null,
		// 		null,
		// 		MediaStore.Audio.AudioColumns.TITLE + " ASC");
		//
		// if (cursor != null) {
		// 	int i = 0, albumCount = 0;
		// 	while (cursor.moveToNext()) {
		// 		final String path = cursor.getString(0);
		// 		final String name = cursor.getString(1);
		// 		final String album = cursor.getString(2);
		// 		String artist = cursor.getString(3);
		// 		if (artist.equals("<unknown>")) {
		// 			artist = null;
		// 		}
		// 		final String mediaId = MusicRepository.MEDIA_ID_ALL_SONGS
		// 				+ MusicRepository.SEPARATOR_MEDIA_ID
		// 				+ i++;
		// 		newSongList.add(new MediaBrowserCompat.MediaItem(_getMediaDescription(mediaId,
		// 				name,
		// 				Uri.parse(path),
		// 				artist,
		// 				album),
		// 				MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
		//
		// 		// create album data
		// 		if (album != null && !albumSet.contains(album)) {
		// 			albumSet.add(album);
		// 			final String albumId = MEDIA_ID_ALBUMS + SEPARATOR_MEDIA_ID + albumCount++;
		// 			final MediaDescriptionCompat description = _DESC_BUILDER.setMediaId(albumId)
		// 					.setTitle(album)
		// 					.setSubtitle(artist)
		// 					.setDescription(null)
		// 					.setMediaUri(null)
		// 					.setIconBitmap(null)
		// 					.setIconUri(null)
		// 					.setExtras(null)
		// 					.build();
		// 			final MediaItem albumItem = new MediaItem(description,
		// 					MediaItem.FLAG_BROWSABLE);
		// 			newAlbumList.add(albumItem);
		// 		}
		// 	}
		// 	cursor.close();
		// }
		// _allSongList.postValue(newSongList);
		// _albumList.postValue(newAlbumList);
	}

	private List<MediaItem> _convert(List<AudioEntity> list, int flags) {
		final List<MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
		for (final AudioEntity audio : list) {
			final Bundle extras = new Bundle();
			extras.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, audio.getPath());
			extras.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, audio.getTitle());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audio.getAlbum());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			extras.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, audio.getDuration());
			extras.putString(MediaMetadataCompat.METADATA_KEY_GENRE, audio.getGenre());
			final MediaDescriptionCompat description = builder
					.setMediaId(MEDIA_ID_ALL_SONGS + audio.getId())
					.setTitle(audio.getDisplayName())
					.setSubtitle(audio.getArtist())
					.setExtras(extras)
					.build();
			final MediaItem mediaItem = new MediaItem(description, flags);
			newList.add(mediaItem);
		}
		return newList;
	}

	public interface Listener {

		void onChildrenChanged(String parentMediaId);
	}
}
