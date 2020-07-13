package com.muiz6.musicplayer.di;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.muiz6.musicplayer.di.scope.ActivityScope;
import com.muiz6.musicplayer.ui.main.home.MainFragment;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class MainFragmentFactoryModule {

	@Provides
	@ActivityScope
	@Named("MainActivity")
	static FragmentFactory provideFactory(final Provider<MainFragment> provider) {
		return new FragmentFactory() {
			@NonNull
			@Override
			public Fragment instantiate(@NonNull ClassLoader classLoader,
					@NonNull String className) {
				if (className.equals(MainFragment.class.getName())) {
					return provider.get();
				}
				return super.instantiate(classLoader, className);
			}
		};
	}
}
