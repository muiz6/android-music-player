package com.muiz6.musicplayer.di;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class GestureDetectorModule {

	@Provides
	static GestureDetector provideGestureDetector(Context context) {
		return new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}
		});
	}
}
