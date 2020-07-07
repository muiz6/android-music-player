package com.muiz6.musicplayer.ui;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;

public interface MediaBrowserProvider {

	@NonNull
	MediaBrowserCompat getMediaBrowser();
}
