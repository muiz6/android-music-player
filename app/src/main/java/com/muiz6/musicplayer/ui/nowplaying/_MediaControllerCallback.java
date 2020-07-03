package com.muiz6.musicplayer.ui.nowplaying;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

class _MediaControllerCallback extends MediaControllerCompat.Callback {

	private final NowPlayingActivity _activity;

	public _MediaControllerCallback(NowPlayingActivity activity) {
		_activity = activity;
	}

	@Override
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
		super.onPlaybackStateChanged(state);
	}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
		super.onMetadataChanged(metadata);
	}
}
