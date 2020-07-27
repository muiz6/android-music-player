package com.muiz6.musicplayer.ui.main.home.songs;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

@FragmentScope
public class SongViewModel extends ViewModel {

	public static final int NOTHING_PLAYING = -1;

	private final MutableLiveData<List<SongItemModel>> _songList =
			new MutableLiveData<>(Collections.<SongItemModel>emptyList());
	private List<MediaBrowserCompat.MediaItem> _mediaSongList;
	private MediaBrowserCompat.SubscriptionCallback _subscriptionCallback =
			new MediaBrowserCompat.SubscriptionCallback() {

				@Override
				public void onChildrenLoaded(@NonNull String parentId,
						@NonNull List<MediaBrowserCompat.MediaItem> children) {
					super.onChildrenLoaded(parentId, children);

					// for use with onSongItemClicked()
					_mediaSongList = children;

					final List<SongItemModel> newSongList = new ArrayList<>();
					for (final MediaBrowserCompat.MediaItem mediaItem : children) {
						final SongItemModel model = new SongItemModel();
						final MediaDescriptionCompat description = mediaItem.getDescription();
						model.setTitle(String.valueOf(description.getTitle()));
						model.setArtist(String.valueOf(description.getSubtitle()));
						newSongList.add(model);
					}
					_songList.postValue(newSongList);
				}
			};
	private final Observer<Boolean> _connectionObserver = new Observer<Boolean>() {

		@Override
		public void onChanged(Boolean bool) {
			if (bool) {
				_connection.subscribe(MusicRepository.MEDIA_ID_ALL_SONGS, _subscriptionCallback);
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
		_connection.unsubscribe(MusicRepository.MEDIA_ID_ALL_SONGS, _subscriptionCallback);
	}

	public LiveData<List<SongItemModel>> getSongList() {
		return _songList;
	}

	public void songItemClicked(int index) {
		final String mediaId = _mediaSongList.get(index).getMediaId();
		_connection.getTransportControls().playFromMediaId(mediaId, null);
	}

	// public LiveData<Integer> getPlayingItemIndex() {
	// 	return _playingItemIndex;
	// }
}
