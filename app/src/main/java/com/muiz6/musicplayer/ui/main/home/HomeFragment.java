package com.muiz6.musicplayer.ui.main.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.BottomSheetPlayerBinding;
import com.muiz6.musicplayer.databinding.FragmentHomeBinding;
import com.muiz6.musicplayer.ui.main.home.browse.BrowseFragmentDirections;
import com.muiz6.musicplayer.ui.main.home.library.LibraryFragmentDirections;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements View.OnClickListener,
		SeekBar.OnSeekBarChangeListener {

	private final BottomSheetBehavior.BottomSheetCallback _bottomSheetCallback =
			new BottomSheetBehavior.BottomSheetCallback() {

				@Override
				public void onStateChanged(@NonNull View bottomSheet, int newState) {}

				@Override
				public void onSlide(@NonNull View bottomSheet, float slideOffset) {
					_binding.homeBg.setVisibility(View.VISIBLE);
					_binding.homeBg.setAlpha(slideOffset);
					_bindingBottomSheet.bottomSheetCollapsedView.setAlpha(1 - slideOffset);
				}
			};
	private final Observer<Integer> _observerSeekBarPosition = new Observer<Integer>() {

		@Override
		public void onChanged(Integer position) {
			_bindingBottomSheet.bottomSheetSeekBar.setProgress(position);
		}
	};
	private final FragmentFactory _fragmentFactory;
	private final ViewModelProvider.Factory _viewModelFactory;
	private HomeViewModel _viewModel;
	private FragmentHomeBinding _binding; // only available on runtime
	private BottomSheetPlayerBinding _bindingBottomSheet;
	private BottomSheetBehavior<View> _behaviourBottomSheet;
	private Resources.Theme _theme;

	// arged ctor for fragment factory
	@Inject
	public HomeFragment(FragmentFactory fragmentFactory,
			ViewModelProvider.Factory viewModelFactory) {
		_fragmentFactory = fragmentFactory;
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		getChildFragmentManager().setFragmentFactory(_fragmentFactory);
		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(HomeViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentHomeBinding.inflate(inflater, container, false);
		_bindingBottomSheet = BottomSheetPlayerBinding.bind(_binding.homeBottomSheet.getRoot());
		_theme = requireActivity().getTheme();
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// add navigation listener to home navigation fragment
		// todo: not working smh ;C
		// final NavController navController = Navigation.findNavController(view);
		// navController.addOnDestinationChangedListener(this);

		// setup bottom sheet
		_behaviourBottomSheet = BottomSheetBehavior.<View>from(_bindingBottomSheet.getRoot());
		_behaviourBottomSheet.addBottomSheetCallback(_bottomSheetCallback);

		// needed for marquee text
		_bindingBottomSheet.bottomSheetCollapsedViewSongTitle.setSelected(true);

		// sync ui with playback
		_observe();

		// hide view if bottom sheet is expanded by default after config change
		// if (_behaviourBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
		// 	_bindingBottomSheet.bottomSheetCollapsedView.setAlpha(0);
		// }

		// build transport controls
		_bindingBottomSheet.bottomSheetCollapsedViewBtnPlayPause.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetCollapsedViewBtnExpandCollapse.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetBtnPlayPause.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetBtnNext.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetBtnPrevious.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetBtnShuffle.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetBtnRepeat.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetFab.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetSeekBar.setOnSeekBarChangeListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// to avoid memory leaks
		_binding = null;
		_bindingBottomSheet = null;
		_theme = null;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.bottom_sheet_collapsed_view_btn_play_pause:
			case R.id.bottom_sheet_btn_play_pause:
				_viewModel.onPlayPauseBtnClicked();
				break;
			case R.id.bottom_sheet_btn_next:
				_viewModel.onSkipNextBtnClicked();
				break;
			case R.id.bottom_sheet_btn_previous:
				_viewModel.onSkipPreviousBtnClicked();
				break;
			case R.id.bottom_sheet_collapsed_view_btn_expand_collapse:
				final int sheetState = _behaviourBottomSheet.getState();
				if (sheetState == BottomSheetBehavior.STATE_COLLAPSED) {
					_behaviourBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
				}
				else if (sheetState == BottomSheetBehavior.STATE_EXPANDED) {
					_behaviourBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
				}
				break;
			case R.id.bottom_sheet_btn_repeat:
				_viewModel.onRepeatBtnClicked();
				break;
			case R.id.bottom_sheet_btn_shuffle:
				_viewModel.onShuffleBtnClicked();
				break;
			case R.id.bottom_sheet_fab:
				final NavController navController = Navigation
						.findNavController(_binding.homeNavHostFragment);
				int destination = navController.getCurrentDestination().getId();
				if (destination != R.id.queueFragment) {
					if (destination == R.id.browseFragment) {
						navController.navigate(BrowseFragmentDirections
								.actionBrowseFragmentToQueueFragment());
					}
					else {
						navController.navigate(LibraryFragmentDirections
								.actionLibraryFragmentToQueueFragment());
					}
				}
				_behaviourBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
		if (fromUser) {
			_viewModel.onSeek(position);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

		// stop updating seek bar progress on ui
		_viewModel.getSeekBarPosition().removeObserver(_observerSeekBarPosition);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		// resume updating seek bar progress on ui
		_viewModel.getSeekBarPosition().observe(getViewLifecycleOwner(), _observerSeekBarPosition);
	}

	private void _observe() {
		final LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
		_viewModel.getAlbumArt().observe(lifecycleOwner, new Observer<Bitmap>() {

			@Override
			public void onChanged(Bitmap bitmap) {
				_bindingBottomSheet.bottomSheetAlbumArt.setImageBitmap(bitmap);
			}
		});
		_viewModel.getSongTitle().observe(lifecycleOwner, new Observer<String>() {

			@Override
			public void onChanged(String s) {
				_bindingBottomSheet.bottomSheetCollapsedViewSongTitle.setText(s);
			}
		});
		_viewModel.getPlayPauseIconResId().observe(lifecycleOwner,
				new Observer<Integer>() {

					@Override
					public void onChanged(@DrawableRes Integer id) {
						_bindingBottomSheet.bottomSheetCollapsedViewBtnPlayPause
								.setImageDrawable(ResourcesCompat
										.getDrawable(getResources(), id, _theme));
						_bindingBottomSheet.bottomSheetBtnPlayPause
								.setImageDrawable(ResourcesCompat
										.getDrawable(getResources(), id, _theme));

						// setting activated shall change drawable color
						// from color state list defined in xml layout
						if (id == R.drawable.ic_pause) {
							_bindingBottomSheet
									.bottomSheetCollapsedViewBtnPlayPause.setActivated(true);
							_bindingBottomSheet
									.bottomSheetBtnPlayPause.setActivated(true);
						}
						else {
							_bindingBottomSheet
									.bottomSheetCollapsedViewBtnPlayPause.setActivated(false);
							_bindingBottomSheet
									.bottomSheetBtnPlayPause.setActivated(false);
						}
					}
				});
		_viewModel.getShuffleState().observe(lifecycleOwner, new Observer<Boolean>() {

			@Override
			public void onChanged(Boolean state) {
				_bindingBottomSheet.bottomSheetBtnShuffle.setActivated(state);
			}
		});
		_viewModel.getRepeatIcon().observe(lifecycleOwner, new Observer<Pair<Integer, Boolean>>() {

			@Override
			public void onChanged(Pair<Integer, Boolean> state) {
				_bindingBottomSheet.bottomSheetBtnRepeat.setImageDrawable(ResourcesCompat
						.getDrawable(getResources(), state.first, _theme));
				_bindingBottomSheet.bottomSheetBtnRepeat.setActivated(state.second);
			}
		});
		_viewModel.getSeekBarMaxValue().observe(lifecycleOwner, new Observer<Integer>() {

			@Override
			public void onChanged(Integer max) {
				_bindingBottomSheet.bottomSheetSeekBar.setMax(max);
			}
		});
		_viewModel.getSeekBarPosition().observe(lifecycleOwner, _observerSeekBarPosition);
	}
}
