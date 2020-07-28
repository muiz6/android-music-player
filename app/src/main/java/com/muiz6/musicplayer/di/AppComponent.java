package com.muiz6.musicplayer.di;

import android.content.Context;

import com.muiz6.musicplayer.di.main.MainComponent;
import com.muiz6.musicplayer.musicservice.MusicService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class,
		MediaBrowserModule.class,
		RoomDatabaseModule.class,
		NotificationManagerModule.class})
public interface AppComponent {

	void inject(MusicService service);

	MainComponent.Factory getMainComponent();

	@Component.Factory
	interface Factory {

		AppComponent create(@BindsInstance @Named("Application") Context context);
	}
}
