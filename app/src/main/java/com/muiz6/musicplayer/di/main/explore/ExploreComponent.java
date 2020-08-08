package com.muiz6.musicplayer.di.main.explore;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.library.LibraryFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {ExploreModule.class})
public interface ExploreComponent {

	LibraryFragment getExploreFragment();

	@Subcomponent.Factory
	interface Factory {

		ExploreComponent create();
	}
}
