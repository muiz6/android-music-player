package com.muiz6.musicplayer.ui.main.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.BottomSheetPlayerBinding;
import com.muiz6.musicplayer.databinding.FragmentHomeBinding;
import com.muiz6.musicplayer.ui.ThemeUtil;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements View.OnClickListener,
		NavController.OnDestinationChangedListener {

	private final BottomSheetBehavior.BottomSheetCallback _bottomSheetCallback =
			new BottomSheetBehavior.BottomSheetCallback() {

				@Override
				public void onStateChanged(@NonNull View bottomSheet, int newState) {
					if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
						_binding.homeBg.setVisibility(View.GONE);
						_bindingBottomSheet.bottomSheetCollapsedViewBtnExpandCollapse
								.setImageDrawable(_drawableUpArrow);
						_bindingBottomSheet.bottomSheetCollapsedViewBtnPrevious
								.setVisibility(View.VISIBLE);
						_bindingBottomSheet.bottomSheetCollapsedViewBtnPlayPause
								.setVisibility(View.VISIBLE);
						_bindingBottomSheet.bottomSheetCollapsedViewBtnNext
								.setVisibility(View.VISIBLE);
					}
					if (newState == BottomSheetBehavior.STATE_EXPANDED) {
						_bindingBottomSheet.bottomSheetCollapsedViewBtnExpandCollapse
								.setImageDrawable(_drawabledownArrow);
						_bindingBottomSheet.bottomSheetCollapsedViewBtnPrevious
								.setVisibility(View.GONE);
						_bindingBottomSheet.bottomSheetCollapsedViewBtnPlayPause
								.setVisibility(View.GONE);
						_bindingBottomSheet.bottomSheetCollapsedViewBtnNext
								.setVisibility(View.GONE);
					}
				}

				@Override
				public void onSlide(@NonNull View bottomSheet, float slideOffset) {
					_binding.homeBg.setVisibility(View.VISIBLE);
					_binding.homeBg.setAlpha(slideOffset);
				}
			};
	private final FragmentFactory _fragmentFactory;
	private final ViewModelProvider.Factory _viewModelFactory;
	private HomeViewModel _viewModel;
	private FragmentHomeBinding _binding; // only available on runtime
	private BottomSheetPlayerBinding _bindingBottomSheet;
	private Drawable _drawableUpArrow;
	private Drawable _drawabledownArrow;
	private Drawable _drawableCollapseArrowPlay;
	private Drawable _drawableArrowPlay;
	private Drawable _drawableCollapsePause;
	private Drawable _drawablePause;
	private int _colorSecondary;
	private int _colorOnSurface;
	private BottomSheetBehavior _behaviourBottomSheet;

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

		// initialize reusable resources
		final Activity activity = requireActivity();
		_drawableUpArrow = activity.getDrawable(R.drawable.ic_keyboard_arrow_up);
		_drawabledownArrow = activity.getDrawable(R.drawable.ic_keyboard_arrow_down);
		_drawableCollapseArrowPlay = activity.getDrawable(R.drawable.ic_play_arrow);
		_drawableArrowPlay = activity.getDrawable(R.drawable.ic_play_arrow);
		_drawableCollapsePause = activity.getDrawable(R.drawable.ic_pause);
		_drawablePause = activity.getDrawable((R.drawable.ic_pause));
		_colorSecondary = ThemeUtil.getColor(activity, R.attr.colorSecondary);
		_colorOnSurface = ThemeUtil.getColor(activity, R.attr.colorOnSurface);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentHomeBinding.inflate(inflater, container, false);
		_bindingBottomSheet = BottomSheetPlayerBinding.bind(_binding.homeBottomSheet.getRoot());
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// add navigation listener to home navigation fragment
		// todo: not working smh ;C
		// final NavController navController = Navigation.findNavController(view);
		// navController.addOnDestinationChangedListener(this);

		// sync with media session
		_viewModel.getMetadata().observe(getViewLifecycleOwner(),
				new Observer<MediaMetadataCompat>() {

					@Override
					public void onChanged(MediaMetadataCompat metadata) {
						_updateFromMetadata(metadata);
					}
				});
		_viewModel.getPlayBackState().observe(getViewLifecycleOwner(),
				new Observer<PlaybackStateCompat>() {

					@Override
					public void onChanged(PlaybackStateCompat pbState) {
						if (pbState != null) {
							_updateFromPlaybackState(pbState);
						}
					}
				});

		// build transport controls
		_bindingBottomSheet.bottomSheetCollapsedViewBtnPlayPause.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetCollapsedViewBtnNext.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetCollapsedViewBtnPrevious.setOnClickListener(this);
		_bindingBottomSheet.bottomSheetCollapsedViewBtnExpandCollapse.setOnClickListener(this);

		// setup bottom sheet
		_behaviourBottomSheet = BottomSheetBehavior.from(_bindingBottomSheet.getRoot());
		_behaviourBottomSheet.addBottomSheetCallback(_bottomSheetCallback);

		// needed for marquee text
		_bindingBottomSheet.bottomSheetCollapsedViewSongTitle.setSelected(true);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// to avoid memory leaks
		_binding = null;
		_bindingBottomSheet = null;
	}

	private void _updateFromMetadata(MediaMetadataCompat metadata) {

		// make bottom appbar visible if metadata exists
		// if (_binding.mainBottomBar.getVisibility() != View.VISIBLE) {
		// 	_binding.mainBottomBarSongTitle.setSelected(true); // for marquee text
		// 	_binding.mainBottomBar.setVisibility(View.VISIBLE);
		// }
		final String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
		if (title != null) {
			_bindingBottomSheet.bottomSheetCollapsedViewSongTitle.setText(title);
		}

		final Uri mediaUri = metadata.getDescription().getMediaUri();
		if (mediaUri!= null) {
			final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(requireContext(), mediaUri);
			final byte[] byteArr = retriever.getEmbeddedPicture();
			if (byteArr != null) {
				final Bitmap albumArt = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
				_bindingBottomSheet.bottomSheetAlbumArt.setImageBitmap(albumArt);
			}
		}
	}

	private void _updateFromPlaybackState(@NonNull PlaybackStateCompat pbState) {
		final ImageButton btn = _bindingBottomSheet.bottomSheetBtnPlayPause;
		final ImageButton btn2 = _bindingBottomSheet.bottomSheetCollapsedViewBtnPlayPause;
		if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
			btn.setImageDrawable(_drawablePause);
			btn2.setImageDrawable(_drawableCollapsePause);
			DrawableCompat.setTint(btn.getDrawable(), _colorSecondary);

			// todo: color not applying on btn2
			DrawableCompat.setTint(btn2.getDrawable(), _colorSecondary);
		}
		else {
			btn.setImageDrawable(_drawableArrowPlay);
			btn2.setImageDrawable(_drawableCollapseArrowPlay);
			DrawableCompat.setTint(btn.getDrawable(), _colorOnSurface);
			DrawableCompat.setTint(btn2.getDrawable(), _colorOnSurface);
		}

		// final ImageButton btn = _bottomSheetBinding.bottomSheetBtnPlayPause;
		// final ImageButton btn2 = _bottomSheetBinding.bottomSheetCollapsedViewBtnPlayPause;
		// final Context context = getContext();
		// if (context != null) {
		// 	Drawable icon = context.getDrawable(R.drawable.ic_play_arrow);
		// 	Drawable icon2 = context.getDrawable(R.drawable.ic_play_arrow);
		// 	int color = ThemeUtil.getColor(getContext(), R.attr.colorOnSurface);
		// 	if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
		// 		color = ThemeUtil.getColor(getContext(), R.attr.colorSecondary);
		// 		icon = context.getDrawable(R.drawable.ic_pause);
		// 		icon2 = context.getDrawable(R.drawable.ic_pause);
		// 	}
		//
		// 	// using same drawable object for both btns results in incorrect size
		// 	btn.setImageDrawable(icon);
		// 	btn2.setImageDrawable(icon2);
		// 	DrawableCompat.setTint(btn.getDrawable(), color);
		// 	DrawableCompat.setTint(btn2.getDrawable(), color);
		// }
	}

	@Override
	public void onClick(View view) {
		if (view == _bindingBottomSheet.bottomSheetCollapsedViewBtnPlayPause) {
			_viewModel.onPlayPauseBtnClicked();
		}
		else if (view == _bindingBottomSheet.bottomSheetCollapsedViewBtnNext) {
			_viewModel.onSkipNextBtnClicked();
		}
		else if (view == _bindingBottomSheet.bottomSheetCollapsedViewBtnPrevious) {
			_viewModel.onSkipPreviousBtnClicked();
		}
		else if (view == _bindingBottomSheet.bottomSheetCollapsedViewBtnExpandCollapse) {
			final int sheetState = _behaviourBottomSheet.getState();
			if (sheetState == BottomSheetBehavior.STATE_COLLAPSED) {
				_behaviourBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
			else if (sheetState == BottomSheetBehavior.STATE_EXPANDED) {
				_behaviourBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		}
		// else if (view == _binding.mainBottomBarSongTitle) {
		// 	final NavController navController = Navigation.findNavController(getView());
		// 	navController.navigate(R.id.main_player_fragment);
		// }
	}

	@Override
	public void onDestinationChanged(@NonNull NavController controller,
			@NonNull NavDestination destination,
			@Nullable Bundle arguments) {
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}
}
