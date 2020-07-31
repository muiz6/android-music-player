package com.muiz6.musicplayer.di;

import android.app.Application;

import com.muiz6.musicplayer.di.main.MainComponent;
import com.muiz6.musicplayer.di.musicservice.MusicServiceComponent;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class,
		MediaBrowserModule.class,
		RoomDatabaseModule.class})
public interface AppComponent {

	MainComponent.Factory getMainComponent();

	MusicServiceComponent.Factory getMusicServiceComponent();

	@Component.Factory
	interface Factory {

		AppComponent create(@BindsInstance Application application);
	}
}
