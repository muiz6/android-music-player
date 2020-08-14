package com.muiz6.musicplayer.ui.main.home.library.albums;

import android.app.Application;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.media.MediaRunnable;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class AlbumViewModel extends AndroidViewModel {

	private final MutableLiveData<List<AlbumItemModel>> _albumList =
			new MutableLiveData<>(Collections.<AlbumItemModel>emptyList());
	private final MediaBrowserCompat.SubscriptionCallback _subscriptionCallback =
			new MediaBrowserCompat.SubscriptionCallback() {

				@Override
				public void onChildrenLoaded(@NonNull String parentId,
						@NonNull final List<MediaBrowserCompat.MediaItem> children) {
					super.onChildrenLoaded(parentId, children);
					_albumMediaList = children;
					new Thread(new MediaRunnable(children) {

						@Override
						public void run() {
							_albumList.postValue(AlbumUtil.getAlbumList(getMediaItemList(),
									getApplication().getApplicationContext()));
						}
					}).start();
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
	private List<MediaBrowserCompat.MediaItem> _albumMediaList;

	@Inject
	public AlbumViewModel(Application application, MusicServiceConnection connection) {
		super(application);
		_connection = connection;
		_connection.isConnected().observeForever(_connectionObserver);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		_connection.unsubscribe(MusicRepository.MEDIA_ID_ALBUMS, _subscriptionCallback);
		_connection.isConnected().removeObserver(_connectionObserver);
	}

	public LiveData<List<AlbumItemModel>> getAlbumList() {
		return _albumList;
	}

	public String getAlbumMediaId(int index) {
		return _albumMediaList.get(index).getMediaId();
	}

	public String getAlbumTitle(int index) {
		return String.valueOf(_albumMediaList.get(index).getDescription().getTitle());
	}
}
