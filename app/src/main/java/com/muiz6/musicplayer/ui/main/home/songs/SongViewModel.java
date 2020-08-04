package com.muiz6.musicplayer.ui.main.home.songs;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.media.MediaRunnable;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

@FragmentScope
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

					new Thread(new _ProcessDataRunnable(children)).start();
				}
			};
	private final Observer<Boolean> _connectionObserver = new Observer<Boolean>() {

		@Override
		public void onChanged(Boolean bool) {
			if (bool) {
				_connection.subscribe(MusicRepository.MEDIA_ID_ALL_SONGS, _subscriptionCallback);
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
						if (mediaId != null) {

							// set last active item as inactive
							final int oldIndex = _playingItemIndex.getValue();
							if (oldIndex != RecyclerView.NO_POSITION) {
								_songList.getValue().get(oldIndex).setActive(false);
							}
							final int newIndex = MusicRepository.getIndexFromMediaId(mediaId);

							// set new active item as active
							_songList.getValue().get(newIndex).setActive(true);
							_playingItemIndex.postValue(newIndex);
						}
					}
				}
			};
	private final MusicServiceConnection _connection;
	private List<MediaBrowserCompat.MediaItem> _mediaSongList;

	@Inject
	public SongViewModel(Application application, MusicServiceConnection connection) {
		super(application);
		_connection = connection;
		_connection.isConnected().observeForever(_connectionObserver);
	}

	@Override
	protected void onCleared() {
		super.onCleared();

		// remove permanent observers
		_connection.isConnected().removeObserver(_connectionObserver);
		_connection.getMetadata().removeObserver(_metadataObserver);
		_connection.unsubscribe(MusicRepository.MEDIA_ID_ALL_SONGS, _subscriptionCallback);
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

	// to convert media item list to song item model list
	private class _ProcessDataRunnable extends MediaRunnable {

		public _ProcessDataRunnable(@NonNull List<MediaBrowserCompat.MediaItem> mediaItems) {
			super(mediaItems);
		}

		@Override
		public void run() {
			final List<SongItemModel> newSongList = new ArrayList<>();
			final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			for (final MediaBrowserCompat.MediaItem mediaItem : getMediaItemList()) {
				final SongItemModel model = new SongItemModel();
				final MediaDescriptionCompat description = mediaItem.getDescription();
				model.setTitle(String.valueOf(description.getTitle()));
				model.setArtist(String.valueOf(description.getSubtitle()));
				final int duration = description.getExtras()
						.getInt(MediaMetadataCompat.METADATA_KEY_DURATION);
				model.setDuration(duration);

				// retrieve album art and add in model
				retriever.setDataSource(getApplication().getApplicationContext(),
						description.getMediaUri());
				byte[] byteArr = retriever.getEmbeddedPicture();
				if (byteArr != null) {
					final Bitmap bmp = BitmapFactory
							.decodeByteArray(byteArr, 0, byteArr.length);
					model.setAlbumArt(Bitmap
							.createScaledBitmap(bmp, 70, 70, false));
				}
				newSongList.add(model);
			}
			_songList.postValue(newSongList);
		}
	}
}
