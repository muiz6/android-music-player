package com.muiz6.musicplayer.ui.main;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.databinding.ActivityMainBinding;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.MyConnectionCallback;
import com.muiz6.musicplayer.ui.SettingActivity;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

public class MainActivity extends AppCompatActivity
	implements MediaBrowserFragment.MediaFragmentListener {

    private static final String _TAG = "MainActivity";
    private ActivityMainBinding _binding;
    private MediaBrowserCompat _mediaBrowser;
    private _MediaControllerCallback _controllerCallback;
    private MyConnectionCallback _connectionCallback;

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
        _controllerCallback = new _MediaControllerCallback(this, _binding);
        _connectionCallback =
                new _ConnectionCallback(this, _controllerCallback, _binding);

        _mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                _connectionCallback,
                null);

        // Instantiate ViewPager, PagerAdapter and TabLayout
        final ViewPager viewPager = _binding.mainViewPager;
        final TabLayout tabLayout = _binding.mainTabs;
        final _MainPagerAdapter pagerAdapter = new _MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setTabIcons(tabLayout);

        // color tab bar icons
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(this,
                R.color.colorAccent),
                PorterDuff.Mode.SRC_IN);
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
        _connectionCallback.setMediaBrowser(_mediaBrowser);
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

    @Nullable
	@Override
	public MediaBrowserCompat getMediaBrowser() {
		return _mediaBrowser;
	}

    public void onClick(View view) {
        if (view == _binding.mainBottomAppbarSongTitle) {
            Intent intent = new Intent(MainActivity.this,
                    NowPlayingActivity.class);
            startActivity(intent);
        }
    }
}