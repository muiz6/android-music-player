package com.muiz6.musicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.muiz6.musicplayer.models.SongData;
import com.muiz6.musicplayer.ui.main.MainFragment;
import com.muiz6.musicplayer.ui.main.SongsFragment;

import java.util.ArrayList;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<SongData> items;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SongsFragment();
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
        switch (position) {
            case 0:
                return "Songs";
            case 1:
                return "Albums";
            case 2:
                return "Artists";
            case 3:
                return "Folders";
            case 4:
                return "Playlists";
            case 5:
                return "Genres";
        }

        // default case
        return "Tab " + position;
    }
}
