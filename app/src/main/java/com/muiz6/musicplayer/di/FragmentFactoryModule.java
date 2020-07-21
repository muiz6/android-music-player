package com.muiz6.musicplayer.di;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import java.util.Map;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentFactoryModule {

	@Provides
	FragmentFactory getFragmentFactory(final Map<Class<?>, Provider<Fragment>> providerMap) {
		return new FragmentFactory() {

			@NonNull
			@Override
			public Fragment instantiate(@NonNull ClassLoader classLoader,
					@NonNull String className) {
				final Class<?> fragmentClass = loadFragmentClass(classLoader, className);
				final Provider<Fragment> provider = providerMap.get(fragmentClass);
				if (provider != null) {
					return provider.get();
				}
				return super.instantiate(classLoader, className);
			}
		};
	}
}
