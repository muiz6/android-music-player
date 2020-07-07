package com.muiz6.musicplayer.ui.nowplaying;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.ActivityNowPlayingBinding;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.MyConnectionCallback;
import com.muiz6.musicplayer.ui.MyControllerCallback;

public class NowPlayingActivity extends AppCompatActivity
		implements MyConnectionCallback.Listener,
		MyControllerCallback.Listener {

	private ActivityNowPlayingBinding _binding;
	private MediaBrowserCompat _mediaBrowser;
	private MediaControllerCompat.Callback _controllerCallback;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_binding = ActivityNowPlayingBinding.inflate(getLayoutInflater());
		setContentView(_binding.getRoot());

		_controllerCallback = new MyControllerCallback(this);
		_mediaBrowser = new MediaBrowserCompat(this,
				new ComponentName(this, MusicService.class),
				new MyConnectionCallback(this),
				null);
	}

	@Override
	protected void onStart() {
		super.onStart();

		_mediaBrowser.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		_mediaBrowser.disconnect();
	}

	public void onClick(View view) {
		if (view == _binding.nowPlayingFab) {
			final Intent intent = new Intent(this, QueueActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onConnected() {
		final MediaControllerCompat controller =
				MediaControllerCompat.getMediaController(this);
		final ImageButton btnPlay = _binding.nowPlayingBtnPlay;
		final PlaybackStateCompat pbState = controller.getPlaybackState();
		if (pbState != null) {
			if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
				btnPlay.setImageDrawable(ContextCompat.getDrawable(this,
						R.drawable.ic_pause_black_24dp));
			}
			else {
				btnPlay.setImageDrawable(ContextCompat.getDrawable(this,
						R.drawable.ic_play_arrow_black_24dp));
			}
		}

		final MediaControllerCompat.TransportControls transportControls =
				controller.getTransportControls();
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final PlaybackStateCompat pbState = controller.getPlaybackState();
				if (pbState != null) {
					if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
						transportControls.pause();
					}
					else {
						transportControls.play();
					}
				}
			}
		});

		final View btnRepeat = _binding.nowPlayingBtnRepeat;
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
			}
		});

		final ImageButton btnShuffle = _binding.nowPlayingBtnShuffle;
		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final int mode = controller.getShuffleMode();
				if (mode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
					transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
					_setColor(btnShuffle.getDrawable(), R.color.colorWhite);
				}
				else {
					transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
					_setColor(btnShuffle.getDrawable(), R.color.colorAccent);
				}
			}
		});
	}

	@NonNull
	@Override
	public MediaBrowserCompat getMediaBrowser() {
		return _mediaBrowser;
	}

	@NonNull
	@Override
	public Activity getActivity() {
		return this;
	}

	@Nullable
	@Override
	public MediaControllerCompat.Callback getMediaControllerCallback() {
		return _controllerCallback;
	}

	@Override
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {}

	private void _setColor(Drawable icon, @ColorRes int id) {
		int tabIconColor = ContextCompat.getColor(this, id);
		if (icon != null) {
			icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
		}
	}
}
