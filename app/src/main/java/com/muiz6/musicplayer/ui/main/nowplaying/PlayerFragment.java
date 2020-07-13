package com.muiz6.musicplayer.ui.main.nowplaying;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentPlayerBinding;
import com.muiz6.musicplayer.media.MediaControllerCallback;
import com.muiz6.musicplayer.ui.ThemeUtil;

import java.util.List;

public class PlayerFragment extends Fragment
	implements MediaControllerCallback.Listener,
	SeekBar.OnSeekBarChangeListener {

	private final Handler _handler = new Handler();
	private final MediaControllerCallback _mediaControllerCallback =
			new MediaControllerCallback();
	private MediaControllerCompat _mediaController;
	private FragmentPlayerBinding _binding;
	private Runnable _seekBarRunnable = new Runnable() {
		@Override
		public void run() {
			int progress = (int) _mediaController.getPlaybackState().getPosition();
			_binding.nowPlayingSeekBar.setProgress(progress);
			_handler.postDelayed(this, 34); // at 30Hz
		}
	};

	// public ctor for framework
	public PlayerFragment() {}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		_mediaControllerCallback.addListener(this);
		_mediaController = MediaControllerCompat.getMediaController(getActivity());
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

		if (_mediaController != null) {
			_mediaController.registerCallback(_mediaControllerCallback);
			_buildTransportControls();
			_updateFromMetadata(_mediaController.getMetadata());
			_updateFromPlaybackState(_mediaController.getPlaybackState());

			_binding.nowPlayingSeekBar.setOnSeekBarChangeListener(this);

			// todo: improve seek bar progress
			getActivity().runOnUiThread(_seekBarRunnable);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (_mediaController != null) {
			_mediaController.unregisterCallback(_mediaControllerCallback);
		}
		_binding = null;
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
	public void onSessionReady() {

	}

	@Override
	public void onSessionDestroyed() {

	}

	@Override
	public void onSessionEvent(String event, Bundle extras) {

	}

	@Override
	public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {

	}

	@Override
	public void onQueueTitleChanged(CharSequence title) {

	}

	@Override
	public void onExtrasChanged(Bundle extras) {

	}

	@Override
	public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {

	}

	@Override
	public void onCaptioningEnabledChanged(boolean enabled) {

	}

	@Override
	public void onRepeatModeChanged(int repeatMode) {

	}

	@Override
	public void onShuffleModeChanged(int shuffleMode) {

	}

	private void _buildTransportControls() {
		final MediaControllerCompat.TransportControls transportControls =
				_mediaController.getTransportControls();
		_binding.nowPlayingBtnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final PlaybackStateCompat pbState = _mediaController.getPlaybackState();
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
				final int mode = _mediaController.getShuffleMode();
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
				final int mode = _mediaController.getRepeatMode();
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

	// following methods
	// belong to
	// SeekBar.OnSeekBarChangeListener

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			_mediaController.getTransportControls().seekTo(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}

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
		final int duration = (int) _mediaController.getMetadata()
				.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
		_binding.nowPlayingSeekBar.setMax(duration);
	}

	private void _updateFromPlaybackState(PlaybackStateCompat state) {
		final int shuffleMode = _mediaController.getShuffleMode();
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

		final int repeatMode = _mediaController.getRepeatMode();
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
}
