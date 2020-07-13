package com.muiz6.musicplayer.di;

import com.muiz6.musicplayer.di.scope.ActivityScope;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.MainActivity;

import dagger.Subcomponent;

@ActivityScope
@FragmentScope
@Subcomponent(modules = {MainFragmentFactoryModule.class,
		MediaBrowserModule.class,
		TabFragmentFactoryModule.class})
public interface MainActivityComponent {
	void inject(MainActivity activity);
}
