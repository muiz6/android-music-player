package com.muiz6.musicplayer.ui.main.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.ThemeUtil;

import javax.inject.Inject;

public class TabMediatorStrategy implements TabLayoutMediator.TabConfigurationStrategy {

	private static final int[] _ICONS = {
			R.drawable.ic_library_music,
			R.drawable.ic_album,
			R.drawable.ic_music_artist,
			R.drawable.ic_local_offer
	};
	private Context _context;

	@Inject
	public TabMediatorStrategy(Context context) {
		_context = context;
	}

	@Override
	public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
		tab.setIcon(_ICONS[position]);

		// first tab is selected by default
		// it should have different color then
		if (position == 0) {
			final int color = ThemeUtil.getColor(_context, R.attr.colorAccent);
			DrawableCompat.setTint(tab.getIcon(), color);
		}
	}
}
