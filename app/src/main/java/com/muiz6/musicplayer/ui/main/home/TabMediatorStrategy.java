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

		// set different tab color from default drawable color
		// setting here because tabIconTint attribute not working
		int color;
		if (position == 0) {
			color = ThemeUtil.getColor(_context, R.attr.colorAccent);
		}
		else {
			color = ThemeUtil.getColor(_context, android.R.attr.textColorSecondary);
		}
		DrawableCompat.setTint(tab.getIcon(), color);
	}
}
