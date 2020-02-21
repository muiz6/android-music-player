package com.muiz6.musicplayer;

import com.muiz6.musicplayer.adapters.MainPagerAdapter;

import com.google.android.material.tabs.TabLayout;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate ViewPager, PagerAdapter and TabLayout
        this.viewPager = findViewById(R.id.pager);
        TabLayout tabs = findViewById(R.id.tabs);
        PagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(this.viewPager);
    }

    @Override
    public void onBackPressed() {
        if (this.viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
        }
    }
}