package com.muiz6.musicplayer.di.main.query;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.home.query.QueryViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class QueryModule {

	@Binds
	@IntoMap
	@ClassKey(QueryViewModel.class)
	abstract ViewModel provideQueryViewModel(QueryViewModel viewModel);
}
