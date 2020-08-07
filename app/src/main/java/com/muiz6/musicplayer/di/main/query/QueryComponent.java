package com.muiz6.musicplayer.di.main.query;

import com.muiz6.musicplayer.ui.main.home.query.QueryFragment;

import dagger.Subcomponent;

@Subcomponent(modules = {QueryModule.class})
public interface QueryComponent {

	QueryFragment getQueryFragment();

	@Subcomponent.Factory
	interface Factory {
		QueryComponent create();
	}
}
