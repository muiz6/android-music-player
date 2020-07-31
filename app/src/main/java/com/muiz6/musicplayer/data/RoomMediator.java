package com.muiz6.musicplayer.data;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

	private final AudioDao _dao;

	@Inject
	public RoomMediator(AudioDatabase db) {
		_dao = db.getAudioDao();
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getAllSongList() {
		final MutableLiveData<List<MediaBrowserCompat.MediaItem>> result =
				new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<SongPojo> list = _dao.getAllSongList();
				final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				int i = 0;
				for (final SongPojo audio : list) {
					final Bundle extras = new Bundle();
					extras.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
							audio.getTitle());
					extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
					extras.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, audio.getDuration());
					final MediaDescriptionCompat description = builder
							.setMediaId(MusicRepository.MEDIA_ID_ALL_SONGS
									+ MusicRepository.SEPARATOR_MEDIA_ID
									+ i++)
							.setMediaUri(Uri.parse(audio.getPath()))
							.setTitle(audio.getDisplayName())
							.setSubtitle(audio.getArtist())
							.setExtras(extras)
							.build();
					final MediaBrowserCompat.MediaItem mediaItem =
							new MediaBrowserCompat.MediaItem(description,
									MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
					newList.add(mediaItem);
				}
				result.postValue(newList);
			}
		}).start();
		return result;
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getAlbumList() {
		final MutableLiveData<List<MediaBrowserCompat.MediaItem>> result =
				new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<AlbumPojo> list = _dao.getAlbumList();
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
				result.postValue(newList);
			}
		}).start();
		return result;
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getArtistList() {
		final MutableLiveData<List<MediaBrowserCompat.MediaItem>> result =
				new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<ArtistPojo> list = _dao.getArtistList();
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
				result.postValue(newList);
			}
		}).start();
		return result;
	}

	public LiveData<List<MediaBrowserCompat.MediaItem>> getGenreList() {
		final MutableLiveData<List<MediaBrowserCompat.MediaItem>> result =
				new MutableLiveData<>(Collections.<MediaBrowserCompat.MediaItem>emptyList());
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<GenrePojo> list = _dao.getGenreList();
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
				result.postValue(newList);
			}
		}).start();
		return result;
	}
}
