package com.muiz6.musicplayer.ui.main.home.library.songs;

import android.app.Application;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.media.MediaRunnable;
import com.muiz6.musicplayer.media.MusicServiceConnection;
import com.muiz6.musicplayer.permission.PermissionManager;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class SongViewModel extends AndroidViewModel {

	private final MutableLiveData<Integer> _playingItemIndex =
			new MutableLiveData<>(RecyclerView.NO_POSITION);
	private final MutableLiveData<List<SongItemModel>> _songList =
			new MutableLiveData<>(Collections.<SongItemModel>emptyList());
	private MediaBrowserCompat.SubscriptionCallback _subscriptionCallback =
			new MediaBrowserCompat.SubscriptionCallback() {

				@Override
				public void onChildrenLoaded(@NonNull String parentId,
						@NonNull List<MediaBrowserCompat.MediaItem> children) {
					super.onChildrenLoaded(parentId, children);

					// for use with onSongItemClicked()
					_mediaSongList = children;

					new Thread(new MediaRunnable(children) {

						@Override
						public void run() {
							SongUtil.getSongList(getMediaItemList(),
									getApplication().getApplicationContext(),
									_permissionManager,
									new SongUtil.Callback() {

										@Override
										public void onResult(List<SongItemModel> resultList) {
											_songList.postValue(resultList);
										}
									});
						}
					}).start();
				}
			};
	private final Observer<Boolean> _connectionObserver = new Observer<Boolean>() {

		@Override
		public void onChanged(Boolean bool) {
			if (bool) {
				_connection.subscribe(MusicRepository.MEDIA_ID_SONGS, _subscriptionCallback);
				if (_connection.getMetadata() != null) {
					_connection.getMetadata().observeForever(_metadataObserver);
				}
			}
		}
	};
	private final Observer<MediaMetadataCompat> _metadataObserver =
			new Observer<MediaMetadataCompat>() {

				@Override
				public void onChanged(MediaMetadataCompat mediaMetadataCompat) {
					if (mediaMetadataCompat != null) {
						final String mediaId = mediaMetadataCompat.getDescription().getMediaId();
						// if (mediaId != null) {
						//
						// 	// set last active item as inactive
						// 	// final int oldIndex = _playingItemIndex.getValue();
						// 	// if (oldIndex != RecyclerView.NO_POSITION) {
						// 	// 	_songList.getValue().get(oldIndex).setActive(false);
						// 	// }
						// 	// final int newIndex = MusicRepository.getIndexFromMediaId(mediaId);
						//
						// 	// set new active item as active
						// 	// _songList.getValue().get(newIndex).setActive(true);
						// 	// _playingItemIndex.postValue(newIndex);
						// }
					}
				}
			};
	private final MusicServiceConnection _connection;
	private final PermissionManager _permissionManager;
	private List<MediaBrowserCompat.MediaItem> _mediaSongList;

	@Inject
	public SongViewModel(Application application,
			MusicServiceConnection connection,
			PermissionManager permissionManager) {
		super(application);
		_connection = connection;
		_permissionManager = permissionManager;
		_connection.isConnected().observeForever(_connectionObserver);
	}

	@Override
	protected void onCleared() {
		super.onCleared();

		// remove permanent observers
		_connection.isConnected().removeObserver(_connectionObserver);
		_connection.getMetadata().removeObserver(_metadataObserver);
		_connection.unsubscribe(MusicRepository.MEDIA_ID_SONGS, _subscriptionCallback);
	}

	public LiveData<List<SongItemModel>> getSongList() {
		return _songList;
	}

	public void songItemClicked(int index) {
		final String mediaId = _mediaSongList.get(index).getMediaId();
		_connection.getTransportControls().playFromMediaId(mediaId, null);
	}

	public LiveData<Integer> getPlayingItemIndex() {
		return _playingItemIndex;
	}
}
