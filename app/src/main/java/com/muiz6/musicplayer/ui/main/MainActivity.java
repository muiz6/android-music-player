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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.musicservice.MusicService;
import com.muiz6.musicplayer.ui.SettingActivity;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

public class MainActivity extends AppCompatActivity {

    private static final String _TAG = "MainActivity";
    private TextView _playerTitle;
    private MediaBrowserCompat _mediaBrowser;
    private MediaControllerCallback _controllerCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // initializing ui components
        _playerTitle = findViewById(R.id.main_bottom_appbar_song_title);
        _playerTitle.setSelected(true); // for marquee text

        // Instantiate ViewPager, PagerAdapter and TabLayout
        final ViewPager viewPager = findViewById(R.id.main_view_pager);
        final TabLayout tabLayout = findViewById(R.id.main_tabs);
        final MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setTabIcons(tabLayout);

        // color tab bar icons
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(this,
                R.color.colorAccent),
                PorterDuff.Mode.SRC_IN);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(MainActivity.this,
                    R.color.colorAccent);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(MainActivity.this,
                    R.color.colorGreyLight);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });

        // setup media browser and media controller
        _ConnectionCallback connectionCallback =
                new _ConnectionCallback(this);

        _mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                connectionCallback,
                null);
        _controllerCallback = new MediaControllerCallback(this);
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
        Intent intent= new Intent(this, MusicService.class);
        ContextCompat.startForegroundService(this, intent);
        _mediaBrowser.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    public MediaControllerCompat.Callback getMediaControllerCallback() {
        return _controllerCallback;
    }

    public MediaBrowserCompat getMediaBrowser() {
        return _mediaBrowser;
    }

    public void onClick(View view) {
        if (view == _playerTitle) {
            Intent intent = new Intent(MainActivity.this,
                    NowPlayingActivity.class);
            startActivity(intent);
        }
    }
}