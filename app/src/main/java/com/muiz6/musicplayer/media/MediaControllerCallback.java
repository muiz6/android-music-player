package com.muiz6.musicplayer.media;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.muiz6.musicplayer.di.scope.FragmentScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Wrapper class for MediaControllerCompat.Callback so that activity can implement
 * methods of MediaControllerCallback.Listener instead of using Callback as inner class
 */
@FragmentScope
public class MediaControllerCallback extends MediaControllerCompat.Callback {

	private final ArrayList<Listener> _listeners = new ArrayList<>();

	@Inject
	public MediaControllerCallback() {}

	@Override
	public void onPlaybackStateChanged(PlaybackStateCompat state) {
		super.onPlaybackStateChanged(state);

		for (final Listener listener : _listeners) {
			listener.onPlaybackStateChanged(state);
		}
	}

	@Override
	public void onMetadataChanged(MediaMetadataCompat metadata) {
		super.onMetadataChanged(metadata);

		for (final Listener listener : _listeners) {
			listener.onMetadataChanged(metadata);
		}
	}

	@Override
	public void onSessionReady() {
		super.onSessionReady();

		for (final Listener listener : _listeners) {
			listener.onSessionReady();
		}
	}

	@Override
	public void onSessionDestroyed() {
		super.onSessionDestroyed();

		for (final Listener listener : _listeners) {
			listener.onSessionDestroyed();
		}
	}

	@Override
	public void onSessionEvent(String event, Bundle extras) {
		super.onSessionEvent(event, extras);

		for (final Listener listener : _listeners) {
			listener.onSessionEvent(event, extras);
		}
	}

	@Override
	public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
		super.onQueueChanged(queue);

		for (final Listener listener : _listeners) {
			listener.onQueueChanged(queue);
		}
	}

	@Override
	public void onQueueTitleChanged(CharSequence title) {
		super.onQueueTitleChanged(title);

		for (final Listener listener : _listeners) {
			listener.onQueueTitleChanged(title);
		}
	}

	@Override
	public void onExtrasChanged(Bundle extras) {
		super.onExtrasChanged(extras);

		for (final Listener listener : _listeners) {
			listener.onExtrasChanged(extras);
		}
	}

	@Override
	public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {
		super.onAudioInfoChanged(info);

		for (final Listener listener : _listeners) {
			listener.onAudioInfoChanged(info);
		}
	}

	@Override
	public void onCaptioningEnabledChanged(boolean enabled) {
		super.onCaptioningEnabledChanged(enabled);

		for (final Listener listener : _listeners) {
			listener.onCaptioningEnabledChanged(enabled);
		}
	}

	@Override
	public void onRepeatModeChanged(int repeatMode) {
		super.onRepeatModeChanged(repeatMode);

		for (final Listener listener : _listeners) {
			listener.onRepeatModeChanged(repeatMode);
		}
	}

	@Override
	public void onShuffleModeChanged(int shuffleMode) {
		super.onShuffleModeChanged(shuffleMode);

		for (final Listener listener : _listeners) {
			listener.onShuffleModeChanged(shuffleMode);
		}
	}

	public void addListener(@NonNull Listener listener) {
		_listeners.add(listener);
	}

	public interface Listener {
		void onPlaybackStateChanged(@Nullable PlaybackStateCompat state);

		void onMetadataChanged(@Nullable MediaMetadataCompat metadata);

		void onSessionReady();

		void onSessionDestroyed();

		void onSessionEvent(String event, Bundle extras);

		void onQueueChanged(List<MediaSessionCompat.QueueItem> queue);

		void onQueueTitleChanged(CharSequence title);

		void onExtrasChanged(Bundle extras);

		void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info);

		void onCaptioningEnabledChanged(boolean enabled);

		void onRepeatModeChanged(int repeatMode);

		void onShuffleModeChanged(int shuffleMode);
	}
}
