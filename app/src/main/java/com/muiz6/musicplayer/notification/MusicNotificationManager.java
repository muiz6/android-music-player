package com.muiz6.musicplayer.notification;

import android.content.Context;
import android.graphics.Color;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.App;
import com.muiz6.musicplayer.R;

public class MusicNotificationManager extends PlayerNotificationManager {

	public MusicNotificationManager(Context context,
			MediaDescriptionAdapter description,
			NotificationListener listener) {
		super(context, App.NOTIFICATION_CHANNEL_ID, App.NOTIFICATION_ID_PLAYER, description,
				listener);

		setColor(Color.BLACK);
		setColorized(true);
		setUseNavigationActionsInCompactView(true);
		setSmallIcon(R.drawable.ic_play_circle_filled_black_24dp);

		// hide fast forward and rewind buttons
		setFastForwardIncrementMs(0);
		setRewindIncrementMs(0);
	}
}
