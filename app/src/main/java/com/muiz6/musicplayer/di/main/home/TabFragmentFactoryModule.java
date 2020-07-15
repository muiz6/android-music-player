package com.muiz6.musicplayer.di.main.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.muiz6.musicplayer.di.scope.FragmentScope;
import com.muiz6.musicplayer.ui.main.home.songs.SongFragment;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class TabFragmentFactoryModule {

	@Provides
	@FragmentScope
	@Named("HomeFragment")
	static FragmentFactory provideTabFragmentFactory(final Provider<SongFragment> provider) {
		return new FragmentFactory() {

			@NonNull
			@Override
			public Fragment instantiate(@NonNull ClassLoader classLoader,
					@NonNull String className) {
				if (className.equals(SongFragment.class.getName())) {
					return provider.get();
				}
				return super.instantiate(classLoader, className);
			}
		};
	}
}
