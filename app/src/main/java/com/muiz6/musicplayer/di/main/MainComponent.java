package com.muiz6.musicplayer.di.main;

import android.content.Context;

import com.muiz6.musicplayer.di.scope.ActivityScope;
import com.muiz6.musicplayer.ui.main.MainActivity;

import dagger.BindsInstance;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = MainFragmentFactoryModule.class)
public interface MainComponent {
	void inject(MainActivity activity);

	// todo: may need to return subcomponent factories here

	@Subcomponent.Factory
	interface Factory {
		MainComponent create(@BindsInstance Context context);
	}
}
