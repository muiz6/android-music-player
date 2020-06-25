package com.muiz6.musicplayer.musicservice.musicprovider;

import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;

import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.MediaBrowserServiceCompat.BrowserRoot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

// singleton pattern
public class MusicProvider {

	public static final String MEDIA_ID_ALL_SONGS = "all_songs";
	public static final String MEDIA_ID_ROOT = "media_items_root";
	// public static final String MEDIA_ID_ARTISTS = "artists";
	// public static final String MEDIA_ID_PLAYLISTS = "playlists";
	// public static final String MEDIA_ID_ALBUMS = "albums";
	// public static final String MEDIA_ID_FOLDERS = "folders";
	// public static final String MEDIA_ID_GENRES = "genres";

	private static final MediaDescriptionCompat.Builder _DESC_BUILDER =
			new MediaDescriptionCompat.Builder();
	private static MusicProvider _INSTANCE;
	private final List<MediaItem> _allSongList = new ArrayList<>();
	private final MediaBrowserServiceCompat _service;

	// singleton pattern
	private MusicProvider(MediaBrowserServiceCompat service) {
		_service = service;

		// fetch music library when provider is created
		new _AsyncFetchAllSongs(new WeakReference<>(service), _allSongList).execute();
	}

	public static MusicProvider getInstance(MediaBrowserServiceCompat service) {
		if (_INSTANCE == null) {
			_INSTANCE = new MusicProvider(service);
		}
		return _INSTANCE;
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

	private static MediaDescriptionCompat _getMediaDescription(String mediaId, String title,
			String subtitle, @Nullable Uri mediaUri) {
		return _DESC_BUILDER.setExtras(null)
				.setIconUri(null)
				.setDescription(null)
				.setIconBitmap(null)
				.setMediaId(mediaId)
				.setTitle(title)
				.setSubtitle(subtitle)
				.setMediaUri(mediaUri)
				.build();
	}
}
