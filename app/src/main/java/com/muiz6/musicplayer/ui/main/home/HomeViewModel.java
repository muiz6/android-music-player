package com.muiz6.musicplayer.ui.main.home;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import javax.inject.Inject;

public class HomeViewModel extends AndroidViewModel {

	private static final String _TAG = "HomeViewModel";
	private final Handler _handler = new Handler(Looper.getMainLooper());
	private final MutableLiveData<Integer> _seekBarMax = new MutableLiveData<>(0);
	private final MutableLiveData<Integer> _playPauseIconResId =
			new MutableLiveData<>(R.drawable.ic_play_arrow);
	private final MutableLiveData<Boolean> _shuffleState = new MutableLiveData<>(false);
	private final MutableLiveData<Pair<Integer, Boolean>> _repeatIcon =
			new MutableLiveData<>(new Pair<>(R.drawable.ic_repeat, false));
	private final MutableLiveData<Integer> _seekBarPosition = new MutableLiveData<>(0);
	private final MutableLiveData<String> _songTitle = new MutableLiveData<>();
	private final MutableLiveData<Bitmap> _albumArt = new MutableLiveData<>();
	private final Observer<MediaMetadataCompat> _observerMetadata =
			new Observer<MediaMetadataCompat>() {

				@Override
				public void onChanged(MediaMetadataCompat metadata) {
					if (metadata != null) {
						final MediaDescriptionCompat description = metadata.getDescription();
						_songTitle.postValue(String.valueOf(description.getTitle()));
						final Uri uri = description.getMediaUri();
						if (uri != null) {
							new Thread(new _AsyncLoadBitmap(uri)).start();
						}

						// metadata.getDescription().getExtras is always null, possibly some
						// issue in exoplayer implementation, but duration can be got from
						// metadata.getBundle()
						final Bundle extras = metadata.getBundle();
						final int duration = (int) extras.getLong(MediaMetadataCompat
								.METADATA_KEY_DURATION);
						_seekBarMax.postValue(duration);
					}
				}
			};
	private final Observer<PlaybackStateCompat> _observerPlaybackState =
			new Observer<PlaybackStateCompat>() {

				@Override
				public void onChanged(PlaybackStateCompat playbackState) {
					if (playbackState!=null) {
						if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
							_playPauseIconResId.postValue(R.drawable.ic_pause);
						}
						else {
							_playPauseIconResId.postValue(R.drawable.ic_play_arrow);
						}
						final MediaControllerCompat controller = _connection.getMediaController();
						int shuffleMode = controller.getShuffleMode();
						if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
							_shuffleState.postValue(true);
						}
						else {
							_shuffleState.postValue(false);
						}
						int repeatMode = controller.getRepeatMode();
						if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
							_repeatIcon.postValue(new Pair<>(R.drawable.ic_repeat, true));
						}
						else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
							_repeatIcon.postValue(new Pair<>(R.drawable.exo_controls_repeat_one,
									false));
						}
						else {
							_repeatIcon.postValue(new Pair<>(R.drawable.ic_repeat, false));
						}
					}
				}
			};
	private final Runnable _updateSeekBarRunnable = new Runnable() {

		@Override
		public void run() {
			final MediaControllerCompat controller = _connection.getMediaController();
			if (controller != null) {

				// _connection.getPlaybackState() will not work as it is only updated when playback
				// state changes
				final PlaybackStateCompat pbState = _connection.getMediaController()
						.getPlaybackState();
				if (pbState != null) {
					_seekBarPosition.postValue((int) pbState.getPosition());
				}
			}

			// todo: stop this when media is not playing for performance
			_handler.postDelayed(this, 40); // 25Hz
		}
	};
	private final MusicServiceConnection _connection;

	@Inject
	public HomeViewModel(Application application, MusicServiceConnection connection) {
		super(application);
		_connection = connection;
		_connection.getMetadata().observeForever(_observerMetadata);
		_connection.getPlaybackState().observeForever(_observerPlaybackState);

		//
		_handler.post(_updateSeekBarRunnable);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		_connection.getMetadata().removeObserver(_observerMetadata);
		_connection.getPlaybackState().removeObserver(_observerPlaybackState);
	}

	public void onPlayPauseBtnClicked() {
		final PlaybackStateCompat pbState = _connection.getPlaybackState().getValue();
		if (pbState != null) {
			long state = pbState.getState();
			if (state == PlaybackStateCompat.STATE_PLAYING) {
				_connection.getTransportControls().pause();
			}
			else {
				_connection.getTransportControls().play();
			}
		}
	}

	public void onSkipNextBtnClicked() {
		_connection.getTransportControls().skipToNext();
	}

	public void onSkipPreviousBtnClicked() {
		_connection.getTransportControls().skipToPrevious();
	}

	public void onRepeatBtnClicked() {
		final int repeatMode = _connection.getMediaController().getRepeatMode();
		final MediaControllerCompat.TransportControls transportControls =
				_connection.getTransportControls();
		switch (repeatMode) {
			case PlaybackStateCompat.REPEAT_MODE_NONE:
				transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
				break;
			case PlaybackStateCompat.REPEAT_MODE_ONE:
				transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
				break;
			case PlaybackStateCompat.REPEAT_MODE_ALL:
				transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
				break;
		}
	}

	public void onShuffleBtnClicked() {
		final int shuffleMode = _connection.getMediaController().getShuffleMode();
		if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
			_connection.getTransportControls()
					.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
		}
		else {
			_connection.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
		}
	}

	public void onSeek(int seekpos) {
		_connection.getTransportControls().seekTo(seekpos);
	}

	public LiveData<Bitmap> getAlbumArt() {
		return _albumArt;
	}

	public LiveData<String> getSongTitle() {
		return _songTitle;
	}

	public LiveData<Integer> getPlayPauseIconResId() {
		return _playPauseIconResId;
	}

	public LiveData<Integer> getSeekBarMaxValue() {
		return _seekBarMax;
	}

	public LiveData<Boolean> getShuffleState() {
		return _shuffleState;
	}

	public LiveData<Pair<Integer, Boolean>> getRepeatIcon() {
		return _repeatIcon;
	}

	private class _AsyncLoadBitmap implements Runnable {

		private final Uri _uri;

		public _AsyncLoadBitmap(@NonNull Uri uri) {
			_uri = uri;
		}

		@Override
		public void run() {
			final MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
			try {
				metadataRetriever.setDataSource(getApplication().getApplicationContext(), _uri);
				final byte[] byteArr = metadataRetriever.getEmbeddedPicture();
				if (byteArr != null) {
					_albumArt.postValue(BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length));
				}
				else {
					_albumArt.postValue(null);
				}
			}
			catch (Exception e) {
				Log.e(_TAG, "Exception caught!", e);
			}
			metadataRetriever.release();
		}
	}

	public LiveData<Integer> getSeekBarPosition() {
		return  _seekBarPosition;
	}
}
