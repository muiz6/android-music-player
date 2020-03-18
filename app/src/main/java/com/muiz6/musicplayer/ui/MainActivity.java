package com.muiz6.musicplayer.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.Repository;
import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.services.AudioService;
import com.muiz6.musicplayer.ui.adapters.MainPagerAdapter;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private AudioService mService;
    private boolean mBound;
    private final ServiceConnection mServiceConnection;

    public MainActivity() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AudioService.MyBinder binder = (AudioService.MyBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // mBinder.postValue(null);
                mBound = false;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate ViewPager, PagerAdapter and TabLayout
        this.viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
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

        FloatingActionButton fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound) {
                    ArrayList<SongDataModel> data = Repository.getInstance(MainActivity.this).getSongList().getValue();

                    // generate a random number
                    int rand = ThreadLocalRandom.current().nextInt(0, data.size());
                    String path = data.get(rand).getPath();
                    mService.playAudio(Uri.parse(path));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // start the service and bind to it
        Intent intent = new Intent(this, AudioService.class);
        // startService(intent);
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