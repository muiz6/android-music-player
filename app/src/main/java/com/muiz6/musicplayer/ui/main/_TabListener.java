package com.muiz6.musicplayer.ui.main;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.ThemeUtil;

class _TabListener extends TabLayout.ViewPagerOnTabSelectedListener {

	private final ViewPager _viewPager;

	public _TabListener(ViewPager viewPager) {
		super(viewPager);

		_viewPager = viewPager;
	}

	@Override
	public void onTabSelected(@NonNull TabLayout.Tab tab) {
		super.onTabSelected(tab);

		final Drawable icon = tab.getIcon();
		if (icon != null) {
			final int color = ThemeUtil.getColor(_viewPager.getContext(), R.attr.colorAccent);
			DrawableCompat.setTint(tab.getIcon(), color);
		}
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {
		super.onTabUnselected(tab);

		final Drawable icon = tab.getIcon();
		if (icon != null) {
			final int color = ThemeUtil.getColor(_viewPager.getContext(),
					android.R.attr.textColorSecondary);
			DrawableCompat.setTint(tab.getIcon(), color);
		}
	}
}
