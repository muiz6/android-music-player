package com.muiz6.musicplayer.di.main.genres;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.genres.GenreFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {GenreModule.class})
public interface GenreComponent {

	GenreFragment getGenreFragment();

	@Subcomponent.Factory
	interface Factory {
		GenreComponent create();
	}
}
