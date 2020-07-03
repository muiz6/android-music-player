package com.muiz6.musicplayer.ui;

import android.app.Activity;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;

public abstract class MyConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

	private final MediaControllerCompat.Callback _controllerCallback;
	private final Activity _activity;
	private MediaBrowserCompat _mediaBrowser;

	/**
	 *
	 * @param activity activity to register the media controller to
	 * @param controllerCallback callback required to keep in sync with media session
	 */
	public MyConnectionCallback(@NonNull Activity activity,
			@NonNull MediaControllerCompat.Callback controllerCallback) {
		_activity = activity;
		_controllerCallback = controllerCallback;
	}

	@Override
	public void onConnected() {
		super.onConnected();

		// Get the token for the MediaSession
		// make sure this.setMediaBrowser() is called in activity
		final MediaSessionCompat.Token token = _mediaBrowser.getSessionToken();

		// Create a MediaController
		try {
			MediaControllerCompat mediaController =
					new MediaControllerCompat(_activity, token);

			// Save the controller for using anywhere
			MediaControllerCompat.setMediaController(_activity, mediaController);

			// Register a Callback to stay in sync
			mediaController.registerCallback(_controllerCallback);
		}
		catch (RemoteException e) {
			Log.e("MyConnCallback", "Error creating controller", e);
		}
	}

	/**
	 * Must be called before MediaBrowserCompat.connect()
	 * @param mediaBrowser media browser of the activity
	 */
	public void setMediaBrowser(@NonNull MediaBrowserCompat mediaBrowser) {
		_mediaBrowser = mediaBrowser;
	}
}
