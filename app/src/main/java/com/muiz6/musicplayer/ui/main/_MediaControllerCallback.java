package com.muiz6.musicplayer.ui.main;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.muiz6.musicplayer.R;

class _MediaControllerCallback extends MediaControllerCompat.Callback {

    private final MainActivity _activity;

    public _MediaControllerCallback(MainActivity activity) {
        _activity = activity;
    }

    @Override
    public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
        final TextView tv = _activity.findViewById(R.id.main_bottom_appbar_song_title);
        final CharSequence title = metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE);
        if (title != null) {
            tv.setText(title);
        }
        else {
            tv.setText("Unknown Title");
        }
    }

    @Override
    public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
        final View bottomAppbar = _activity.findViewById(R.id.main_bottom_appbar);
        if (state != null && bottomAppbar.getVisibility() != View.VISIBLE) {
            bottomAppbar.setVisibility(View.VISIBLE);
            final View viewPager = _activity.findViewById(R.id.main_view_pager);
            viewPager.setPadding(viewPager.getPaddingStart(), viewPager.getPaddingTop(),
                    viewPager.getPaddingEnd(), bottomAppbar.getHeight());
        }

        final ImageButton btn = _activity.findViewById(R.id.main_bottom_appbar_btn_play);

        // use current theme colors
        final TypedValue typedValue = new TypedValue();
        final Resources.Theme theme = _activity.getTheme();
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
