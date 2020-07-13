package com.muiz6.musicplayer.ui.main.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentFactory;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.FragmentMainBinding;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.media.MediaConnectionCallback;
import com.muiz6.musicplayer.media.MediaControllerCallback;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.ThemeUtil;
import com.muiz6.musicplayer.ui.main.MediaBrowserFragment;

import javax.inject.Inject;
import javax.inject.Named;

@FragmentScope
public class MainFragment extends MediaBrowserFragment {

	private final MediaBrowserCompat _mediaBrowser;
	private final FragmentFactory _fragmentFactory;
	private FragmentMainBinding _binding; // only available on runtime

	// arged ctor for fragment factory
	@Inject
	public MainFragment(MediaBrowserCompat mediaBrowser,
			MediaConnectionCallback connectionCallback,
			MediaControllerCallback controllerCallback,
			@Named("MainFragment") FragmentFactory factory) {
		super(mediaBrowser, connectionCallback, controllerCallback);
		_mediaBrowser = mediaBrowser;
		_fragmentFactory = factory;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);

		getChildFragmentManager().setFragmentFactory(_fragmentFactory);
		final Intent intentMusicService = new Intent(getContext(), MusicService.class);
		ContextCompat.startForegroundService(context, intentMusicService);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_binding = FragmentMainBinding.bind(view);

		// setup toolbar
		final NavController navController = Navigation.findNavController(view);
		final AppBarConfiguration appBarConfiguration =
				new AppBarConfiguration.Builder(navController.getGraph()).build();
		NavigationUI.setupWithNavController(_binding.mainToolbar,
				navController,
				appBarConfiguration);

		// final NavController navController = Navigation.findNavController(view);
		_binding.mainBottomBarSongTitle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				navController.navigate(R.id.main_player_fragment);
			}
		});

		// setup tab layout
		final _MainPagerAdapter pagerAdapter = new _MainPagerAdapter(this, _mediaBrowser);
		_binding.mainViewPager.setAdapter(pagerAdapter);
		new TabLayoutMediator(_binding.mainTabLayout,
				_binding.mainViewPager,
				new _TabMediator(getContext())).attach();
		_binding.mainTabLayout.addOnTabSelectedListener(new _TabListener(getContext()));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// to avoid memory leaks
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
			final MediaMetadataCompat metadata = mediaController.getMetadata();
			final PlaybackStateCompat pbState = mediaController.getPlaybackState();
			_updateFromMetadata(metadata);
			_updateFromPlaybackState(pbState);
		}
		_buildTransportControls();
	}

	// following methods
	// belong to
	// MediaControllerCallback.Listener

	@Override
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
		super.onPlaybackStateChanged(state);

		_updateFromPlaybackState(state);
	}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
		super.onMetadataChanged(metadata);

		_updateFromMetadata(metadata);
	}

	private void _updateFromMetadata(MediaMetadataCompat metadata) {
		if (metadata != null) {

			// make bottom appbar visible if metadata exists
			final LinearLayout bottomAppbar = _binding.mainBottomBar;
			final ViewPager2 viewPager = _binding.mainViewPager;
			if (bottomAppbar.getVisibility() != View.VISIBLE) {
				bottomAppbar.setVisibility(View.VISIBLE);
				viewPager.setPadding(viewPager.getPaddingStart(), viewPager.getPaddingTop(),
						viewPager.getPaddingEnd(), bottomAppbar.getHeight());
			}
			final String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
			if (title != null) {
				_binding.mainBottomBarSongTitle.setText(title);
			}
		}
	}

	private void _updateFromPlaybackState(PlaybackStateCompat pbState) {
		if (pbState != null) {
			final ImageButton btn = _binding.mainBottomBarBtnPlayPause;
			Drawable icon = getContext().getDrawable(R.drawable.ic_play_arrow);
			int color = ThemeUtil.getColor(getContext(), R.attr.tint);
			if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
				color = ThemeUtil.getColor(getContext(), R.attr.colorAccent);
				icon = getContext().getDrawable(R.drawable.ic_pause);
			}
			btn.setImageDrawable(icon);
			DrawableCompat.setTint(btn.getDrawable(), color);
		}
	}

	private void _buildTransportControls() {
		final MediaControllerCompat mediaController = getMediaController();
		if (mediaController != null) {
			final MediaControllerCompat.TransportControls transportControls =
					mediaController.getTransportControls();
			_binding.mainBottomBarBtnPlayPause.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final PlaybackStateCompat pbState = mediaController.getPlaybackState();
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
			_binding.mainBottomBarBtnNext.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					transportControls.skipToNext();
				}
			});
			_binding.mainBottomBarBtnPrevious.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					transportControls.skipToPrevious();
				}
			});
		}
	}
}
