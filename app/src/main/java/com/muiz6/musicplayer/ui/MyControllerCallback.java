package com.muiz6.musicplayer.ui;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

public class MyControllerCallback extends MediaControllerCompat.Callback {

	private Listener _listener;

	public MyControllerCallback(Listener listener) {
		_listener = listener;
	}

	@Override
	public void onPlaybackStateChanged(PlaybackStateCompat state) {
		super.onPlaybackStateChanged(state);

		_listener.onPlaybackStateChanged(state);
	}

	@Override
	public void onMetadataChanged(MediaMetadataCompat metadata) {
		super.onMetadataChanged(metadata);

		_listener.onMetadataChanged(metadata);
	}

	public interface Listener {
		void onPlaybackStateChanged(@Nullable PlaybackStateCompat state);
		void onMetadataChanged(@Nullable MediaMetadataCompat metadata);
	}
}
