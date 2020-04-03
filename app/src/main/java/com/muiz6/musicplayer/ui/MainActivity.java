package com.muiz6.musicplayer.ui;

import android.content.ComponentName;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.MusicService;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.callbacks.MainActivityMediaBrowserConnectionCallback;
import com.muiz6.musicplayer.callbacks.MainActivityMediaControllerCallback;
import com.muiz6.musicplayer.ui.adapters.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String _TAG = "MainActivity";
    private MediaBrowserCompat _mediaBrowser;
    private MainActivityMediaControllerCallback _controllerCallback;
    private FragmentManager _fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup media browser and media controller
        _controllerCallback = new MainActivityMediaControllerCallback(this);
        MainActivityMediaBrowserConnectionCallback connectionCallback =
                new MainActivityMediaBrowserConnectionCallback(this);

        _mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class),
                connectionCallback,
                null);

        // must call before mediaBrowser.connect()
        connectionCallback.setMediaBrowser(_mediaBrowser);

        // Instantiate ViewPager, PagerAdapter and TabLayout
        ViewPager viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.main_tabs);
        _fragmentManager= getSupportFragmentManager();
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(_fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setTabIcons(tabLayout);

        // color tab bar icons
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(this,
                R.color.colorAccent),
                PorterDuff.Mode.SRC_IN);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

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
                    R.color.textSecondary);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        _mediaBrowser.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
}