package com.muiz6.musicplayer.data;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.muiz6.musicplayer.data.db.AlbumPojo;
import com.muiz6.musicplayer.data.db.ArtistPojo;
import com.muiz6.musicplayer.data.db.AudioDao;
import com.muiz6.musicplayer.data.db.AudioDatabase;
import com.muiz6.musicplayer.data.db.GenrePojo;
import com.muiz6.musicplayer.data.db.SongPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A class to convert room data into media session compatible media item
 */
@Singleton
public class RoomMediator {

	private MutableLiveData<List<MediaBrowserCompat.MediaItem>> _allSongList =
			new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
	private MutableLiveData<List<MediaBrowserCompat.MediaItem>> _albumList =
			new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
	private MutableLiveData<List<MediaBrowserCompat.MediaItem>> _artistList =
			new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
	private MutableLiveData<List<MediaBrowserCompat.MediaItem>> _genreList =
			new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
	private final Observer<List<SongPojo>> _dbSongListObserver =
			new Observer<List<SongPojo>>() {

				@Override
				public void onChanged(List<SongPojo> audioEntities) {
					if (audioEntities != null) {
						_convertSong(audioEntities);
					}
				}
			};
	private final Observer<List<AlbumPojo>> _dbAlbumListObserver =
			new Observer<List<AlbumPojo>>() {

				@Override
				public void onChanged(List<AlbumPojo> audioEntities) {
					if (audioEntities != null) {
						_convertAlbum(audioEntities);
					}
				}
			};
	private final Observer<List<ArtistPojo>> _dbArtistListObserver =
			new Observer<List<ArtistPojo>>() {

				@Override
				public void onChanged(List<ArtistPojo> artistPojos) {
					if (artistPojos != null) {
						_convertArtist(artistPojos);
					}
				}
			};
	private final Observer<List<GenrePojo>> _dbGenreListObserver =
			new Observer<List<GenrePojo>>() {

				@Override
				public void onChanged(List<GenrePojo> genrePojos) {
					if (genrePojos != null) {
						_convertGenre(genrePojos);
					}
				}
			};
	private final LiveData<List<SongPojo>> _dbAllSongList;
	private final LiveData<List<AlbumPojo>> _dbAlbumList;
	private final LiveData<List<ArtistPojo>> _dbArtistList;
	private final LiveData<List<GenrePojo>> _dbGenreList;

	@Inject
	public RoomMediator(AudioDatabase db) {
		final AudioDao dao = db.getAudioDao();
		_dbAllSongList = dao.getAllSongList();
		_dbAlbumList = dao.getAlbumList();
		_dbArtistList = dao.getArtistList();
		_dbGenreList = dao.getGenreList();

		// todo: this throws NPE for some reason
		// make sure data is loaded atleast once if there is no change
		// _convertSong(_dbAllSongList.getValue());
		// _convertAlbum(_dbAlbumList.getValue());
		// _convertArtist(_dbArtistList.getValue());
		// _convertGenre(_dbGenreList.getValue());

		// todo: remove observer when not needed to improve performance
		_dbAllSongList.observeForever(_dbSongListObserver);
		_dbAlbumList.observeForever(_dbAlbumListObserver);
		_dbArtistList.observeForever(_dbArtistListObserver);
		_dbGenreList.observeForever(_dbGenreListObserver);
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getAllSongList() {
		return _allSongList;
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getAlbumList() {
		return _albumList;
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getArtistList() {
		return _artistList;
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getGenreList() {
		return _genreList;
	}

	private void _convertSong(final List<SongPojo> list) {

		// process in bg thread for performance
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				int i = 0;
				for (final SongPojo audio : list) {
					final Bundle extras = new Bundle();
					extras.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
							audio.getTitle());
					extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
					final MediaDescriptionCompat description = builder
							.setMediaId(MusicRepository.MEDIA_ID_ALL_SONGS
									+ MusicRepository.SEPARATOR_MEDIA_ID
									+ i++)
							.setTitle(audio.getDisplayName())
							.setSubtitle(audio.getArtist())
							.setExtras(extras)
							.build();
					final MediaBrowserCompat.MediaItem mediaItem =
							new MediaBrowserCompat.MediaItem(description,
									MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
					newList.add(mediaItem);
				}
				_allSongList.postValue(newList);
			}
		}).start();
	}

	private void _convertAlbum(final List<AlbumPojo> list) {

		// process in bg thread for performance
		new Thread(new Runnable() {

			@Override
			public void run() {
			    final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				int i = 0;
				for (final AlbumPojo audio : list) {
					final Bundle extras = new Bundle();
					extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audio.getAlbum());
					extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
					final MediaDescriptionCompat description = builder
							.setMediaId(MusicRepository.MEDIA_ID_ALBUMS
									+ MusicRepository.SEPARATOR_MEDIA_ID
									+ i++)
							.setTitle(audio.getAlbum())
							.setSubtitle(audio.getArtist())
							.setExtras(extras)
							.build();
					final MediaBrowserCompat.MediaItem mediaItem =
							new MediaBrowserCompat.MediaItem(description,
									MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
					newList.add(mediaItem);
				}
				_albumList.postValue(newList);
			}
		}).start();
	}

	private void _convertArtist(final List<ArtistPojo> list) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				int i = 0;
				for (final ArtistPojo audio : list) {
					final Bundle extras = new Bundle();
					extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
					final MediaDescriptionCompat description = builder
							.setMediaId(MusicRepository.MEDIA_ID_ARTISTS
									+ MusicRepository.SEPARATOR_MEDIA_ID
									+ i++)
							.setTitle(audio.getArtist())
							.setExtras(extras)
							.build();
					final MediaBrowserCompat.MediaItem mediaItem =
							new MediaBrowserCompat.MediaItem(description,
									MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
					newList.add(mediaItem);
				}
				_artistList.postValue(newList);
			}
		}).start();
	}

	private void _convertGenre(final List<GenrePojo> list) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				int i = 0;
				for (final GenrePojo audio : list) {
					final Bundle extras = new Bundle();
					extras.putString(MediaMetadataCompat.METADATA_KEY_GENRE, audio.getGenre());
					final MediaDescriptionCompat description = builder
							.setMediaId(MusicRepository.MEDIA_ID_ARTISTS
									+ MusicRepository.SEPARATOR_MEDIA_ID
									+ i++)
							.setTitle(audio.getGenre())
							.setExtras(extras)
							.build();
					final MediaBrowserCompat.MediaItem mediaItem =
							new MediaBrowserCompat.MediaItem(description,
									MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
					newList.add(mediaItem);
				}
				_genreList.postValue(newList);
			}
		}).start();
	}
}
