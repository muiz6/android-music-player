package com.muiz6.musicplayer.ui.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.ActivityMainBinding;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.MyConnectionCallback;
import com.muiz6.musicplayer.ui.MyControllerCallback;
import com.muiz6.musicplayer.ui.SettingActivity;
import com.muiz6.musicplayer.ui.ThemeUtil;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

public class MainActivity extends AppCompatActivity
		implements MyConnectionCallback.Listener,
		MyControllerCallback.Listener {

	private MyControllerCallback _controllerCallback;
	private static final String _TAG = "MainActivity";
	private ActivityMainBinding _binding;
	private MediaBrowserCompat _mediaBrowser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(_binding.getRoot());

		final Toolbar toolbar = _binding.mainToolbar;
		setSupportActionBar(toolbar);

		// initializing ui components
		_binding.mainBottomAppbarSongTitle.setSelected(true); // for marquee text

		// setup media browser and media controller
		_controllerCallback = new MyControllerCallback(this);

		_mediaBrowser = new MediaBrowserCompat(this,
				new ComponentName(this, MusicService.class),
				new MyConnectionCallback(this),
				null);

		// Instantiate ViewPager, PagerAdapter and TabLayout
		final ViewPager viewPager = _binding.mainViewPager;
		final TabLayout tabLayout = _binding.mainTabs;
		final _MainPagerAdapter pagerAdapter = new _MainPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
		tabLayout.setupWithViewPager(viewPager);
		pagerAdapter.setTabIcons(tabLayout);

		// color tab bar icons
		final TabLayout.Tab tab = tabLayout.getTabAt(0);
		if (tab != null) {
			final Drawable tabIcon = tab.getIcon();
			if (tabIcon != null) {
				final int color = ThemeUtil.getColor(this, R.attr.colorAccent);
				DrawableCompat.setTint(tabIcon, color);
			}
		}
		tabLayout.addOnTabSelectedListener(new _TabListener(viewPager));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();

		// start service and let media browser bind to it
		final Intent intent= new Intent(this, MusicService.class);
		ContextCompat.startForegroundService(this, intent);
		_mediaBrowser.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// so that default volume button action is media volume increase/decrease
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			final Intent intent = new Intent(this, SettingActivity.class);
			ContextCompat.startActivity(this, intent, null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// (see "stay in sync with the MediaSession")
		if (MediaControllerCompat.getMediaController(this) != null) {
			MediaControllerCompat.getMediaController(this).unregisterCallback(_controllerCallback);
		}
		_mediaBrowser.disconnect();
	}

	// following methods
	// belong to
	// MyConnectionCallback.Listener

	@Override
	public void onConnected() {
		final MediaControllerCompat.TransportControls transportControls = MediaControllerCompat
				.getMediaController(this).getTransportControls();

		// Grab the view for the play/pause button
		final ImageButton playPauseBtn = _binding.mainBottomAppbarBtnPlay;

		// Attach a listener to the button
		playPauseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final PlaybackStateCompat pbState = MediaControllerCompat
						.getMediaController(MainActivity.this)
						.getPlaybackState();
				if (pbState != null) {
					if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
						transportControls.pause();
					}
					else if (pbState.getState() == PlaybackStateCompat.STATE_PAUSED) {
						transportControls.play();
					}
				}
			}
		});

		final ImageButton btnSkipPrev = _binding.mainBottomAppbarBtnPrevious;
		btnSkipPrev.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				transportControls.skipToPrevious();
			}
		});

		final ImageButton btnSkipNext = _binding.mainBottomAppbarBtnNext;
		btnSkipNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				transportControls.skipToNext();
			}
		});

		final MediaControllerCompat mediaController =
				MediaControllerCompat.getMediaController(this);
		_updateBottomBar(mediaController.getMetadata());
		_updatePlayPauseBtn(mediaController.getPlaybackState());
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

	// following methods
	// belong to
	// MyControllerCallback.Listener

	@Override
	public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
		_updatePlayPauseBtn(state);
	}

	@Override
	public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
		_updateBottomBar(metadata);
	}

	// following methods
	// are specific to
	// this activity

	public void onClick(View view) {
		if (view == _binding.mainBottomAppbarSongTitle) {
			Intent intent = new Intent(MainActivity.this,
					NowPlayingActivity.class);
			startActivity(intent);
		}
	}

	private void _updateBottomBar(MediaMetadataCompat metadata) {
		if (metadata != null) {

			// make bottom appbar visible if metadata exists
			final LinearLayout bottomAppbar = _binding.mainBottomAppbar;
			final ViewPager viewPager = _binding.mainViewPager;
			bottomAppbar.setVisibility(View.VISIBLE);
			viewPager.setPadding(viewPager.getPaddingStart(), viewPager.getPaddingTop(),
					viewPager.getPaddingEnd(), bottomAppbar.getHeight());

			final String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE);
			if (title != null) {
				_binding.mainBottomAppbarSongTitle.setText(title);
			}
		}
	}

	private void _updatePlayPauseBtn(PlaybackStateCompat pbState) {
		if (pbState != null) {
			final ImageButton btn = _binding.mainBottomAppbarBtnPlay;
			Drawable icon = getDrawable(R.drawable.ic_play_arrow);
			int color = ThemeUtil.getColor(this, R.attr.tint);
			if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
				color = ThemeUtil.getColor(this, R.attr.colorAccent);
				icon = getDrawable(R.drawable.ic_pause);
			}
			btn.setImageDrawable(icon);
			DrawableCompat.setTint(btn.getDrawable(), color);
		}
	}
}