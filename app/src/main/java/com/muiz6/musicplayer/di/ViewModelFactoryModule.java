package com.muiz6.musicplayer.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class ViewModelFactoryModule {

	@Provides
	static ViewModelProvider.Factory getViewModelFactory(final Map<Class<?>,
			Provider<ViewModel>> providerMap) {
		return new ViewModelProvider.Factory() {

			@NonNull
			@Override
			public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
				return (T) providerMap.get(modelClass).get();
			}
		};
	}
}
