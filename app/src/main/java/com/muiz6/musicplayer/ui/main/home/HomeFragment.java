package com.muiz6.musicplayer.ui.main.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentHomeBinding;
import com.muiz6.musicplayer.ui.ThemeUtil;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements View.OnClickListener {

	private final FragmentFactory _fragmentFactory;
	private final TabLayoutMediator.TabConfigurationStrategy _tabMediatorStrategy;
	private final TabLayout.OnTabSelectedListener _tabListener;
	private final ViewModelProvider.Factory _viewModelFactory;
	private HomeViewModel _viewModel;
	private FragmentHomeBinding _binding; // only available on runtime

	// arged ctor for fragment factory
	@Inject
	public HomeFragment(FragmentFactory factory,
			ViewModelProvider.Factory viewModelFactory,
			TabLayoutMediator.TabConfigurationStrategy tabMediatorStrategy,
			TabLayout.OnTabSelectedListener tabListener) {
		_fragmentFactory = factory;
		_tabMediatorStrategy = tabMediatorStrategy;
		_tabListener = tabListener;
		_viewModelFactory = viewModelFactory;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		_viewModel = new ViewModelProvider(this, _viewModelFactory).get(HomeViewModel.class);
		getChildFragmentManager().setFragmentFactory(_fragmentFactory);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		_binding = FragmentHomeBinding.inflate(inflater, container, false);
		return _binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup toolbar
		final NavController navController = Navigation.findNavController(view);
		final AppBarConfiguration appBarConfiguration =
				new AppBarConfiguration.Builder(navController.getGraph()).build();
		NavigationUI.setupWithNavController(_binding.mainToolbar,
				navController,
				appBarConfiguration);

		// setup tab layout
		_binding.mainViewPager.setAdapter(new HomePagerAdapter(this,
				getActivity().getClassLoader(),
				_fragmentFactory));
		new TabLayoutMediator(_binding.mainTabLayout,
				_binding.mainViewPager,
				_tabMediatorStrategy).attach();
		_binding.mainTabLayout.addOnTabSelectedListener(_tabListener);

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
		_binding.mainBottomBarBtnPlayPause.setOnClickListener(this);
		_binding.mainBottomBarBtnNext.setOnClickListener(this);
		_binding.mainBottomBarBtnPrevious.setOnClickListener(this);
		_binding.mainBottomBarSongTitle.setOnClickListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// to avoid memory leaks
		_binding = null;
	}

	private void _updateFromMetadata(MediaMetadataCompat metadata) {

		// make bottom appbar visible if metadata exists
		if (_binding.mainBottomBar.getVisibility() != View.VISIBLE) {
			_binding.mainBottomBarSongTitle.setSelected(true); // for marquee text
			_binding.mainBottomBar.setVisibility(View.VISIBLE);

			// todo: not working for some reason
			_binding.mainViewPager.setPadding(_binding.mainViewPager.getPaddingStart(),
					_binding.mainViewPager.getPaddingTop(),
					_binding.mainViewPager.getPaddingEnd(),
					_binding.mainBottomBar.getHeight());
		}
		final String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
		if (title != null) {
			_binding.mainBottomBarSongTitle.setText(title);
		}
	}

	private void _updateFromPlaybackState(PlaybackStateCompat pbState) {
		final ImageButton btn = _binding.mainBottomBarBtnPlayPause;
		final Context context = getContext();
		if (context != null) {
			Drawable icon = context.getDrawable(R.drawable.ic_play_arrow);
			int color = ThemeUtil.getColor(getContext(), R.attr.tint);
			if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
				color = ThemeUtil.getColor(getContext(), R.attr.colorAccent);
				icon = context.getDrawable(R.drawable.ic_pause);
			}
			btn.setImageDrawable(icon);
			DrawableCompat.setTint(btn.getDrawable(), color);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == _binding.mainBottomBarBtnPlayPause) {
			_viewModel.onPlayPauseBtnClicked();
		}
		else if (view == _binding.mainBottomBarBtnNext) {
			_viewModel.onSkipNextBtnClicked();
		}
		else if (view == _binding.mainBottomBarBtnPrevious) {
			_viewModel.onSkipPreviousBtnClicked();
		}
		else if (view == _binding.mainBottomBarSongTitle) {
			final NavController navController = Navigation.findNavController(getView());
			navController.navigate(R.id.main_player_fragment);
		}
	}
}