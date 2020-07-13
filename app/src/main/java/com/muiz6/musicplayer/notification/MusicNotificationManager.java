package com.muiz6.musicplayer.notification;

import android.content.Context;
import android.graphics.Color;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.MyApp;
import com.muiz6.musicplayer.R;

public class MusicNotificationManager extends PlayerNotificationManager {

	public MusicNotificationManager(Context context,
			MediaDescriptionAdapter description,
			NotificationListener listener) {
		super(context, MyApp.NOTIFICATION_CHANNEL_ID, MyApp.NOTIFICATION_ID_PLAYER, description,
				listener);

		setColor(Color.BLACK);
		setColorized(true);
		setUseNavigationActionsInCompactView(true);
		setSmallIcon(R.drawable.ic_play_circle_filled);

		// hide fast forward and rewind buttons
		setFastForwardIncrementMs(0);
		setRewindIncrementMs(0);
	}
}
