package com.muiz6.musicplayer.ui.main;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.TypedValue;
import android.widget.ImageButton;

import androidx.annotation.ColorInt;

import com.muiz6.musicplayer.R;

public class MediaControllerCallback extends MediaControllerCompat.Callback {

    private final MainActivity _activity;

    public MediaControllerCallback(MainActivity activity) {
        _activity = activity;
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {
        // TextView tv = _activity.findViewById(R.id.main_bottom_appbar_song_title);
        // tv.setText(metadata.getText(""));
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        ImageButton btn = _activity.findViewById(R.id.main_bottom_appbar_btn_play);

        // use current theme colors
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = _activity.getTheme();
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
            @ColorInt int color = typedValue.data;
            btn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            btn.setImageDrawable(_activity.getDrawable(R.drawable.ic_pause_black_24dp));
        }
        else {
            theme.resolveAttribute(R.attr.iconTint, typedValue, true);
            @ColorInt int color = typedValue.data;
            btn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            btn.setImageDrawable(_activity.getDrawable(R.drawable.ic_play_arrow_black_24dp));
        }
    }
}
