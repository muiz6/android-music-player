package com.muiz6.musicplayer.di.main.nowplaying;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.nowplaying.PlayerViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class PlayerModule {

	@Binds
	@IntoMap
	@ClassKey(PlayerViewModel.class)
	abstract ViewModel getPlayerViewModel(PlayerViewModel viewModel);
}
