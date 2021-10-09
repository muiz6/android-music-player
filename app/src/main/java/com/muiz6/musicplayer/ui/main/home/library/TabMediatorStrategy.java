package com.muiz6.musicplayer.ui.main.home.library;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.R;

import javax.inject.Inject;

public class TabMediatorStrategy implements TabLayoutMediator.TabConfigurationStrategy {

	private static final int[] _ICONS = {
			R.drawable.ic_library_music,
			R.drawable.ic_album,
			R.drawable.ic_music_artist,
			R.drawable.ic_local_offer
	};

	@Inject
	public TabMediatorStrategy() {}

	@Override
	public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
		tab.setIcon(_ICONS[position]);
	}
}
