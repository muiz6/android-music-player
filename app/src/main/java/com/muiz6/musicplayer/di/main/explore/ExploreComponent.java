package com.muiz6.musicplayer.di.main.explore;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.explore.ExploreFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {ExploreModule.class})
public interface ExploreComponent {

	ExploreFragment getExploreFragment();

	@Subcomponent.Factory
	interface Factory {

		ExploreComponent create();
	}
}
