package com.muiz6.musicplayer.ui.nowplaying;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.ActivityNowPlayingBinding;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.MyConnectionCallback;
import com.muiz6.musicplayer.ui.MyControllerCallback;
import com.muiz6.musicplayer.ui.ThemeUtil;

public class NowPlayingActivity extends AppCompatActivity
		implements MyConnectionCallback.Listener,
		MyControllerCallback.Listener {

	private final Handler _handler = new Handler();
	private ActivityNowPlayingBinding _binding;
	private MediaBrowserCompat _mediaBrowser;
	private MediaControllerCompat.Callback _controllerCallback;
	private Runnable _seekBarRunnable = new Runnable() {
		@Override
		public void run() {
			final MediaControllerCompat controller = MediaControllerCompat
					.getMediaController(NowPlayingActivity.this);
			int progress = (int) controller.getPlaybackState().getPosition();
			_binding.nowPlayingSeekBar.setProgress(progress);
			_handler.postDelayed(this, 34); // at 30Hz
		}
	};

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

	@Override
	public void onConnected() {
		final MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
		final MediaControllerCompat.TransportControls transportControls =
				controller.getTransportControls();
		_binding.nowPlayingBtnPlay.setOnClickListener(new View.OnClickListener() {

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

		_binding.nowPlayingBtnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
			}
		});

		_binding.nowPlayingBtnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final int mode = controller.getShuffleMode();
				if (mode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
					transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
				}
				else {
					transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
				}
			}
		});

		_binding.nowPlayingBtnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				transportControls.skipToNext();
			}
		});

		_binding.nowPlayingBtnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				transportControls.skipToPrevious();
			}
		});

		_binding.nowPlayingBtnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final int mode = controller.getRepeatMode();
				if (mode == PlaybackStateCompat.REPEAT_MODE_ALL) {
					transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
				}
				else if (mode == PlaybackStateCompat.REPEAT_MODE_ONE) {
					transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
				}
				else {
					transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
				}
			}
		});

		_updateFromPlaybackState(controller.getPlaybackState());
		_updateFromMetadata(controller.getMetadata());

		_binding.nowPlayingSeekBar.setOnSeekBarChangeListener(
				new _SeekBarListener(controller.getTransportControls()));
		this.runOnUiThread(_seekBarRunnable);
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
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
		_updateFromPlaybackState(state);
	}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
		_updateFromMetadata(metadata);
	}

	// following methods
	// are specific to
	// this activity

	public void onClick(View view) {
		if (view == _binding.nowPlayingFab) {
			final Intent intent = new Intent(this, QueueActivity.class);
			startActivity(intent);
		}
	}

	private void _updateFromMetadata(MediaMetadataCompat metadata) {
		if (metadata != null) {
			final String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
			if (title != null) {
				_binding.nowPlayingTitle.setText(title);
			}
			final String artist = metadata
					.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
			if (artist != null) {
				_binding.nowPlayingArtist.setText(artist);
			}
			else {
				_binding.nowPlayingArtist.setText(R.string.unknown_artist);
			}
			final String album = metadata
					.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION);
			if (album != null) {
				_binding.nowPlayingAlbum.setText(album);
			}
			else {
				_binding.nowPlayingAlbum.setText(R.string.unknown_album);
			}
		}
		final int duration = (int) MediaControllerCompat.getMediaController(this).getMetadata()
				.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
		_binding.nowPlayingSeekBar.setMax(duration);
	}

	private void _updateFromPlaybackState(PlaybackStateCompat state) {
		final MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
		final int shuffleMode = controller.getShuffleMode();
		final int colorAccent = ThemeUtil.getColor(this, R.attr.colorAccent);
		final int colorIconDefault = ThemeUtil.getColor(this, R.attr.tint);
		if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
			DrawableCompat.setTint(_binding.nowPlayingBtnShuffle.getDrawable(),
					colorAccent);
		}
		else {
			DrawableCompat.setTint(_binding.nowPlayingBtnShuffle.getDrawable(),
					colorIconDefault);
		}

		final int repeatMode = controller.getRepeatMode();
		if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
			_binding.nowPlayingBtnRepeat
					.setImageDrawable(getDrawable(R.drawable.ic_repeat));
			DrawableCompat.setTint(_binding.nowPlayingBtnRepeat.getDrawable(),
					colorAccent);
		}
		else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
			_binding.nowPlayingBtnRepeat
					.setImageDrawable(getDrawable(R.drawable.exo_controls_repeat_one));
		}
		else {
			_binding.nowPlayingBtnRepeat
					.setImageDrawable(getDrawable(R.drawable.ic_repeat));
			DrawableCompat.setTint(_binding.nowPlayingBtnRepeat.getDrawable(),
					colorIconDefault);
		}

		if (state != null) {
			if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
				_binding.nowPlayingBtnPlay.setImageDrawable(ContextCompat.getDrawable(this,
						R.drawable.ic_pause));
				DrawableCompat.setTint(_binding.nowPlayingBtnPlay.getDrawable(),
						colorAccent);
			}
			else {
				_binding.nowPlayingBtnPlay.setImageDrawable(ContextCompat.getDrawable(this,
						R.drawable.ic_play_arrow));
				DrawableCompat.setTint(_binding.nowPlayingBtnPlay.getDrawable(),
						colorIconDefault);
			}
		}
	}
}
