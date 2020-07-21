package com.muiz6.musicplayer.ui.main.nowplaying;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.media.MusicServiceConnection;

import javax.inject.Inject;

@FragmentScope
public class PlayerViewModel extends ViewModel {

	private final MusicServiceConnection _connection;

	@Inject
	public PlayerViewModel(MusicServiceConnection connection) {
		_connection = connection;
	}

	public LiveData<Boolean> isConnected() {
		return _connection.isConnected();
	}

	public LiveData<PlaybackStateCompat> getPlayBackState() {
		return _connection.getPlaybackState();
	}

	public LiveData<MediaMetadataCompat> getMetadata() {
		return _connection.getMetadata();
	}

	public void onPlayPauseBtnClicked() {
		final PlaybackStateCompat pbState = getPlayBackState().getValue();
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

	/**
	 * updates with PlaybackStateCompat so call when observing playback state
	 * @return current repeat mode of media session
	 */
	public int getRepeatMode() {
		return _connection.getMediaController().getRepeatMode();
	}

	/**
	 * updates with PlaybackStateCompat so call when observing playback state
	 * @return current shuffle mode of media session
	 */
	public int getShuffleMode() {
		return _connection.getMediaController().getShuffleMode();
	}
}
