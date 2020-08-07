package com.muiz6.musicplayer.ui.main.home;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.media.MusicServiceConnection;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {

	private final MusicServiceConnection _connection;

	@Inject
	public HomeViewModel(MusicServiceConnection connection) {
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
}
