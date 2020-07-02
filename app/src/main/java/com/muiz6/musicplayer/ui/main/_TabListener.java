package com.muiz6.musicplayer.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.muiz6.musicplayer.R;

class _TabListener extends TabLayout.ViewPagerOnTabSelectedListener {

	private final ViewPager _viewPager;

	public _TabListener(ViewPager viewPager) {
		super(viewPager);

		_viewPager = viewPager;
	}

	@Override
	public void onTabSelected(@NonNull TabLayout.Tab tab) {
		super.onTabSelected(tab);

		_applyTabColor(tab, R.color.colorAccent);
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {
		super.onTabUnselected(tab);

		_applyTabColor(tab, R.color.colorGreyLight);
	}

	private void _applyTabColor(TabLayout.Tab tab, @ColorRes int id) {
		int tabIconColor = ContextCompat.getColor(_viewPager.getContext(), id);
		final Drawable icon = tab.getIcon();
		if (icon != null) {
			icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
		}
	}
}
