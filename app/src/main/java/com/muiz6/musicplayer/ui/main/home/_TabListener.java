package com.muiz6.musicplayer.ui.main.home;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.ThemeUtil;

import javax.inject.Inject;

class _TabListener implements TabLayout.OnTabSelectedListener {

	private final Context _context;

	@Inject
	public _TabListener(Context context) {
		_context = context;
	}

	@Override
	public void onTabSelected(@NonNull TabLayout.Tab tab) {
		final Drawable icon = tab.getIcon();
		if (icon != null) {
			final int color = ThemeUtil.getColor(_context, R.attr.colorAccent);
			DrawableCompat.setTint(tab.getIcon(), color);
		}
	}

	@Override
	public void onTabReselected(TabLayout.Tab tab) {}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {
		final Drawable icon = tab.getIcon();
		if (icon != null) {
			final int color = ThemeUtil.getColor(_context,
					android.R.attr.textColorSecondary);
			DrawableCompat.setTint(tab.getIcon(), color);
		}
	}
}
