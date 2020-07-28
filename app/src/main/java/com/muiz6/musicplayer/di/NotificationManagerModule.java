package com.muiz6.musicplayer.di;

import android.content.Context;
import android.graphics.Color;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.MyApp;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.notification.DescriptionAdapter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationManagerModule {

	@Provides
	PlayerNotificationManager provideNotificationManager(@Named("Application") Context context,
			DescriptionAdapter adapter) {
		final PlayerNotificationManager manager = new PlayerNotificationManager(context,
				MyApp.NOTIFICATION_CHANNEL_ID,
				MyApp.NOTIFICATION_ID_PLAYER,
				adapter);
		manager.setColor(Color.BLACK);
		manager.setColorized(true);
		manager.setUseNavigationActionsInCompactView(true);
		manager.setSmallIcon(R.drawable.ic_play_circle_filled);

		// hide fast forward and rewind buttons
		manager.setFastForwardIncrementMs(0);
		manager.setRewindIncrementMs(0);
		return manager;
	}
}
