package com.muiz6.musicplayer.di.main.queue;

import androidx.lifecycle.ViewModel;

import com.muiz6.musicplayer.ui.main.home.queue.QueueViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class QueueModule {

	@Binds
	@IntoMap
	@ClassKey(QueueViewModel.class)
	abstract ViewModel bindViewModel(QueueViewModel viewModel);
}
