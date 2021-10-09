package com.muiz6.musicplayer.media;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class MediaRunnable implements Runnable{

	private final List<MediaBrowserCompat.MediaItem> _mediaItems;

	public MediaRunnable(@NonNull List<MediaBrowserCompat.MediaItem> mediaItems) {
		_mediaItems = mediaItems;
	}

	@NonNull
	public List<MediaBrowserCompat.MediaItem> getMediaItemList() {
		return _mediaItems;
	}
}
