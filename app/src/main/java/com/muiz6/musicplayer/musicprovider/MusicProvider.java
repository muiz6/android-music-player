package com.muiz6.musicplayer.musicprovider;

import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.MediaBrowserServiceCompat.BrowserRoot;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

// singleton pattern
public class MusicProvider implements MediaSessionConnector.MediaMetadataProvider {

	public static final String MEDIA_ID_ALL_SONGS = "allSongs";
	public static final String MEDIA_ID_ROOT = "mediaItemRoot";
	public static final Character SEPARATOR_MEDIA_ID = '.';
	// public static final String MEDIA_ID_ARTISTS = "artists";
	// public static final String MEDIA_ID_PLAYLISTS = "playlists";
	// public static final String MEDIA_ID_ALBUMS = "albums";
	// public static final String MEDIA_ID_FOLDERS = "folders";
	// public static final String MEDIA_ID_GENRES = "genres";

	private static final MediaDescriptionCompat.Builder _DESC_BUILDER =
			new MediaDescriptionCompat.Builder();
	private static MusicProvider _instance;
	private final List<MediaItem> _allSongList = new ArrayList<>();
	private final MediaBrowserServiceCompat _service;
	private int _offset; // offset of player track with all songs list

	// singleton pattern
	private MusicProvider(MediaBrowserServiceCompat service) {
		_service = service;

		// fetch music library when provider is created
		new _AsyncFetchAllSongs(new WeakReference<>(service), _allSongList).execute();
	}

	public static MusicProvider getInstance(MediaBrowserServiceCompat service) {
		if (_instance == null) {
			_instance = new MusicProvider(service);
		}
		return _instance;
	}

	public static BrowserRoot getBrowserRoot() {
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	/**
	 *
	 * @param mediaId id of the parent media item
	 * @return list of child media items, empty if parent item is not browsable or invalid
	 */
	public List<MediaItem> getChildren(String mediaId) {

		// if accessing from root
		if (mediaId.equals(MusicProvider.MEDIA_ID_ROOT)) {

			final ArrayList<MediaItem> rootItems = new ArrayList<>();
			rootItems.add(new MediaItem(_getMediaDescription(MEDIA_ID_ALL_SONGS,
					"All Songs",
					"All Songs",
					null), MediaItem.FLAG_BROWSABLE));
			return rootItems;
		}
		else if (mediaId.equals(MEDIA_ID_ALL_SONGS)) {
			return _allSongList;
		}

		// else
		return new ArrayList<>();
	}

	@Nullable
	public MediaItem getMediaItemById(String mediaId) {
		for (final MediaItem i:_allSongList) {
			if (i.getMediaId().equals(mediaId)) {
				return i;
			}
		}

		// else
		return null;
	}

	@Nullable
	public MediaSource[] getQueueBytMediaId(String mediaId) {

		// todo: parse int in separate method for readability
		final int mediaIndex = Integer.parseInt(mediaId.substring(mediaId.indexOf('.') + 1));
		_offset = mediaIndex;
		if (_allSongList.size() > 0) {
			final MediaSource[] sources = new MediaSource[_allSongList.size()];
			final DataSource.Factory dataSourceFactory =
					new DefaultDataSourceFactory(_service, "exoplayer-codelab");
			final MediaSourceFactory factory = new ProgressiveMediaSource.Factory(dataSourceFactory);
			int queueIndex = 0;

			// start queue from the selected media item and add previous songs at the end of queue
			for (int i = mediaIndex; i < sources.length; i++, queueIndex++) {
				sources[queueIndex] = factory
						.createMediaSource(_allSongList.get(i).getDescription().getMediaUri());
			}
			for (int i = 0; i < mediaIndex; i++, queueIndex++) {
				sources[queueIndex] = factory
						.createMediaSource(_allSongList.get(i).getDescription().getMediaUri());
			}
			return sources;
		}
		return null;
	}

	// following method
	// belong to
	// MediaSessionConnector.MediaMetadataProvider

	@NonNull
	@Override
	public MediaMetadataCompat getMetadata(@NonNull Player player) {

		// mod to prevent array out of bounds exception
		int index = (player.getCurrentWindowIndex() + _offset) % _allSongList.size();
		final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
		final MediaItem mediaItem = _allSongList.get(index);
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

	private static MediaDescriptionCompat _getMediaDescription(String mediaId, String title,
			String subtitle, @Nullable Uri mediaUri) {
		return _DESC_BUILDER

				// call to reset if set externally
				.setExtras(null)
				.setIconUri(null)
				.setDescription(null)
				.setIconBitmap(null)

				// set actual parameters
				.setMediaId(mediaId)
				.setTitle(title)
				.setSubtitle(subtitle)
				.setMediaUri(mediaUri)
				.build();
	}
}
