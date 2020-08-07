package com.muiz6.musicplayer.ui.main.home.query;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.media.MediaRunnable;
import com.muiz6.musicplayer.ui.main.SharedQueryViewModel;
import com.muiz6.musicplayer.ui.main.home.explore.albums.AlbumItemModel;
import com.muiz6.musicplayer.ui.main.home.explore.artists.ArtistItemModel;
import com.muiz6.musicplayer.ui.main.home.explore.artists.ArtistUtil;
import com.muiz6.musicplayer.ui.main.home.explore.songs.SongItemModel;
import com.muiz6.musicplayer.ui.main.home.explore.songs.SongUtil;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class QueryViewModel extends AndroidViewModel {

	private final MutableLiveData<List<AlbumItemModel>> _albumList =
			new MutableLiveData<>(Collections.<AlbumItemModel>emptyList());
	private final MutableLiveData<List<SongItemModel>>_songList =
			new MutableLiveData<>(Collections.<SongItemModel>emptyList());
	private final MutableLiveData<List<ArtistItemModel>> _artistList =
			new MutableLiveData<>(Collections.<ArtistItemModel>emptyList());
	private final MusicRepository _repository;

	@Inject
	public QueryViewModel(Application app, MusicRepository repository) {
		super(app);
		_repository = repository;
	}

	public void executeQuery(Uri queryUri) {
		final String type = queryUri.getQueryParameter(SharedQueryViewModel.KEY_TYPE);
		if (type != null) {
			if (type.equals(SharedQueryViewModel.TYPE_SEARCH)) {
				final String value = queryUri.getQueryParameter(SharedQueryViewModel.KEY_VALUE);
				if (value != null && !value.isEmpty()) {
					// _binding.queryTextView.setText(value);
				}
			}
			else if (type.equals(SharedQueryViewModel.TYPE_ARTIST)) {
				final String value = queryUri.getQueryParameter(SharedQueryViewModel.KEY_VALUE);
				if (value != null && !value.isEmpty()) {
					_onSearchArtist(value);
				}
			}
			else if (type.equals(SharedQueryViewModel.TYPE_GENRE)) {
				final String value = queryUri.getQueryParameter(SharedQueryViewModel.KEY_VALUE);
				if (value != null && !value.isEmpty()) {
					_onSearchGenre(value);
				}
			}
		}
	}

	public LiveData<List<AlbumItemModel>> getAlbumList() {
		return _albumList;
	}

	public LiveData<List<SongItemModel>> getSongList() {
		return _songList;
	}

	public LiveData<List<ArtistItemModel>> getArtistList() {
		return _artistList;
	}

	private void _onSearchArtist(String artist) {
		_repository.searchSongsByArtist(artist, new MediaBrowserCompat.SearchCallback() {

			@Override
			public void onSearchResult(@NonNull String query,
					Bundle extras,
					@NonNull final List<MediaBrowserCompat.MediaItem> items) {
				super.onSearchResult(query, extras, items);
				new Thread(new MediaRunnable(items) {

					@Override
					public void run() {
						_songList.postValue(SongUtil.getSongList(getMediaItemList(),
								getApplication().getApplicationContext()));
					}
				}).start();
			}
		});
	}

	private void _onSearchGenre(final String genre) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				_repository.searchArtistsByGenre(genre, new MediaBrowserCompat.SearchCallback() {

					@Override
					public void onSearchResult(@NonNull String query,
							Bundle extras,
							@NonNull List<MediaBrowserCompat.MediaItem> items) {
						super.onSearchResult(query, extras, items);
						_artistList.postValue(ArtistUtil.getArtistList(items));
					}
				});
				_repository.searchSongsByGenre(genre, new MediaBrowserCompat.SearchCallback() {

					@Override
					public void onSearchResult(@NonNull String query,
							Bundle extras,
							@NonNull List<MediaBrowserCompat.MediaItem> items) {
						super.onSearchResult(query, extras, items);
						_songList.postValue(SongUtil.getSongList(items,
								getApplication().getApplicationContext()));
					}
				});
			}
		}).start();
	}
}
