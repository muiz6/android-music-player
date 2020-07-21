package com.muiz6.musicplayer.di.main.home.songs;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.home.songs.SongViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class SongModule {

	@Binds
	@IntoMap
	@ClassKey(SongViewModel.class)
	public abstract ViewModel getSongViewModel(SongViewModel viewModel);
}
