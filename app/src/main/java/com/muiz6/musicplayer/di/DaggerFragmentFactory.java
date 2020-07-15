package com.muiz6.musicplayer.di;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import java.util.Map;

import javax.inject.Provider;

public class DaggerFragmentFactory extends FragmentFactory {

	private final Map<Class<?>, Provider<Fragment>> _providerMap;

	public DaggerFragmentFactory(Map<Class<?>, Provider<Fragment>> providerMap) {
		_providerMap = providerMap;
	}

	@NonNull
	@Override
	public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
		final Class<?> fragmentClass = loadFragmentClass(classLoader, className);
		final Provider<Fragment> provider = _providerMap.get(fragmentClass);
		if (provider != null) {
			return provider.get();
		}
		return super.instantiate(classLoader, className);
	}
}
