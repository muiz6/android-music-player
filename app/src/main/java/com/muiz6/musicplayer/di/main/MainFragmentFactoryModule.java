package com.muiz6.musicplayer.di.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import com.muiz6.musicplayer.di.DaggerFragmentFactory;
import com.muiz6.musicplayer.di.main.home.HomeComponent;
import com.muiz6.musicplayer.ui.main.home.HomeFragment;
import com.muiz6.musicplayer.ui.main.nowplaying.PlayerFragment;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {HomeComponent.class, NowPlayingComponent.class})
public abstract class MainFragmentFactoryModule {

	@Provides
	@IntoMap
	@ClassKey(HomeFragment.class)
	static Fragment provideHomeFragment(HomeComponent.Factory factory) {
		return factory.create().getHomeFragment();
	}

	@Provides
	@IntoMap
	@ClassKey(PlayerFragment.class)
	static Fragment provideFragment(NowPlayingComponent.Factory factory) {
		return factory.create().getPlayerFragment();
	}

	@Provides
	@Named("MainActivity")
	static FragmentFactory getFragmentFactory(final Map<Class<?>, Provider<Fragment>> providerMap) {
		return new DaggerFragmentFactory(providerMap);
	}
}
