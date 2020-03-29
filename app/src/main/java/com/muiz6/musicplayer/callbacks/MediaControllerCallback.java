package com.muiz6.musicplayer.callbacks;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.muiz6.musicplayer.R;

public class MediaControllerCallback extends MediaControllerCompat.Callback {

    private final Activity _activity;

    public MediaControllerCallback(Activity activity) {
        _activity = activity;
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {}

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        ImageButton btn = _activity.findViewById(R.id.main_bottom_appbar_btn_play);

        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            int color = ContextCompat.getColor(_activity, R.color.colorAccent);
            btn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            btn.setImageDrawable(_activity.getDrawable(R.drawable.ic_pause_black_24dp));
        }
        else {
            int color = ContextCompat.getColor(_activity, R.color.textPrimary);
            btn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            btn.setImageDrawable(_activity.getDrawable(R.drawable.ic_play_arrow_black_24dp));
        }
    }
}
