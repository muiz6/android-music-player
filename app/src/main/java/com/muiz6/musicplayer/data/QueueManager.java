package com.muiz6.musicplayer.data;

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
import com.muiz6.musicplayer.data.db.AudioDao;
import com.muiz6.musicplayer.data.db.AudioDatabase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class QueueManager {

	private final Handler _handler = new Handler(Looper.getMainLooper());
	private final Context _context;
	private final AudioDao _dao;
	private List<MediaBrowserCompat.MediaItem> _queue;
	// private final MusicRepository _repository;

	@Inject
	public QueueManager(@Named("Application") Context context, AudioDatabase db) {
		_context = context;
		_dao = db.getAudioDao();
	}

	public void getQueueBytMediaId(@NonNull String mediaId, Callback callback)
		throws IllegalArgumentException {

		final MediaIdPojo mediaIdPojo = MusicRepository.getMediaId(mediaId);
		if (mediaIdPojo == null) {
			throw new IllegalArgumentException("Invalid media id!");
		}

		// todo: use media id to provide queue
		new Thread(new _AsyncPrepare(mediaIdPojo, callback)).start();
	}

	public MediaSessionConnector.QueueNavigator getQueueNavigator(MediaSessionCompat session) {
		return new TimelineQueueNavigator(session) {

			@Override
			public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
				return _queue.get(windowIndex).getDescription();
			}
		};
	}

	public interface Callback {
		void onCompletion(ConcatenatingMediaSource queue);
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
			_queue = MusicRepository.getSongMediaItems(_dao.getAllSongList());
			final MediaSource[] sources = new MediaSource[_queue.size()];
			final MediaSourceFactory factory = new ProgressiveMediaSource
					.Factory(new DefaultDataSourceFactory(_context, "exoplayer-codelab"));
			int i = 0;
			for (final MediaBrowserCompat.MediaItem mediaItem : _queue) {
				sources[i++] = factory.createMediaSource(mediaItem.getDescription().getMediaUri());
			}
			_handler.post(new Runnable() {

				@Override
				public void run() {
					_callback.onCompletion(new ConcatenatingMediaSource(sources));
				}
			});
		}
	}
}
