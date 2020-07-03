package com.muiz6.musicplayer.ui.nowplaying;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.MyConnectionCallback;

class _ConnectionCallback extends MyConnectionCallback {

	private static final String _TAG = "NowPlayingMBCC";
	private final NowPlayingActivity _activity;

	public _ConnectionCallback(NowPlayingActivity activity,
			MediaControllerCompat.Callback callback) {
		super(activity, callback);

		_activity = activity;
	}

	@Override
	public void onConnected() {
		super.onConnected();

		final MediaControllerCompat controller =
				MediaControllerCompat.getMediaController(_activity);
		final ImageButton btnPlay = _activity.findViewById(R.id.now_playing_btn_play);
		final PlaybackStateCompat pbState = controller.getPlaybackState();
		if (pbState != null) {
			if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
				btnPlay.setImageDrawable(ContextCompat.getDrawable(_activity,
						R.drawable.ic_pause_black_24dp));
			}
			else {
				btnPlay.setImageDrawable(ContextCompat.getDrawable(_activity,
						R.drawable.ic_play_arrow_black_24dp));
			}
		}

		final MediaControllerCompat.TransportControls transportControls =
				controller.getTransportControls();
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final PlaybackStateCompat pbState = controller.getPlaybackState();
				if (pbState != null) {
					if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
						transportControls.pause();
					}
					else {
						transportControls.play();
					}
				}
			}
		});

		final View btnRepeat = _activity.findViewById(R.id.now_playing_btn_repeat);
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
			}
		});

		final ImageButton btnShuffle = _activity.findViewById(R.id.now_playing_btn_shuffle);
		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final int mode = controller.getShuffleMode();
				if (mode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
					transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
					_setColor(btnShuffle.getDrawable(), R.color.colorWhite);
				}
				else {
					transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
					_setColor(btnShuffle.getDrawable(), R.color.colorAccent);
				}
			}
		});
	}

	@Override
	public void onConnectionFailed() {
		super.onConnectionFailed();
	}

	@Override
	public void onConnectionSuspended() {
		super.onConnectionSuspended();
	}

	private void _setColor(Drawable icon, @ColorRes int id) {
		int tabIconColor = ContextCompat.getColor(_activity, id);
		if (icon != null) {
			icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
		}
	}
}
