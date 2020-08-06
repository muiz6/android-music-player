package com.muiz6.musicplayer.di.main.artists;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.explore.artists.ArtistFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {ArtistModule.class})
public interface ArtistComponent {

	ArtistFragment getArtistFragment();

	@Subcomponent.Factory
	interface Factory {

		ArtistComponent create();
	}
}
