package com.muiz6.musicplayer.di.main.home;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.muiz6.musicplayer.ui.main.home.TabListener;
import com.muiz6.musicplayer.ui.main.home.TabMediatorStrategy;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TabLayoutModule {

	@Binds
	abstract TabLayout.OnTabSelectedListener provideTabListener(TabListener listener);

	@Binds
	abstract TabLayoutMediator.TabConfigurationStrategy provideTabMediatorStrategy(
			TabMediatorStrategy strategy);
}
