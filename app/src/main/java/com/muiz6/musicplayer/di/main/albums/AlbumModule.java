package com.muiz6.musicplayer.di.main.albums;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.home.library.albums.AlbumViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class AlbumModule {

	@Binds
	@IntoMap
	@ClassKey(AlbumViewModel.class)
	abstract ViewModel getAlbumViewModel(AlbumViewModel viewModel);
}
