package com.muiz6.musicplayer.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.media.MediaBrowserServiceCompat.BrowserRoot;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.muiz6.musicplayer.BuildConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MusicRepository implements
		MediaSessionConnector.MediaMetadataProvider {

	public static final String MEDIA_ID_ROOT = BuildConfig.APPLICATION_ID + ".mediaItemRoot";
	public static final String MEDIA_ID_ALL_SONGS = BuildConfig.APPLICATION_ID + ".allSongs";
	public static final String MEDIA_ID_ALBUMS = BuildConfig.APPLICATION_ID + ".albums";
	public static final String MEDIA_ID_ARTISTS = BuildConfig.APPLICATION_ID + ".artists";
	public static final String MEDIA_ID_GENRES = BuildConfig.APPLICATION_ID + ".genres";
	public static final Character SEPARATOR_MEDIA_ID = '.';

	private static final MediaDescriptionCompat.Builder _DESC_BUILDER =
			new MediaDescriptionCompat.Builder();
	private final Set<Listener> _listenerSet = new HashSet<>();
	private final Observer<List<MediaItem>> _allSongListObserver =
			new DataObserver(MEDIA_ID_ALL_SONGS, _listenerSet);
	private final Observer<List<MediaItem>> _albumListObserver =
			new DataObserver(MEDIA_ID_ALBUMS, _listenerSet);
	private final Observer<List<MediaItem>> _artistListObserver =
			new DataObserver(MEDIA_ID_ARTISTS, _listenerSet);
	private final Observer<List<MediaItem>> _genreListObserver =
			new DataObserver(MEDIA_ID_GENRES, _listenerSet);
	private final LiveData<List<MediaItem>> _allSongList;
	private final LiveData<List<MediaItem>> _albumList;
	private final LiveData<List<MediaItem>> _artistList;
	private final LiveData<List<MediaItem>> _genreList;
	private final Context _context;

	@Inject
	public MusicRepository(@Named("Application") Context context, RoomMediator roomMediator) {
		_context = context;
		_allSongList = roomMediator.getAllSongList();
		_albumList = roomMediator.getAlbumList();
		_artistList = roomMediator.getArtistList();
		_genreList = roomMediator.getGenreList();
	}

	@NonNull
	@Override
	public MediaMetadataCompat getMetadata(@NonNull Player player) {
		int index = player.getCurrentWindowIndex();
		final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
		final MediaItem mediaItem = _allSongList.getValue().get(index);
		final CharSequence title = mediaItem.getDescription().getTitle();
		final CharSequence artist = mediaItem.getDescription().getSubtitle();
		final CharSequence album = mediaItem.getDescription().getDescription();
		if (title != null) {
			builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title.toString());
		}
		if (artist != null) {
			builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist.toString());
		}
		if (album != null) {
			builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, album.toString())
					.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album.toString());
		}
		return builder.build();
	}

	public static BrowserRoot getBrowserRoot() {
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	public static int getIndexFromMediaId(String mediaId) {
		return Integer.parseInt(mediaId.substring(mediaId.lastIndexOf('.') + 1));
	}

	/**
	 * @param mediaId id of the parent media item
	 * @return list of child media items, empty if parent item is not browsable or invalid
	 */
	public List<MediaItem> getChildren(String mediaId) {

		// if accessing from root
		if (mediaId.equals(MusicRepository.MEDIA_ID_ROOT)) {
			final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
			final List<MediaItem> rootItems = new ArrayList<>();
			rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_ALL_SONGS)
					.setTitle("All Songs")
					.build(),
					MediaItem.FLAG_BROWSABLE));
			rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_ALBUMS)
					.setTitle("Albums")
					.build(),
					MediaItem.FLAG_BROWSABLE));
			rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_ARTISTS)
					.setTitle("Artists")
					.build(),
					MediaItem.FLAG_BROWSABLE));
			rootItems.add(new MediaItem(builder.setMediaId(MEDIA_ID_GENRES)
					.setTitle("Genres")
					.build(),
					MediaItem.FLAG_BROWSABLE));
			return rootItems;
		}
		else if (mediaId.equals(MEDIA_ID_ALL_SONGS)) {
			return _allSongList.getValue();
		}
		else if (mediaId.equals(MEDIA_ID_ALBUMS)) {
			return _albumList.getValue();
		}
		else if (mediaId.equals(MEDIA_ID_ARTISTS)) {
			return _artistList.getValue();
		}
		else if (mediaId.equals(MEDIA_ID_GENRES)) {
			return _genreList.getValue();
		}

		// else
		return new ArrayList<>();
	}

	@Nullable
	public MediaItem getMediaItemById(String mediaId) {
		for (final MediaItem i : _allSongList.getValue()) {
			if (i.getMediaId().equals(mediaId)) {
				return i;
			}
		}

		// else
		return null;
	}

	// following method
	// belong to
	// MediaSessionConnector.MediaMetadataProvider

	@Nullable
	public MediaSource[] getQueueBytMediaId(String mediaId) {

		// todo: use media id to provide queue
		if (_allSongList.getValue().size() > 0) {
			final MediaSource[] sources = new MediaSource[_allSongList.getValue().size()];
			final DataSource.Factory dataSourceFactory =
					new DefaultDataSourceFactory(_context, "exoplayer-codelab");
			final MediaSourceFactory factory = new ProgressiveMediaSource.Factory(dataSourceFactory);
			int i = 0;
			for (final MediaBrowserCompat.MediaItem mediaItem : _allSongList.getValue()) {
				sources[i++] = factory
						.createMediaSource(mediaItem.getDescription().getMediaUri());
			}
			return sources;
		}
		return null;
	}

	public MediaSessionConnector.QueueNavigator getQueueNavigator(MediaSessionCompat session) {
		return new TimelineQueueNavigator(session) {

			@NonNull
			@Override
			public MediaDescriptionCompat getMediaDescription(@NonNull Player player,
					int windowIndex) {
				return _allSongList.getValue().get(windowIndex).getDescription();
			}
		};
	}

	public void addListener(Listener listener) {
		_listenerSet.add(listener);

		// start observing when first listener is added
		if (!_listenerSet.isEmpty()) {
			_allSongList.observeForever(_allSongListObserver);
			_albumList.observeForever(_albumListObserver);
			_artistList.observeForever(_artistListObserver);
			_genreList.observeForever(_genreListObserver);
		}
	}

	public void removeListener(Listener listener) {
		_listenerSet.remove(listener);

		// stop observing when all listeners are removed
		if (_listenerSet.isEmpty()) {
			_allSongList.removeObserver(_allSongListObserver);
			_albumList.removeObserver(_albumListObserver);
			_artistList.removeObserver(_artistListObserver);
			_genreList.removeObserver(_genreListObserver);
		}
	}

	private static MediaDescriptionCompat _getMediaDescription(String mediaId,
			String title,
			@Nullable Uri mediaUri,
			@Nullable String subtitle,
			@Nullable String description) {
		return _DESC_BUILDER

				// call to reset if set externally
				.setExtras(null)
				.setIconUri(null)
				.setIconBitmap(null)

				// set actual parameters
				.setMediaId(mediaId)
				.setTitle(title)
				.setSubtitle(subtitle)
				.setMediaUri(mediaUri)
				.setDescription(description)
				.build();
	}

	public interface Listener {

		void onChildrenChanged(String parentMediaId);
	}
}
