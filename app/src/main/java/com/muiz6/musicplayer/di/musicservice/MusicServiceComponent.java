package com.muiz6.musicplayer.di.musicservice;

import com.muiz6.musicplayer.di.scope.ServiceScope;
import com.muiz6.musicplayer.musicservice.MusicService;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent(modules = {MusicServiceModule.class})
public interface MusicServiceComponent {

	void inject(MusicService service);

	@Subcomponent.Factory
	interface Factory {
		MusicServiceComponent create();
	}
}
