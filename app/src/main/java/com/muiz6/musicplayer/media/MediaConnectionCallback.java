package com.muiz6.musicplayer.media;

import android.support.v4.media.MediaBrowserCompat;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Wrapper class for MediaBrowserCompat.ConnectionCallback so that activity can implement
 * methods of MediaConnectionCallback.Listener instead of using ConnectionCallback as inner class.
 */
@Singleton
public class MediaConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

	private final Set<Listener> _listeners = new HashSet<>();

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

	public void removeListener(Listener listener) {
		_listeners.remove(listener);
	}

	public void removeAllListeners() {
		_listeners.clear();
	}

	public interface Listener {
		void onConnected();

		void onConnectionFailed();

		void onConnectionSuspended();
	}
}
