package com.muiz6.musicplayer.ui.nowplaying;

import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.muiz6.musicplayer.R;

class _ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

    private static final String _TAG = "NowPlayingMBCC";
    private final NowPlayingActivity _activity;
    // private final MediaBrowserCompat _mediaBrowser;

    public _ConnectionCallback(NowPlayingActivity activity) {
        _activity = activity;
    }

    @Override
    public void onConnected() {
        super.onConnected();

        try {
            final MediaSessionCompat.Token token = _activity.getMediaBrowser().getSessionToken();
            final MediaControllerCompat controller =
                    new MediaControllerCompat(_activity, token);
            MediaControllerCompat.setMediaController(_activity, controller);
        }
        catch (RemoteException e) {
            Log.e(_TAG, "failed to create media controller ;(", e);
            return;
        }

        final ImageButton btnPlay = _activity.findViewById(R.id.now_playing_btn_play);
        final PlaybackStateCompat pbState =
                MediaControllerCompat.getMediaController(_activity).getPlaybackState();
        if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            btnPlay.setImageDrawable(ContextCompat.getDrawable(_activity,
                    R.drawable.ic_pause_black_24dp));
        }
        else {
            btnPlay.setImageDrawable(ContextCompat.getDrawable(_activity,
                    R.drawable.ic_play_arrow_black_24dp));
        }
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();
    }
}
