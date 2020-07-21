package com.muiz6.musicplayer.di.main.home;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.HomeFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {HomeModule.class,
		TabLayoutModule.class})
public interface HomeComponent {
	HomeFragment getHomeFragment();

	@Subcomponent.Factory
	interface Factory {
		HomeComponent create();
	}
}
