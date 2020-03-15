package com.muiz6.musicplayer.ui.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.ui.fragments.MainFragment;
import com.muiz6.musicplayer.ui.fragments.SongFragment;

import java.util.ArrayList;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<SongDataModel> items;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SongFragment();
        }
        return new MainFragment();
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    /// must be called after setupWithViewPager()
    public void setTabIcons(TabLayout tabLayout) {

        // resource ids of desired icons
        int icons[] = {
            R.drawable.ic_library_music_black_24dp,
            R.drawable.ic_album_black_24dp,
            R.drawable.ic_face_black_24dp,
            R.drawable.ic_folder_black_24dp,
            R.drawable.ic_queue_music_black_24dp,
            R.drawable.ic_equalizer_black_24dp
        };

        for (int i =0; i < getCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);;
        }
    }
}
