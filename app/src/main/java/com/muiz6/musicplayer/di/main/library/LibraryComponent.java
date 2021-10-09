package com.muiz6.musicplayer.di.main.library;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.library.LibraryFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {LibraryModule.class})
public interface LibraryComponent {

	LibraryFragment getExploreFragment();

	@Subcomponent.Factory
	interface Factory {

		LibraryComponent create();
	}
}
