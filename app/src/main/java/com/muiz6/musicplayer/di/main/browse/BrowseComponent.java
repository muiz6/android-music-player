package com.muiz6.musicplayer.di.main.browse;

import com.muiz6.musicplayer.ui.main.home.browse.BrowseFragment;

import dagger.Subcomponent;

@Subcomponent(modules = {BrowseModule.class})
public interface BrowseComponent {

	BrowseFragment getBrowseFragment();

	@Subcomponent.Factory
	interface Factory {
		BrowseComponent create();
	}
}
