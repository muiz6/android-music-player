package com.muiz6.musicplayer.data;

import android.support.v4.media.MediaBrowserCompat;

import androidx.lifecycle.Observer;

import java.util.List;
import java.util.Set;

public class DataObserver implements Observer<List<MediaBrowserCompat.MediaItem>> {

	private final Set<MusicRepository.Listener> _listenerSet;
	private final String _parentMediaId;

	public DataObserver(String parentMediaId, Set<MusicRepository.Listener> listenerSet) {
		_listenerSet = listenerSet;
		_parentMediaId = parentMediaId;
	}

	@Override
	public void onChanged(List<MediaBrowserCompat.MediaItem> mediaItem) {
		for (MusicRepository.Listener listener : _listenerSet) {
			listener.onChildrenChanged(_parentMediaId);
		}
	}
}
