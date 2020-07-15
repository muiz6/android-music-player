package com.muiz6.musicplayer.ui.main.nowplaying;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentPlayerBinding;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.media.MediaConnectionCallback;
import com.muiz6.musicplayer.media.MediaControllerCallback;
import com.muiz6.musicplayer.ui.ThemeUtil;
import com.muiz6.musicplayer.ui.main.MediaBrowserFragment;

import javax.inject.Inject;

@FragmentScope
public class PlayerFragment extends MediaBrowserFragment
		implements SeekBar.OnSeekBarChangeListener {

	private final Handler _handler = new Handler();
	private final MediaControllerCallback _mediaControllerCallback;
	private FragmentPlayerBinding _binding;
	private Runnable _seekBarRunnable = new Runnable() {
		@Override
		public void run() {
			int progress = (int) getMediaController().getPlaybackState().getPosition();
			_binding.nowPlayingSeekBar.setProgress(progress);
			_handler.postDelayed(this, 34); // at 30Hz
		}
	};

	// arg ctor for fragment factory
	@Inject
	public PlayerFragment(MediaBrowserCompat mediaBrowser,
			MediaConnectionCallback connectionCallback,
			MediaControllerCallback controllerCallback) {
		super(mediaBrowser, connectionCallback, controllerCallback);

		_mediaControllerCallback = controllerCallback;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		_mediaControllerCallback.addListener(this);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_player, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_binding = FragmentPlayerBinding.bind(view);

		final NavController navController = Navigation.findNavController(view);
		_binding.nowPlayingFab.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				navController.navigate(R.id.main_queue_fragment);
			}
		});

		final MediaControllerCompat mediaController = getMediaController();
		if (mediaController != null) {
			mediaController.registerCallback(_mediaControllerCallback);
			_buildTransportControls();
			_updateFromMetadata(mediaController.getMetadata());
			_updateFromPlaybackState(mediaController.getPlaybackState());

			_binding.nowPlayingSeekBar.setOnSeekBarChangeListener(this);

			// todo: improve seek bar progress
			getActivity().runOnUiThread(_seekBarRunnable);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		_binding = null;
	}

	// following methods
	// belong to
	// MediaConnectionCallback.Listener

	@Override
	public void onConnected() {
		super.onConnected();

		final MediaControllerCompat mediaController = getMediaController();
		if (mediaController != null) {
			_updateFromMetadata(mediaController.getMetadata());
			_updateFromPlaybackState(mediaController.getPlaybackState());
			_updateFromShuffleMode(mediaController.getShuffleMode());
			_updateFromRepeatMode(mediaController.getRepeatMode());
		}
	}

	// following methods
	// belong to
	// MediaControllerCallback.Listener

	@Override
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
		_updateFromPlaybackState(state);
	}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
		_updateFromMetadata(metadata);
	}

	@Override
	public void onRepeatModeChanged(int repeatMode) {
		_updateFromRepeatMode(repeatMode);
	}

	@Override
	public void onShuffleModeChanged(int shuffleMode) {
		_updateFromShuffleMode(shuffleMode);
	}

	// following methods
	// belong to
	// SeekBar.OnSeekBarChangeListener

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			final MediaControllerCompat mediaController = getMediaController();
			if (mediaController != null) {
				mediaController.getTransportControls().seekTo(progress);
			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}

	private void _buildTransportControls() {
		final MediaControllerCompat.TransportControls transportControls =
				getMediaController().getTransportControls();
		_binding.nowPlayingBtnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final PlaybackStateCompat pbState = getMediaController().getPlaybackState();
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
				final int mode = getMediaController().getShuffleMode();
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
				final int mode = getMediaController().getRepeatMode();
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
		final int duration = (int) getMediaController().getMetadata()
				.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
		_binding.nowPlayingSeekBar.setMax(duration);
	}

	private void _updateFromPlaybackState(PlaybackStateCompat state) {
		final int colorAccent = ThemeUtil.getColor(getContext(), R.attr.colorAccent);
		final int colorIconDefault = ThemeUtil.getColor(getContext(), R.attr.tint);
		if (state != null) {
			if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
				_binding.nowPlayingBtnPlay.setImageDrawable(ContextCompat.getDrawable(getContext(),
						R.drawable.ic_pause));
				DrawableCompat.setTint(_binding.nowPlayingBtnPlay.getDrawable(),
						colorAccent);
			}
			else {
				_binding.nowPlayingBtnPlay.setImageDrawable(ContextCompat.getDrawable(getContext(),
						R.drawable.ic_play_arrow));
				DrawableCompat.setTint(_binding.nowPlayingBtnPlay.getDrawable(),
						colorIconDefault);
			}
		}
	}

	private void _updateFromShuffleMode(int shuffleMode) {
		final int colorAccent = ThemeUtil.getColor(getContext(), R.attr.colorAccent);
		final int colorIconDefault = ThemeUtil.getColor(getContext(), R.attr.tint);
		if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
			DrawableCompat.setTint(_binding.nowPlayingBtnShuffle.getDrawable(),
					colorAccent);
		}
		else {
			DrawableCompat.setTint(_binding.nowPlayingBtnShuffle.getDrawable(),
					colorIconDefault);
		}
	}

	private void _updateFromRepeatMode(int repeatMode) {
		final int colorAccent = ThemeUtil.getColor(getContext(), R.attr.colorAccent);
		final int colorIconDefault = ThemeUtil.getColor(getContext(), R.attr.tint);
		if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
			_binding.nowPlayingBtnRepeat
					.setImageDrawable(getContext().getDrawable(R.drawable.ic_repeat));
			DrawableCompat.setTint(_binding.nowPlayingBtnRepeat.getDrawable(),
					colorAccent);
		}
		else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
			_binding.nowPlayingBtnRepeat
					.setImageDrawable(getContext().getDrawable(R.drawable.exo_controls_repeat_one));
		}
		else {
			_binding.nowPlayingBtnRepeat
					.setImageDrawable(getContext().getDrawable(R.drawable.ic_repeat));
			DrawableCompat.setTint(_binding.nowPlayingBtnRepeat.getDrawable(),
					colorIconDefault);
		}
	}
}
