package com.muiz6.musicplayer.media;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

// intermediary class between music service and clients. clients connect to MusicServiceConnetion
// instead of binding to service directly
@Singleton
public class MusicServiceConnection
		implements MediaConnectionCallback.Listener,
		MediaControllerCallback.Listener {

	private static final String _TAG = "MusicServiceConnection";
	private final MutableLiveData<Boolean> _isConnected = new MutableLiveData<>(false);
	private final Context _context;
	private final MediaBrowserCompat _mediaBrowser;
	private final MediaConnectionCallback _connectionCallback;
	private final MediaControllerCallback _controllerCallback;
	private final MutableLiveData<PlaybackStateCompat> _playbackState = new MutableLiveData<>();
	private final MutableLiveData<MediaMetadataCompat> _metadata = new MutableLiveData<>();
	private MediaControllerCompat _mediaController;

	@Inject
	public MusicServiceConnection(@Named("Application") Context context,
			MediaBrowserCompat mediaBrowser,
			MediaConnectionCallback connectionCallback,
			MediaControllerCallback controllerCallback) {
		_context = context;
		_mediaBrowser = mediaBrowser;
		_connectionCallback = connectionCallback;
		_controllerCallback = controllerCallback;
	}

	// di shall call this method right after constructor
	@Inject
	public void prepare() {
		_connectionCallback.addListener(this);
		_controllerCallback.addListener(this);

		// bind to music service
		_mediaBrowser.connect();
	}

	// following methods
	// belong to
	// MediaConnectionCallback.Listener

	@Override
	public void onConnected() {
		try {
			_mediaController = new MediaControllerCompat(_context, _mediaBrowser.getSessionToken());
			_mediaController.registerCallback(_controllerCallback);
		}
		catch (RemoteException e) {
			Log.d(_TAG, "Failed to initialize media controller", e);
		}
		_isConnected.postValue(true);
	}

	@Override
	public void onConnectionFailed() {
		_isConnected.postValue(false);
	}

	@Override
	public void onConnectionSuspended() {
		_isConnected.postValue(false);
	}

	// following methods
	// belong to
	// MediaControllerCallback.Listener

	@Override
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
		_playbackState.postValue(state);
	}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
		_metadata.postValue(metadata);
	}

	@Override
	public void onSessionReady() {}

	@Override
	public void onSessionDestroyed() {
		onConnectionSuspended();
	}

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

	// repeat mode and shuffle mode change can be intercepted by playback state changed

	@Override
	public void onRepeatModeChanged(int repeatMode) {}

	@Override
	public void onShuffleModeChanged(int shuffleMode) {}

	public void subscribe(@NonNull String parentId,
			@NonNull MediaBrowserCompat.SubscriptionCallback callback) {
		_mediaBrowser.subscribe(parentId, callback);
	}

	public void unsubscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback) {
		_mediaBrowser.unsubscribe(parentId, callback);
	}

	public LiveData<Boolean> isConnected() {
		return _isConnected;
	}

	public LiveData<PlaybackStateCompat> getPlaybackState() {
		return _playbackState;
	}

	public LiveData<MediaMetadataCompat> getMetadata() {
		return _metadata;
	}

	@Nullable
	public MediaControllerCompat.TransportControls getTransportControls() {
		if (_mediaController != null) {
			return _mediaController.getTransportControls();
		}
		return null;
	}

	@Nullable
	public MediaControllerCompat getMediaController() {
		return _mediaController;
	}
}
