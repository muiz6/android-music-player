package com.muiz6.musicplayer.musicservice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.muiz6.musicplayer.data.MediaIdPojo;
import com.muiz6.musicplayer.data.MusicRepository;
import com.muiz6.musicplayer.di.scope.ServiceScope;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@ServiceScope
public class QueueManager {

	private final Handler _handler = new Handler(Looper.getMainLooper());
	private final Context _context;
	private final MusicRepository _repository;
	private List<MediaBrowserCompat.MediaItem> _queue;

	@Inject
	public QueueManager(@Named("Application") Context context, MusicRepository repository) {
		_context = context;
		_repository = repository;
	}

	public void getQueueBytMediaId(@NonNull String mediaId, Callback callback)
		throws IllegalArgumentException {

		final MediaIdPojo mediaIdPojo = MediaIdPojo.fromString(mediaId);
		if (mediaIdPojo == null && mediaIdPojo.getCategory().equals(MediaIdPojo.CATEGORY_SONG)) {
			throw new IllegalArgumentException("Invalid media id! "
					+ "Media Id should be of a playable media Item");
		}
		new Thread(new _AsyncPrepare(mediaIdPojo, callback)).start();
	}

	public MediaSessionConnector.QueueNavigator getQueueNavigator(MediaSessionCompat session) {
		return new TimelineQueueNavigator(session) {

			@Override
			@NonNull
			public MediaDescriptionCompat getMediaDescription(@NonNull Player player,
					int windowIndex) {
				return _queue.get(windowIndex).getDescription();
			}
		};
	}

	public interface Callback {
		void onCompletion(@NonNull ConcatenatingMediaSource queue, int windowIndex);
	}

	private class _AsyncPrepare implements Runnable {

		private final MediaIdPojo _mediaIdPojo;
		private final Callback _callback;

		public _AsyncPrepare(MediaIdPojo mediaId, Callback callback) {
			_mediaIdPojo = mediaId;
			_callback = callback;
		}

		@Override
		public void run() {

			// provided media item is sibling of to be generated media items
			final String parentCategory = _mediaIdPojo.getParentCategory();
			final int parentId = _mediaIdPojo.getParentId();
			final MediaIdPojo parentMediaId = new MediaIdPojo();
			parentMediaId.setCategory(parentCategory);
			parentMediaId.setId(parentId);
			switch (parentCategory) {
				case MediaIdPojo.CATEGORY_LIBRARY:
					_queue = _repository.getAllSongList();
					break;
				case MediaIdPojo.CATEGORY_ARTIST:
					_queue = _repository.getSongListByArtist(parentMediaId);
					break;
				case MediaIdPojo.CATEGORY_ALBUM:
					_queue = _repository.getSongListByAlbum(parentMediaId);
					break;
				case MediaIdPojo.CATEGORY_GENRE:
					_queue = _repository.getSongListByGenre(parentMediaId);
					break;
				default:
					_queue = Collections.emptyList();
			}

			// song list is not sorted by id sadly
			int i = 0;
			for (; i < _queue.size(); i++) {
				final int id = MediaIdPojo.fromString(_queue.get(i).getMediaId()).getId();
				if (_mediaIdPojo.getId() == id) {
					break;
				}
			}
			final int windowIndex = i;
			final MediaSource[] sources = new MediaSource[_queue.size()];
			final MediaSourceFactory factory = new ProgressiveMediaSource
					.Factory(new DefaultDataSourceFactory(_context, "exoplayer-codelab"));
			i = 0;
			for (final MediaBrowserCompat.MediaItem mediaItem : _queue) {
				sources[i++] = factory.createMediaSource(mediaItem.getDescription().getMediaUri());
			}
			_handler.post(new Runnable() {

				@Override
				public void run() {
					_callback.onCompletion(new ConcatenatingMediaSource(sources), windowIndex);
				}
			});
		}
	}
}
