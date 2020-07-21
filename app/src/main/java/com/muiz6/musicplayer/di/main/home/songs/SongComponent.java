package com.muiz6.musicplayer.di.main.home.songs;

import com.muiz6.musicplayer.di.MediaBrowserModule;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.songs.SongFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {SongModule.class,
		MediaBrowserModule.class})
public interface SongComponent {
	SongFragment getSongFragment();

	@Subcomponent.Factory
	interface Factory {
		SongComponent create();
	}
}
