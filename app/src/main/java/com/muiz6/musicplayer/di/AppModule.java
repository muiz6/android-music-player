package com.muiz6.musicplayer.di;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.muiz6.musicplayer.di.main.MainComponent;
import com.muiz6.musicplayer.di.musicservice.MusicServiceComponent;
import com.muiz6.musicplayer.musicservice.MusicService;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {MainComponent.class,
		MusicServiceComponent.class})
public abstract class AppModule {

	@Provides
	@Named("Application")
	static Context provideContext(Application application) {
		return application.getApplicationContext();
	}

	@Provides
	@Named("MusicServiceIntent")
	static Intent provideMusicServiceIntent(@Named("Application") Context context) {
		return new Intent(context, MusicService.class);
	}
}
