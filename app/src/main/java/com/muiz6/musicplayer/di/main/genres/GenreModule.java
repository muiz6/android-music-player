package com.muiz6.musicplayer.di.main.genres;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.home.explore.genres.GenreViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class GenreModule {

	@Binds
	@IntoMap
	@ClassKey(GenreViewModel.class)
	abstract ViewModel provideGenreViewModel(GenreViewModel viewModel);
}
