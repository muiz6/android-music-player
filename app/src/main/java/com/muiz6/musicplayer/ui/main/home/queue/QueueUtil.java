package com.muiz6.musicplayer.ui.main.home.queue;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.muiz6.musicplayer.ui.main.home.library.songs.SongItemModel;

import java.util.ArrayList;
import java.util.List;

public abstract class QueueUtil {

	public static List<SongItemModel> getQueue(List<MediaSessionCompat.QueueItem> queue) {
		final List<SongItemModel> newQueue = new ArrayList<>();
		for (MediaSessionCompat.QueueItem queueItem : queue) {
			final SongItemModel model = new SongItemModel();
			final MediaDescriptionCompat description = queueItem.getDescription();
			// final Bundle extras = description.getExtras();
			model.setTitle(String.valueOf(description.getTitle()));
			model.setArtist(String.valueOf(description.getSubtitle()));
			newQueue.add(model);
		}
		return newQueue;
	}
}
