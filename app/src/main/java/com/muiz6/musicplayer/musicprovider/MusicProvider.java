package com.muiz6.musicplayer.musicprovider;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
	private final Context _context;
	// private final AsyncFetch _asyncFetch;

	public MusicProvider(MediaBrowserServiceCompat service) {
		_context = service;
		// _asyncFetch = asyncFetch;

		// fetch music library when provider is created
		new _AsyncFetchAllSongs(new WeakReference<>(service), _allSongList).execute();
	}

	@NonNull
	@Override
	public MediaMetadataCompat getMetadata(@NonNull Player player) {
		int index = player.getCurrentWindowIndex();
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

	public static MusicProvider getInstance(MediaBrowserServiceCompat service) {
		if (_instance == null) {
			_instance = new MusicProvider(service);
		}
		return _instance;
	}

	public static BrowserRoot getBrowserRoot() {
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	public static int getIndexFromMediaId(String mediaId) {
		return Integer.parseInt(mediaId.substring(mediaId.indexOf('.') + 1));
	}

	/**
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
		for (final MediaItem i : _allSongList) {
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
		if (_allSongList.size() > 0) {
			final MediaSource[] sources = new MediaSource[_allSongList.size()];
			final DataSource.Factory dataSourceFactory =
					new DefaultDataSourceFactory(_context, "exoplayer-codelab");
			final MediaSourceFactory factory = new ProgressiveMediaSource.Factory(dataSourceFactory);
			int i = 0;
			for (final MediaBrowserCompat.MediaItem mediaItem : _allSongList) {
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
				return _allSongList.get(windowIndex).getDescription();
			}
		};
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
