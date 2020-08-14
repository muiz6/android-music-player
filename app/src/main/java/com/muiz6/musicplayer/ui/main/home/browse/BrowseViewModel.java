package com.muiz6.musicplayer.ui.main.home.browse;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.muiz6.musicplayer.data.MediaIdPojo;
import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.media.MediaRunnable;
import com.muiz6.musicplayer.media.MusicServiceConnection;
import com.muiz6.musicplayer.permission.PermissionManager;
import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumItemModel;
import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumUtil;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongItemModel;
import com.muiz6.musicplayer.ui.main.home.library.songs.SongUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class BrowseViewModel extends AndroidViewModel {

	private final MutableLiveData<List<AlbumItemModel>> _albumList =
			new MutableLiveData<>(Collections.<AlbumItemModel>emptyList());
	private final MutableLiveData<List<SongItemModel>> _songList =
			new MutableLiveData<>(Collections.<SongItemModel>emptyList());
	private final MutableLiveData<String> _fragmentTitle = new MutableLiveData<>();
	private final Observer<Boolean> _connectionObserver = new Observer<Boolean>() {

		@Override
		public void onChanged(Boolean aBoolean) {
			if (aBoolean) {
				_connection.subscribe(_parentMediaId, _subscriptionCallback);
			}
		}
	};
	private final MediaBrowserCompat.SubscriptionCallback _subscriptionCallback =
			new MediaBrowserCompat.SubscriptionCallback() {

				@Override
				public void onChildrenLoaded(@NonNull String parentId,
						@NonNull List<MediaBrowserCompat.MediaItem> children) {
					super.onChildrenLoaded(parentId, children);
					new Thread(new MediaRunnable(children) {

						@Override
						public void run() {
							final List<MediaBrowserCompat.MediaItem> albumList = new ArrayList<>();
							final List<MediaBrowserCompat.MediaItem> songList = new ArrayList<>();
							for (final MediaBrowserCompat.MediaItem item : getMediaItemList()) {
								final Bundle extras = item.getDescription().getExtras();
								final String type = extras
										.getString(MusicRepository.KEY_EXTRAS_MEDIA_CATEGORY);
								if (type.equals(MediaIdPojo.CATEGORY_ALBUM)) {
									albumList.add(item);
								}
								else if (type.equals(MediaIdPojo.CATEGORY_SONG)) {
									songList.add(item);
								}
								// else ignore
							}
							_albumMediaList = albumList;
							_songMediaList = songList;
							_albumList.postValue(AlbumUtil.getAlbumList(albumList,
									getApplication().getApplicationContext()));
							_songList.postValue(SongUtil.getSongList(songList,
									getApplication().getApplicationContext(),
									_permissionManager));
						}
					}).start();
				}
			};
	private final MusicServiceConnection _connection;
	private final PermissionManager _permissionManager;
	private List<MediaBrowserCompat.MediaItem> _albumMediaList;
	private List<MediaBrowserCompat.MediaItem> _songMediaList;
	private String _parentMediaId;

	@Inject
	public BrowseViewModel(@NonNull Application application,
			MusicServiceConnection connection,
			PermissionManager permissionManager) {
		super(application);
		_permissionManager = permissionManager;
		_connection = connection;
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		_connection.unsubscribe(_parentMediaId, _subscriptionCallback);
		_connection.isConnected().removeObserver(_connectionObserver);
	}

	public void setParentMediaId(String parentMediaId) {
		if (!parentMediaId.equals(_parentMediaId)) {
			_parentMediaId = parentMediaId;
			_fragmentTitle.postValue(parentMediaId);
			_connection.isConnected().observeForever(_connectionObserver);
		}
	}

	public LiveData<List<AlbumItemModel>> getAlbumList() {
		return _albumList;
	}

	public LiveData<List<SongItemModel>> getSongList() {
		return _songList;
	}

	public String getAlbumMediaId(int index) {
		return _albumMediaList.get(index).getMediaId();
	}

	public void onSongItemClicked(int index) {
		_connection.getTransportControls()
				.playFromMediaId(_songMediaList.get(index).getMediaId(), null);
	}

	public String getAlbumTitle(int index) {
		return String.valueOf(_albumMediaList.get(index).getDescription().getTitle());
	}
}
