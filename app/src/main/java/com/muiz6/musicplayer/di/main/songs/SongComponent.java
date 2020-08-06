package com.muiz6.musicplayer.di.main.songs;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.explore.songs.SongFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {SongModule.class})
public interface SongComponent {

	SongFragment getSongFragment();

	@Subcomponent.Factory
	interface Factory {

		SongComponent create();
	}
}
