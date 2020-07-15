package com.muiz6.musicplayer.di.main;

import com.muiz6.musicplayer.di.MediaBrowserModule;
import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.nowplaying.PlayerFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {MediaBrowserModule.class})
public interface NowPlayingComponent {
	PlayerFragment getPlayerFragment();

	@Subcomponent.Factory
	interface Factory {
		NowPlayingComponent create();
	}
}
