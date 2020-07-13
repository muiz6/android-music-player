package com.muiz6.musicplayer.media;

import android.support.v4.media.MediaBrowserCompat;

import com.muiz6.musicplayer.di.scope.FragmentScope;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Wrapper class for MediaBrowserCompat.ConnectionCallback so that activity can implement
 * methods of MediaConnectionCallback.Listener instead of using ConnectionCallback as inner class.
 */
@FragmentScope
public class MediaConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

	private final ArrayList<Listener> _listeners = new ArrayList<>();

	@Inject
	public MediaConnectionCallback() {}

	@Override
	public void onConnected() {
		super.onConnected();

		for (final Listener listener : _listeners) {
			listener.onConnected();
		}
	}

	@Override
	public void onConnectionFailed() {
		super.onConnectionFailed();

		for (final Listener listener : _listeners) {
			listener.onConnectionFailed();
		}
	}

	@Override
	public void onConnectionSuspended() {
		super.onConnectionSuspended();

		for (final Listener listener : _listeners) {
			listener.onConnectionSuspended();
		}
	}

	public void addListener(Listener listener) {
		_listeners.add(listener);
	}

	public interface Listener {
		void onConnected();

		void onConnectionFailed();

		void onConnectionSuspended();
	}
}
