package com.muiz6.musicplayer.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.Repository;
import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.services.AudioService;
import com.muiz6.musicplayer.ui.adapters.MainPagerAdapter;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.MyBinder binder = (AudioService.MyBinder) service;
            mService = binder.getService();
            mBound = true;

            // bind progressbar to service
            final ProgressBar progressBar = findViewById(R.id.main_bottom_appbar_progressbar);
            mService.getProgressPercentage().observe(MainActivity.this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if (integer != null) {
                        progressBar.setProgress(integer);
                    }
                    else {
                        progressBar.setProgress(0);
                    }
                }
            });

            // bind play button to service
            final ImageButton playButton = findViewById(R.id.main_bottom_appbar_play_btn);
            mService.getPlayState().observe(MainActivity.this, new Observer<Boolean>(){
                @Override
                public void onChanged(Boolean bool) {
                    if (bool != null) {
                        if (bool) {
                            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                            int color = ContextCompat.getColor(MainActivity.this, R.color.colorAccent);
                            playButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        }
                        else {
                            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                            int color = ContextCompat.getColor(MainActivity.this, R.color.textPrimary);
                            playButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                        }
                    }
                }
            });

            // bind current song text to service
            final TextView view = findViewById(R.id.main_bottom_appbar_song_title);
            view.setSelected(true);
            mService.getCurrentSong().observe(MainActivity.this, new Observer<SongDataModel>() {
                @Override
                public void onChanged(SongDataModel data) {
                    if (data != null) {

                        view.setText(data.getTitle() + " | " + data.getArtist());
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // mBinder.postValue(null);
            mBound = false;
        }
    }

    private ViewPager viewPager;
    private AudioService mService;
    private boolean mBound;
    private final ServiceConnection mServiceConnection;

    public MainActivity() {
        mServiceConnection = new MyServiceConnection();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate ViewPager, PagerAdapter and TabLayout
        this.viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.main_tabs);
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(this.viewPager);
        pagerAdapter.setTabIcons(tabLayout);

        // color tab bar icons
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        tabLayout.setOnTabSelectedListener(
            new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                    int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.colorAccent);
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    super.onTabUnselected(tab);
                    int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.textSecondary);
                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    super.onTabReselected(tab);
                }
            }
        );

        // FloatingActionButton fab = findViewById(R.id.main_fab);

        ImageButton playBtn = findViewById(R.id.main_bottom_appbar_play_btn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mBound) {
                ArrayList<SongDataModel> data = Repository.getInstance(MainActivity.this).getSongList().getValue();

                // generate a random number
                int rand = ThreadLocalRandom.current().nextInt(0, data.size());
                mService.playAudio(data.get(rand));
            }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // start the service and bind to it
        Intent intent = new Intent(this, AudioService.class);
        ContextCompat.startForegroundService(this, intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(mServiceConnection);
    }

    // @Override
    // public void onBackPressed() {
    //     if (this.viewPager.getCurrentItem() == 0) {
    //         // If the user is currently looking at the first step, allow the system to handle the
    //         // Back button. This calls finish() on this activity and pops the back stack.
    //         super.onBackPressed();
    //     } else {
    //         // Otherwise, select the previous step.
    //         this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
    //     }
    // }
}