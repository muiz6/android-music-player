package com.muiz6.musicplayer.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.nowplaying.NowPlayingActivity;

public class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

	private final Context _context;

	public DescriptionAdapter(Context context) {
		_context = context;
	}

	@Override
	public CharSequence getCurrentContentTitle(Player player) {
		int index = player.getCurrentWindowIndex();
		return "Exo Title";
	}

	@Nullable
	@Override
	public PendingIntent createCurrentContentIntent(Player player) {
		Intent intent = new Intent(_context, NowPlayingActivity.class);

		// todo: look into request codes
		return PendingIntent.getActivity(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	@Nullable
	@Override
	public CharSequence getCurrentContentText(Player player) {
		return null;
	}

	@Nullable
	@Override
	public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
		final Bitmap bmp = BitmapFactory.decodeResource(_context.getResources(),
				R.drawable.artwork_placeholder);
		return bmp;
	}
}
