package com.muiz6.musicplayer.di.main.browse;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.home.browse.BrowseViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class BrowseModule {

	@Binds
	@IntoMap
	@ClassKey(BrowseViewModel.class)
	abstract ViewModel bindViewModel(BrowseViewModel viewModel);
}
