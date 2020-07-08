package com.muiz6.musicplayer.ui.main;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.ThemeUtil;
import com.muiz6.musicplayer.ui.main.songs.SongFragment;

class _MainPagerAdapter extends FragmentPagerAdapter {

    private final SongFragment _songFragment;

    public _MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        _songFragment = SongFragment.newInstance();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return _songFragment;
        }
        else {
            return new PlaceholderFragment();
        }
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

    /**
    * must be called after TabLayout.setupWithViewPager()
    */
    public void setTabIcons(TabLayout tabLayout) {

        // resource ids of desired icons
        int[] icons = {
            R.drawable.ic_library_music,
            R.drawable.ic_album,
            R.drawable.ic_face,
            R.drawable.ic_folder,
            R.drawable.ic_queue_music,
            R.drawable.ic_local_offer
        };

        // get textColorSecondary from theme
        @ColorInt final int color = ThemeUtil.getColor(tabLayout.getContext(),
                android.R.attr.textColorSecondary);
        for (int i = 0; i < getCount(); i++) {
            final TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab !=  null) {
                tab.setIcon(icons[i]);

                // setting color here because tabIconTint attribute not working
                DrawableCompat.setTint(tab.getIcon(), color);
            }
        }
    }
}
