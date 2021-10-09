package com.muiz6.musicplayer.musicservice;

import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaDescriptionCompat;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat.Result;

import com.muiz6.musicplayer.data.MediaIdPojo;
import com.muiz6.musicplayer.data.MusicRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BrowseManager {

	private final Map<Integer, _BrowseNode> _mapBrowseNodeLevel1 = new HashMap<>();
	private final Map<String, _BrowseNode> _mapBrowseNodeLevel2 = new HashMap<>();
	private final MusicRepository _repository;

	@Inject
	public BrowseManager(MusicRepository repository) {
		_repository = repository;

		// populate map collections to use with adapter pattern
		_populateMapLevel1();
		_populateMapLevel2();
	}

	public void loadChildren(@NonNull String parentMediaId,
			@NonNull Result<List<MediaItem>> result) {
		boolean flagHandled = false;
		final MediaIdPojo objMediaId = MediaIdPojo.fromString(parentMediaId);
		if (objMediaId != null) {
			final String category = objMediaId.getCategory();
			final int id = objMediaId.getId();
			if (category.equals(MediaIdPojo.CATEGORY_ROOT)) {
				_browseRoot(result);
				flagHandled = true;
			}
			else {

				// adapter pattern
				_BrowseNode browseAction = null;
				switch (category) {
					case MediaIdPojo.CATEGORY_LIBRARY:
						browseAction = _mapBrowseNodeLevel1.get(id);
						break;
					case MediaIdPojo.CATEGORY_ALBUM:
					case MediaIdPojo.CATEGORY_ARTIST:
					case MediaIdPojo.CATEGORY_GENRE:
						browseAction = _mapBrowseNodeLevel2.get(category);
				}
				if (browseAction != null) {
					browseAction.action(objMediaId, result);
					flagHandled = true;
				}
			}
		}
		if (!flagHandled) {
			result.sendResult(Collections.<MediaItem>emptyList());
		}
	}

	private static void _browseRoot(@NonNull final Result<List<MediaItem>> result) {
		result.detach();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
				final List<MediaItem> rootItems = new ArrayList<>();
				rootItems.add(new MediaItem(builder
						.setMediaId(MusicRepository.MEDIA_ID_SONGS)
						.setTitle("All Songs")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				rootItems.add(new MediaItem(builder
						.setMediaId(MusicRepository.MEDIA_ID_ALBUMS)
						.setTitle("Albums")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				rootItems.add(new MediaItem(builder
						.setMediaId(MusicRepository.MEDIA_ID_ARTISTS)
						.setTitle("Artists")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				rootItems.add(new MediaItem(builder
						.setMediaId(MusicRepository.MEDIA_ID_GENRES)
						.setTitle("Genres")
						.build(),
						MediaItem.FLAG_BROWSABLE));
				result.sendResult(rootItems);
			}
		}).start();
	}

	private void _populateMapLevel1() {
		_mapBrowseNodeLevel1.put(MediaIdPojo.ID_LIBRARY_SONGS, new _BrowseNode() {

			@Override
			public void action(@NonNull final MediaIdPojo parentMediaId,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_repository.getAllSongList());
					}
				}).start();
			}
		});
		_mapBrowseNodeLevel1.put(MediaIdPojo.ID_LIBRARY_ALBUMS, new _BrowseNode() {

			@Override
			public void action(@NonNull final MediaIdPojo parentMediaId,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_repository.getAllAlbumList());
					}
				}).start();
			}
		});
		_mapBrowseNodeLevel1.put(MediaIdPojo.ID_LIBRARY_ARTISTS, new _BrowseNode() {

			@Override
			public void action(@NonNull final MediaIdPojo parentMediaId,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_repository.getAllArtistList());
					}
				}).start();
			}
		});
		_mapBrowseNodeLevel1.put(MediaIdPojo.ID_LIBRARY_GENRES, new _BrowseNode() {

			@Override
			public void action(@NonNull final MediaIdPojo parentMediaId,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_repository.getAllGenreList());
					}
				}).start();
			}
		});
	}

	private void _populateMapLevel2() {
		_mapBrowseNodeLevel2.put(MediaIdPojo.CATEGORY_ARTIST, new _BrowseNode() {

			@Override
			public void action(@NonNull final MediaIdPojo parentMediaId,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<MediaItem> resultList = new ArrayList<>();
						resultList.addAll(_repository.getSongListByArtist(parentMediaId));
						resultList.addAll(_repository.getAlbumListByArtist(parentMediaId));
						result.sendResult(resultList);
					}
				}).start();
			}
		});
		_mapBrowseNodeLevel2.put(MediaIdPojo.CATEGORY_GENRE, new _BrowseNode() {

			@Override
			public void action(@NonNull final MediaIdPojo parentMediaId,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<MediaItem> resultList = new ArrayList<>();
						resultList.addAll(_repository.getSongListByGenre(parentMediaId));
						resultList.addAll(_repository.getAlbumListByGenre(parentMediaId));
						result.sendResult(resultList);
					}
				}).start();
			}
		});
		_mapBrowseNodeLevel2.put(MediaIdPojo.CATEGORY_ALBUM, new _BrowseNode() {

			@Override
			public void action(@NonNull final MediaIdPojo parentMediaId,
					@NonNull final Result<List<MediaItem>> result) {
				result.detach();
				new Thread(new Runnable() {

					@Override
					public void run() {
						result.sendResult(_repository.getSongListByAlbum(parentMediaId));
					}
				}).start();
			}
		});
	}

	// action associated for level 2 of the tree hierarchy
	// loading one album, artist etc.
	private interface _BrowseNode {

		void action(@NonNull MediaIdPojo parentMediaId, @NonNull Result<List<MediaItem>> result);
	}
}
