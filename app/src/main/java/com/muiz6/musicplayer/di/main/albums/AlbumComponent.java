package com.muiz6.musicplayer.di.main.albums;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.albums.AlbumFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {AlbumModule.class})
public interface AlbumComponent {

	AlbumFragment getAlbumFragment();

	@Subcomponent.Factory
	interface Factory {

		AlbumComponent create();
	}
}
