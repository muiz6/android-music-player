package com.muiz6.musicplayer.di;

import android.content.Context;

import dagger.BindsInstance;
import dagger.Component;

@Component
public interface AppComponent {
	MainActivityComponent getMainActivityComponent();

	@Component.Factory
	interface Factory {
		AppComponent create(@BindsInstance Context context);
	}
}
