package com.muiz6.musicplayer.data;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat.BrowserRoot;

import com.muiz6.musicplayer.BuildConfig;
import com.muiz6.musicplayer.data.db.AudioDao;
import com.muiz6.musicplayer.data.db.AudioDatabase;
import com.muiz6.musicplayer.data.db.pojos.AlbumPojo;
import com.muiz6.musicplayer.data.db.pojos.ArtistPojo;
import com.muiz6.musicplayer.data.db.pojos.GenrePojo;
import com.muiz6.musicplayer.data.db.pojos.SongPojo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MusicRepository implements AudioDatabase.Callback {

	public static final String KEY_EXTRAS_MEDIA_CATEGORY = BuildConfig.APPLICATION_ID + ".mediaType";
	public static final String MEDIA_ID_ROOT = new MediaIdPojo(MediaIdPojo.CATEGORY_ROOT,
			MediaIdPojo.ID_ROOT,
			null,
			0).toString();
	public static final String MEDIA_ID_SONGS = new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
			MediaIdPojo.ID_LIBRARY_SONGS,
			null,
			0).toString();
	public static final String MEDIA_ID_ALBUMS = new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
			MediaIdPojo.ID_LIBRARY_ALBUMS,
			null,
			0).toString();
	public static final String MEDIA_ID_ARTISTS = new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
			MediaIdPojo.ID_LIBRARY_ARTISTS,
			null,
			0).toString();
	public static final String MEDIA_ID_GENRES = new MediaIdPojo(MediaIdPojo.CATEGORY_LIBRARY,
			MediaIdPojo.ID_LIBRARY_GENRES,
			null,
			0).toString();

	private final Handler _handler = new Handler(Looper.getMainLooper());
	private final AudioDatabase _db;
	private final AudioDao _dao;
	private Callback _callback;

	@Inject
	public MusicRepository(@NonNull AudioDatabase db) {
		_db = db;
		_dao = db.getAudioDao();
	}

	@Override
	public void onCompletion(boolean success) {
		if (_callback != null) {
			_handler.post(new Runnable() {

				@Override
				public void run() {

					// notify everyone that all data has changed
					// just notifying root id is not working
					_callback.notifyChildrenChanged(MEDIA_ID_SONGS);
					_callback.notifyChildrenChanged(MEDIA_ID_ALBUMS);
					_callback.notifyChildrenChanged(MEDIA_ID_ARTISTS);
					_callback.notifyChildrenChanged(MEDIA_ID_GENRES);
				}
			});
		}
	}

	@NonNull
	public static BrowserRoot getBrowserRoot() {
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	public void scanMusicLibrary() {
		_db.scanMusicLibrary();
	}

	public void setCallback(@Nullable Callback callback) {
		_callback = callback;
		if (_callback != null) {
			_db.setCallback(this);
		}
		else {
			_db.setCallback(null);
		}
	}

	@NonNull
	public List<MediaItem> getAllSongList() {
		return _getSongMediaItems(_dao.getAllSongList(), MediaIdPojo.fromString(MEDIA_ID_SONGS));
	}

	@NonNull
	public List<MediaItem> getAllAlbumList() {
		return _getAlbumMediaItems(_dao.getAllAlbumList(), MediaIdPojo.fromString(MEDIA_ID_ALBUMS));
	}

	@NonNull
	public List<MediaItem> getAllArtistList() {
		return _getArtistMediaItems(_dao.getAllArtistList(),
				MediaIdPojo.fromString(MEDIA_ID_ARTISTS));
	}

	@NonNull
	public List<MediaItem> getAllGenreList() {
		return _getGenreMediaItems(_dao.getAllGenreList(), MediaIdPojo.fromString(MEDIA_ID_GENRES));
	}

	@NonNull
	public List<MediaItem> getSongListByArtist(@NonNull MediaIdPojo artistMediaId) {
		return _getSongMediaItems(_dao.getSongListByArtistId(artistMediaId.getId()), artistMediaId);
	}

	@NonNull
	public List<MediaItem> getSongListByAlbum(@NonNull MediaIdPojo albumMediaId) {
		return _getSongMediaItems(_dao.getSongListByAlbumId(albumMediaId.getId()), albumMediaId);
	}

	@NonNull
	public List<MediaItem> getSongListByGenre(@NonNull MediaIdPojo genreMediaId) {
		return _getSongMediaItems(_dao.getSongListByGenreId(genreMediaId.getId()), genreMediaId);
	}

	@NonNull
	public List<MediaItem> getAlbumListByArtist(@NonNull MediaIdPojo artistMediaId) {
		return _getAlbumMediaItems(_dao.getAlbumListByArtistId(artistMediaId.getId()),
				artistMediaId);
	}

	@NonNull
	public List<MediaItem> getAlbumListByGenre(@NonNull MediaIdPojo genreMediaId) {
		return _getAlbumMediaItems(_dao.getAlbumListByGenreId(genreMediaId.getId()), genreMediaId);
	}

	@NonNull
	private static List<MediaItem> _getSongMediaItems(@NonNull List<SongPojo> list,
			@NonNull MediaIdPojo parentMediaId) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_SONG);
		objMediaId.setParentCategory(parentMediaId.getCategory());
		objMediaId.setParentId(parentMediaId.getId());
		for (final SongPojo audio : list) {
			objMediaId.setId(audio.getRowId());
			final Bundle extras = new Bundle();
			extras.putString(KEY_EXTRAS_MEDIA_CATEGORY, MediaIdPojo.CATEGORY_SONG);
			extras.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
					audio.getTitle());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			extras.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, audio.getDuration());
			final MediaDescriptionCompat description = builder
					.setMediaId(objMediaId.toString())
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
		return newList;
	}

	@NonNull
	private static List<MediaItem> _getArtistMediaItems(@NonNull List<ArtistPojo> list,
			@NonNull MediaIdPojo parentMediaId) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_ARTIST);
		objMediaId.setParentCategory(parentMediaId.getCategory());
		objMediaId.setParentId(parentMediaId.getId());
		for (final ArtistPojo audio : list) {
			final Bundle extras = new Bundle();
			objMediaId.setId(audio.getArtistId());
			extras.putString(KEY_EXTRAS_MEDIA_CATEGORY, MediaIdPojo.CATEGORY_ARTIST);
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			final MediaDescriptionCompat description = builder
					.setMediaId(objMediaId.toString())
					.setTitle(audio.getArtist())
					.setExtras(extras)
					.build();
			final MediaBrowserCompat.MediaItem mediaItem =
					new MediaBrowserCompat.MediaItem(description,
							MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
			newList.add(mediaItem);
		}
		return newList;
	}

	@NonNull
	private static List<MediaItem> _getAlbumMediaItems(@NonNull List<AlbumPojo> list,
			@NonNull MediaIdPojo parentMediaId) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_ALBUM);
		objMediaId.setParentCategory(parentMediaId.getCategory());
		objMediaId.setParentId(parentMediaId.getId());
		for (final AlbumPojo audio : list) {
			objMediaId.setId(audio.getAlbumId());
			final Bundle extras = new Bundle();
			extras.putString(KEY_EXTRAS_MEDIA_CATEGORY, MediaIdPojo.CATEGORY_ALBUM);
			extras.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, audio.getAlbum());
			extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.getArtist());
			final MediaDescriptionCompat description = builder
					.setMediaId(objMediaId.toString())
					.setTitle(audio.getAlbum())
					.setSubtitle(audio.getArtist())
					.setExtras(extras)
					.build();
			final MediaBrowserCompat.MediaItem mediaItem =
					new MediaBrowserCompat.MediaItem(description,
							MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
			newList.add(mediaItem);
		}
		return newList;
	}

	@NonNull
	private static List<MediaItem> _getGenreMediaItems(@NonNull List<GenrePojo> list,
			@NonNull MediaIdPojo parentMediaId) {
		final List<MediaBrowserCompat.MediaItem> newList = new ArrayList<>();
		final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
		final MediaIdPojo objMediaId = new MediaIdPojo();
		objMediaId.setCategory(MediaIdPojo.CATEGORY_GENRE);
		objMediaId.setParentCategory(parentMediaId.getCategory());
		objMediaId.setParentId(parentMediaId.getId());
		for (final GenrePojo audio : list) {
			objMediaId.setId(audio.getGenreId());
			final Bundle extras = new Bundle();
			extras.putString(MusicRepository.KEY_EXTRAS_MEDIA_CATEGORY,
					MediaIdPojo.CATEGORY_GENRE);
			extras.putString(MediaMetadataCompat.METADATA_KEY_GENRE, audio.getGenre());
			final MediaDescriptionCompat description = builder
					.setMediaId(objMediaId.toString())
					.setTitle(audio.getGenre())
					.setExtras(extras)
					.build();
			final MediaBrowserCompat.MediaItem mediaItem =
					new MediaBrowserCompat.MediaItem(description,
							MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
			newList.add(mediaItem);
		}
		return newList;
	}

	// used to call notify children changed method of media browser service
	public interface Callback {

		void notifyChildrenChanged(@NonNull String string);
	}
}
