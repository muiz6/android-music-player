package com.muiz6.musicplayer.ui;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.Repository;
import com.muiz6.musicplayer.ui.adapters.MainPagerAdapter;

import com.google.android.material.tabs.TabLayout;

import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;

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

        // bottom appbar configuration
        ImageButton playButton = findViewById(R.id.main_bottom_appbar_play_btn);
        // playButton.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Repository.getInstance(MainActivity.this).getSongList().getValue().get(5).getPath();
                MediaPlayer player = MediaPlayer.create(MainActivity.this, Uri.parse(path));
                player.start();
            }
        });
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