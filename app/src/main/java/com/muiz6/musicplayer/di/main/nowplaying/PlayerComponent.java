package com.muiz6.musicplayer.di.main.nowplaying;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.nowplaying.PlayerFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(modules = {PlayerModule.class})
public interface PlayerComponent {

	PlayerFragment getPlayerFragment();

	@Subcomponent.Factory
	interface Factory {

		PlayerComponent create();
	}
}
