package com.muiz6.musicplayer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.muiz6.musicplayer.media.MediaConnectionCallback;
import com.muiz6.musicplayer.media.MediaControllerCallback;

import java.util.List;

public abstract class MediaBrowserFragment extends Fragment
	implements MediaConnectionCallback.Listener,
	MediaControllerCallback.Listener {

	private static final String _TAG = "MediaBrowserFragment";
	private final MediaBrowserCompat _mediaBrowser;
	private final MediaControllerCallback _mediaControllerCallback;
	private final MediaConnectionCallback _mediaConnectionCallback;
	private MediaControllerCompat _mediaController; // only available at runtime

	// abstract fragment does not need a factory
	public MediaBrowserFragment(MediaBrowserCompat mediaBrowser,
			MediaConnectionCallback connectionCallback,
			MediaControllerCallback controllerCallback) {
		_mediaBrowser = mediaBrowser;
		_mediaConnectionCallback = connectionCallback;
		_mediaControllerCallback = controllerCallback;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		_mediaConnectionCallback.addListener(this);
		_mediaControllerCallback.addListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		_mediaBrowser.connect();
	}

	@Override
	public void onStop() {
		super.onStop();

		// stay in sync with the MediaSession
		if (_mediaController != null) {
			_mediaController.unregisterCallback(_mediaControllerCallback);
		}
		_mediaBrowser.disconnect();
	}

	// following methods
	// belong to
	// MediaConnectionCallback.Listener

	@Override
	public void onConnected() {
		try {
			_mediaController = new MediaControllerCompat(getContext(),
					_mediaBrowser.getSessionToken());
			_mediaController.registerCallback(_mediaControllerCallback);
		}
		catch (RemoteException e) {
			Log.d(_TAG, "Failed to initialize media controller", e);
		}
	}

	@Override
	public void onConnectionSuspended() {
		Log.d(_TAG, "Connection suspended");
	}

	@Override
	public void onConnectionFailed() {
		Log.d(_TAG, "Connection failed");
	}

	// following methods
	// belong to
	// MediaControllerCallback.Listener
	// implemented all here so sub classes can implement only needed callbacks

	@Override
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {}

	@Override
	public void onSessionReady() {}

	@Override
	public void onSessionDestroyed() {}

	@Override
	public void onSessionEvent(String event, Bundle extras) {}

	@Override
	public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {}

	@Override
	public void onQueueTitleChanged(CharSequence title) {}

	@Override
	public void onExtrasChanged(Bundle extras) {}

	@Override
	public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {}

	@Override
	public void onCaptioningEnabledChanged(boolean enabled) {}

	@Override
	public void onRepeatModeChanged(int repeatMode) {}

	@Override
	public void onShuffleModeChanged(int shuffleMode) {}

	@Nullable
	public MediaControllerCompat getMediaController() {
		return _mediaController;
	}
}
