package com.muiz6.musicplayer.di;

import com.muiz6.musicplayer.di.main.MainComponent;
import com.muiz6.musicplayer.di.scope.ApplicationScope;

import dagger.Component;

@ApplicationScope
@Component(modules = AppSubcomponentModule.class)
public interface AppComponent {
	MainComponent.Factory getMainComponent();

	@Component.Factory
	interface Factory {
		AppComponent create();
	}
}
