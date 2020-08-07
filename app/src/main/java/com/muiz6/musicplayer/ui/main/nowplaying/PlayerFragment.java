package com.muiz6.musicplayer.ui.main.nowplaying;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentPlayerBinding;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.ThemeUtil;

import javax.inject.Inject;

@FragmentScope
public class PlayerFragment extends Fragment
		implements View.OnClickListener,
		SeekBar.OnSeekBarChangeListener {

	private final ViewModelProvider.Factory _viewModelFactory;
	private FragmentPlayerBinding _binding;
	private PlayerViewModel _viewModel;
	// private final Handler _handler = new Handler();
	// private ExecutorService _executorService = Executors.newSingleThreadExecutor();
	// private Future _longRunningTaskFuture;
	// private Runnable _seekBarRunnable = new Runnable() {
	// 	@Override
	// 	public void run() {
	// 		if (_binding != null) {
	// 			int progress = (int) getMediaController().getPlaybackState().getPosition();
	// 			_binding.nowPlayingSeekBar.setProgress(progress);
	// 		}
	// 		_handler.postDelayed(this, 34); // at 30Hz
	// 	}
	// };

	@Inject
	public PlayerFragment(ViewModelProvider.Factory viewModelFactory) {
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(PlayerViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentPlayerBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup toolbar
		final NavController navController = Navigation.findNavController(view);
		NavigationUI.setupWithNavController(_binding.playerToolbar, navController);

		// sync with media session
		_viewModel.getPlayBackState().observe(getViewLifecycleOwner(),
				new Observer<PlaybackStateCompat>() {

					@Override
					public void onChanged(PlaybackStateCompat pbState) {
						if (pbState != null) {
							_updateFromPlaybackState(pbState);

							_updateFromRepeatMode(_viewModel.getRepeatMode());
							_updateFromShuffleMode(_viewModel.getShuffleMode());
						}
					}
				});
		_viewModel.getMetadata().observe(getViewLifecycleOwner(),
				new Observer<MediaMetadataCompat>() {

					@Override
					public void onChanged(MediaMetadataCompat metadata) {
						if (metadata != null) {
							_updateFromMetadata(metadata);
						}
					}
				});

		// build transport controls
		_binding.playerBtnPlayPause.setOnClickListener(this);
		_binding.playerBtnNext.setOnClickListener(this);
		_binding.playerBtnPrevious.setOnClickListener(this);
		_binding.playerBtnShuffle.setOnClickListener(this);
		_binding.playerBtnRepeat.setOnClickListener(this);
		_binding.playerFab.setOnClickListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// if (_longRunningTaskFuture != null) {
		// 	_longRunningTaskFuture.cancel(true);
		// }
		_binding = null;
	}

	// following methods
	// belong to
	// View.OnClickListener

	@Override
	public void onClick(View view) {
		if (view == _binding.playerBtnPlayPause) {
			_viewModel.onPlayPauseBtnClicked();
		}
		else if (view == _binding.playerBtnNext) {
			_viewModel.onSkipNextBtnClicked();
		}
		else if (view == _binding.playerBtnPrevious) {
			_viewModel.onSkipPreviousBtnClicked();
		}
		else if (view == _binding.playerBtnShuffle) {
			_viewModel.onShuffleBtnClicked();
		}
		else if (view == _binding.playerBtnRepeat) {
			_viewModel.onRepeatBtnClicked();
		}
		else if (view == _binding.playerFab) {
			final NavController navController = Navigation.findNavController(getView());
			navController.navigate(R.id.main_queue_fragment);
		}
	}

	// following methods
	// belong to
	// SeekBar.OnSeekBarChangeListener

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			// final MediaControllerCompat mediaController = getMediaController();
			// if (mediaController != null) {
			// 	mediaController.getTransportControls().seekTo(progress);
			// }
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}

	// private void _buildTransportControls() {
	// 	_binding.nowPlayingSeekBar.setOnSeekBarChangeListener(this);
	//
	// 	// todo: optimize seek bar progress
	// 	_longRunningTaskFuture = _executorService.submit(_seekBarRunnable);
	// }

	private void _updateFromMetadata(MediaMetadataCompat metadata) {
		final String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
		if (title != null) {
			_binding.playerTitle.setText(title);
		}
		final String artist = metadata
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
		if (artist != null) {
			_binding.playerArtist.setText(artist);
		}
		else {
			_binding.playerArtist.setText(R.string.unknown_artist);
		}
		final String album = metadata
				.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION);
		if (album != null) {
			_binding.playerAlbum.setText(album);
		}
		else {
			_binding.playerAlbum.setText(R.string.unknown_album);
		}
		// final int duration = (int) getMediaController().getMetadata()
		// 		.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
		// _binding.nowPlayingSeekBar.setMax(duration);
	}

	private void _updateFromPlaybackState(PlaybackStateCompat state) {
		final int colorAccent = ThemeUtil.getColor(getContext(), R.attr.colorSecondary);
		final int colorIconDefault = ThemeUtil.getColor(getContext(), R.attr.colorOnSurface);
		if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
			_binding.playerBtnPlayPause.setImageDrawable(ContextCompat.getDrawable(getContext(),
					R.drawable.ic_pause));
			DrawableCompat.setTint(_binding.playerBtnPlayPause.getDrawable(),
					colorAccent);
		}
		else {
			_binding.playerBtnPlayPause.setImageDrawable(ContextCompat.getDrawable(getContext(),
					R.drawable.ic_play_arrow));
			DrawableCompat.setTint(_binding.playerBtnPlayPause.getDrawable(),
					colorIconDefault);
		}
	}

	private void _updateFromShuffleMode(int shuffleMode) {
		final int colorAccent = ThemeUtil.getColor(getContext(), R.attr.colorSecondary);
		final int colorIconDefault = ThemeUtil.getColor(getContext(), R.attr.colorOnSurface);
		if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
			DrawableCompat.setTint(_binding.playerBtnShuffle.getDrawable(),
					colorAccent);
		}
		else {
			DrawableCompat.setTint(_binding.playerBtnShuffle.getDrawable(),
					colorIconDefault);
		}
	}

	private void _updateFromRepeatMode(int repeatMode) {
		final int colorAccent = ThemeUtil.getColor(getContext(), R.attr.colorSecondary);
		final int colorIconDefault = ThemeUtil.getColor(getContext(), R.attr.colorOnSurface);
		if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
			_binding.playerBtnRepeat
					.setImageDrawable(getContext().getDrawable(R.drawable.ic_repeat));
			DrawableCompat.setTint(_binding.playerBtnRepeat.getDrawable(),
					colorAccent);
		}
		else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
			_binding.playerBtnRepeat
					.setImageDrawable(getContext().getDrawable(R.drawable.exo_controls_repeat_one));
		}
		else {
			_binding.playerBtnRepeat
					.setImageDrawable(getContext().getDrawable(R.drawable.ic_repeat));
			DrawableCompat.setTint(_binding.playerBtnRepeat.getDrawable(),
					colorIconDefault);
		}
	}
}
