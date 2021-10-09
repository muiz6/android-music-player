package com.muiz6.musicplayer.ui.main.home.queue;

import android.support.v4.media.session.MediaSessionCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.media.MusicServiceConnection;
import com.muiz6.musicplayer.media.QueueRunnable;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongItemModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class QueueViewModel extends ViewModel {

	private final MutableLiveData<List<SongItemModel>> _queue =
			new MutableLiveData<>(Collections.<SongItemModel>emptyList());
	private final Observer<List<MediaSessionCompat.QueueItem>> _observerQueue =
			new Observer<List<MediaSessionCompat.QueueItem>>() {

				@Override
				public void onChanged(List<MediaSessionCompat.QueueItem> queueItems) {
					new Thread(new QueueRunnable(queueItems) {

						@Override
						public void run() {
							_queue.postValue(QueueUtil.getQueue(getQueue()));
						}
					}).start();
				}
			};
	private final MusicServiceConnection _connection;

	@Inject
	public QueueViewModel(MusicServiceConnection connection) {
		_connection = connection;
		_connection.getQueue().observeForever(_observerQueue);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		_connection.getQueue().removeObserver(_observerQueue);
	}

	public LiveData<List<SongItemModel>> getQueue() {
		return _queue;
	}
}
