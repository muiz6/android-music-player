package com.muiz6.musicplayer.ui;

import android.app.Activity;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

	private static final String _TAG = "MyConnectionCallback";
	private final Listener _listener;

	public MyConnectionCallback(@NonNull MyConnectionCallback.Listener listener) {
		_listener = listener;
	}

	@Override
	public void onConnected() {
		super.onConnected();

		// Get the token for the MediaSession
		final MediaSessionCompat.Token token = _listener.getMediaBrowser().getSessionToken();

		// Create a MediaController
		try {
			MediaControllerCompat mediaController =
					new MediaControllerCompat(_listener.getActivity(), token);

			// Save the controller for using anywhere
			MediaControllerCompat.setMediaController(_listener.getActivity(), mediaController);

			// Register a Callback (if provided) to stay in sync
			final MediaControllerCompat.Callback callback = _listener.getMediaControllerCallback();
			if (callback != null) {
				mediaController.registerCallback(callback);
			}
		}
		catch (RemoteException e) {
			Log.e("MyConnCallback", "Error creating controller", e);
		}

		_listener.onConnected();
	}

	@Override
	public void onConnectionFailed() {
		super.onConnectionFailed();

		Log.d(_TAG, "Connection Failed");
	}

	@Override
	public void onConnectionSuspended() {
		super.onConnectionSuspended();

		Log.d(_TAG, "Connection Suspended");
	}

	public interface Listener extends MediaBrowserProvider{
		void onConnected();

		@NonNull
		Activity getActivity();

		@Nullable
		MediaControllerCompat.Callback getMediaControllerCallback();
	}
}
