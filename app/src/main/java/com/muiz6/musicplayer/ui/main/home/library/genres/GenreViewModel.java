package com.muiz6.musicplayer.ui.main.home.library.genres;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.media.MediaRunnable;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GenreViewModel extends ViewModel {

	private final MutableLiveData<List<GenreItemModel>> _genreList =
			new MutableLiveData<>(Collections.<GenreItemModel>emptyList());
	private final MediaBrowserCompat.SubscriptionCallback _subscriptionCallback =
			new MediaBrowserCompat.SubscriptionCallback() {

				@Override
				public void onChildrenLoaded(@NonNull String parentId,
						@NonNull List<MediaBrowserCompat.MediaItem> children) {
					super.onChildrenLoaded(parentId, children);
					_genreMediaList = children;
					new Thread(new MediaRunnable(children) {

						@Override
						public void run() {
							_genreList.postValue(GenreUtil.getGenreList(getMediaItemList()));
						}
					}).start();
				}
			};
	private final Observer<Boolean> _connectionObserver = new Observer<Boolean>() {

		@Override
		public void onChanged(Boolean state) {
			if (state) {
				_connection.subscribe(MusicRepository.MEDIA_ID_GENRES, _subscriptionCallback);
			}
		}
	};
	private final MusicServiceConnection _connection;
	private List<MediaBrowserCompat.MediaItem> _genreMediaList;

	@Inject
	public GenreViewModel(MusicServiceConnection connection) {
		_connection = connection;
		_connection.isConnected().observeForever(_connectionObserver);
	}

	@Override
	protected void onCleared() {
		super.onCleared();

		_connection.isConnected().removeObserver(_connectionObserver);
		_connection.unsubscribe(MusicRepository.MEDIA_ID_GENRES, _subscriptionCallback);
	}

	public LiveData<List<GenreItemModel>> getGenreList() {
		return _genreList;
	}

	public String getGenreId(int index) {
		return _genreMediaList.get(index).getMediaId();
	}
}
