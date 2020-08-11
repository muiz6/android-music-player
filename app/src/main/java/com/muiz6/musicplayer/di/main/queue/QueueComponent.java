package com.muiz6.musicplayer.di.main.queue;

import com.muiz6.musicplayer.ui.main.home.queue.QueueFragment;

import dagger.Subcomponent;

@Subcomponent(modules = {QueueModule.class})
public interface QueueComponent {

	QueueFragment getQueueFragment();

	@Subcomponent.Factory
	interface Factory {

		QueueComponent create();
	}
}
