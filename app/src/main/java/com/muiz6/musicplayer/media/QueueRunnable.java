package com.muiz6.musicplayer.media;

import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class QueueRunnable implements Runnable {

	private final List<MediaSessionCompat.QueueItem> _playQueue;

	public QueueRunnable(@NonNull List<MediaSessionCompat.QueueItem> queue) {
		_playQueue = queue;
	}

	public List<MediaSessionCompat.QueueItem> getQueue() {
		return _playQueue;
	}
}
