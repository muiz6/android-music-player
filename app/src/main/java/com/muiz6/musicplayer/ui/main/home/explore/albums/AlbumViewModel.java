package com.muiz6.musicplayer.ui.main.home.explore.albums;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class AlbumViewModel extends ViewModel {

	private final MutableLiveData<List<MediaBrowserCompat.MediaItem>> _albumList =
			new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
	private final MediaBrowserCompat.SubscriptionCallback _subscriptionCallback =
			new MediaBrowserCompat.SubscriptionCallback() {

				@Override
				public void onChildrenLoaded(@NonNull String parentId,
						@NonNull List<MediaBrowserCompat.MediaItem> children) {
					super.onChildrenLoaded(parentId, children);

					_albumList.setValue(children);
				}
			};
	private final Observer<Boolean> _connectionObserver =
			new Observer<Boolean>() {

				@Override
				public void onChanged(Boolean state) {
					if (state) {
						_connection.subscribe(MusicRepository.MEDIA_ID_ALBUMS, _subscriptionCallback);
					}
				}
			};
	private final MusicServiceConnection _connection;

	@Inject
	public AlbumViewModel(MusicServiceConnection connection) {
		_connection = connection;
		_connection.isConnected().observeForever(_connectionObserver);
	}

	@Override
	protected void onCleared() {
		super.onCleared();

		_connection.unsubscribe(MusicRepository.MEDIA_ID_ALBUMS, _subscriptionCallback);
		_connection.isConnected().removeObserver(_connectionObserver);
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getAlbumList() {
		return _albumList;
	}
}
