package com.muiz6.musicplayer.di.main.artists;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.home.library.artists.ArtistViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class ArtistModule {

	@Binds
	@IntoMap
	@ClassKey(ArtistViewModel.class)
	abstract ViewModel getArtistViewModel(ArtistViewModel viewModel);
}
