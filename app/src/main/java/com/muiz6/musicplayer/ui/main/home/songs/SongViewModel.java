package com.muiz6.musicplayer.ui.main.home.songs;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.media.MusicServiceConnection;
import com.muiz6.musicplayer.musicprovider.MusicProvider;

import java.util.List;

import javax.inject.Inject;

@FragmentScope
public class SongViewModel extends ViewModel {

	public static final int NOTHING_PLAYING = -1;

	private final MutableLiveData<List<MediaBrowserCompat.MediaItem>> _songList =
			new MutableLiveData<>();
	private MediaBrowserCompat.SubscriptionCallback _subscriptionCallback =
			new MediaBrowserCompat.SubscriptionCallback() {

				@Override
				public void onChildrenLoaded(@NonNull String parentId,
						@NonNull List<MediaBrowserCompat.MediaItem> children) {
					super.onChildrenLoaded(parentId, children);

					_songList.postValue(children);
				}
			};
	private final Observer<Boolean> _connectionObserver = new Observer<Boolean>() {

		@Override
		public void onChanged(Boolean bool) {
			if (bool) {
				_connection.subscribe(MusicProvider.MEDIA_ID_ALL_SONGS, _subscriptionCallback);
				// if (_connection.getMetadata() != null) {
				// 	_connection.getMetadata().observeForever(_metadataObserver);
				// }
			}
		}
	};
	// private final Observer<MediaMetadataCompat> _metadataObserver =
	// 		new Observer<MediaMetadataCompat>() {
	//
	// 			@Override
	// 			public void onChanged(MediaMetadataCompat mediaMetadataCompat) {
	// 				if (mediaMetadataCompat != null) {
	// 					final String mediaId = mediaMetadataCompat.getDescription().getMediaId();
	// 					if (mediaId != null) {
	// 						final String subStr = mediaId.substring(mediaId.lastIndexOf('.') + 1);
	// 						int index = Integer.parseInt(subStr);
	// 						_playingItemIndex.postValue(index);
	// 					}
	// 				}
	// 			}
	// 		};
	// private MutableLiveData<Integer> _playingItemIndex = new MutableLiveData<>(NOTHING_PLAYING);
	private final MusicServiceConnection _connection;

	@Inject
	public SongViewModel(MusicServiceConnection connection) {
		_connection = connection;
		_connection.isConnected().observeForever(_connectionObserver);
	}

	@Override
	protected void onCleared() {
		super.onCleared();

		// remove permanent observers
		_connection.isConnected().removeObserver(_connectionObserver);
		// _connection.getMetadata().removeObserver(_metadataObserver);
		_connection.unsubscribe(MusicProvider.MEDIA_ID_ALL_SONGS, _subscriptionCallback);
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getSongList() {
		return _songList;
	}

	public void songItemClicked(int index) {
		final String mediaId = _songList.getValue().get(index).getMediaId();
		_connection.getTransportControls().playFromMediaId(mediaId, null);
	}

	// public LiveData<Integer> getPlayingItemIndex() {
	// 	return _playingItemIndex;
	// }
}
