package com.muiz6.musicplayer.ui.main.home;

import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.muiz6.musicplayer.ui.main.PlaceholderFragment;
import com.muiz6.musicplayer.ui.main.home.songs.SongFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

	private final MediaBrowserCompat _mediaBrowser;

	public HomePagerAdapter(@NonNull Fragment fragment, MediaBrowserCompat mediaBrowser) {
		super(fragment);

		_mediaBrowser = mediaBrowser;
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		if (position == 0) {
			return new SongFragment(_mediaBrowser);
		}
		else {
			return new PlaceholderFragment();
		}
	}

	@Override
	public int getItemCount() {
		return 5;
	}
}

// class HomePagerAdapter extends FragmentPagerAdapter {
//
// 	private final MediaBrowserCompat _mediaBrowser;
//
// 	public HomePagerAdapter(@NonNull FragmentManager fm, MediaBrowserCompat mediaBrowser) {
// 		super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//
// 		_mediaBrowser = mediaBrowser;
// 	}
//
// 	@NonNull
// 	@Override
// 	public Fragment getItem(int position) {
// 		if (position == 0) {
// 			return new SongFragment(_mediaBrowser);
// 		}
// 		else {
// 			return new PlaceholderFragment();
// 		}
// 	}
//
// 	@Override
// 	public int getCount() {
// 		return 5;
// 	}
//
// 	/**
// 	 * must be called after TabLayout.setupWithViewPager()
// 	 */
// 	public void setTabIcons(TabLayout tabLayout) {
//
// 		// resource ids of desired icons
// 		final int[] icons = {
// 				R.drawable.ic_library_music,
// 				R.drawable.ic_album,
// 				R.drawable.ic_face,
// 				R.drawable.ic_queue_music,
// 				R.drawable.ic_local_offer
// 		};
//
// 		// set different tab color from default drawable color
// 		@ColorInt final int color = ThemeUtil.getColor(tabLayout.getContext(),
// 				android.R.attr.textColorSecondary);
// 		for (int i = 0; i < getCount(); i++) {
// 			final TabLayout.Tab tab = tabLayout.getTabAt(i);
// 			if (tab != null) {
// 				tab.setIcon(icons[i]);
//
// 				// setting color here because tabIconTint attribute not working
// 				DrawableCompat.setTint(tab.getIcon(), color);
// 			}
// 		}
// 	}
// }
