package com.muiz6.musicplayer.ui.main;

import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.muiz6.musicplayer.R;

class _ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

    private static final String _TAG = "MainActivityMBCC";
    private final MainActivity _activity;

    public _ConnectionCallback(MainActivity activity) {

        // do not call activity.getMediaBrowser() or activity.getMediaControllerCallback()
        // here as they are null when ConnectionCallback is created
        _activity = activity;
    }

    @Override
    public void onConnected() {
        Log.d(_TAG,"Connected!");

        // Get the token for the MediaSession
        MediaSessionCompat.Token token = _activity.getMediaBrowser().getSessionToken();

        // Create a MediaController
        try {
            MediaControllerCompat mediaController =
                new MediaControllerCompat(_activity, token);

            // Save the controller for using anywhere
            MediaControllerCompat.setMediaController(_activity, mediaController);
        }
        catch (RemoteException e) {
            Log.e(_TAG, "Error creating controller", e);
            return;
        }

        _buildTransportControls();

        // Register a Callback to stay in sync
        MediaControllerCompat.getMediaController(_activity)
                .registerCallback(_activity.getMediaControllerCallback());
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();

        Log.d(_TAG, "onConnectionSuspended: suspended ;(");
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();

        Log.d(_TAG, "connection failed!");
    }

    private void _buildTransportControls() {
        final MediaControllerCompat.TransportControls transportControls = MediaControllerCompat
                .getMediaController(_activity).getTransportControls();

        // Grab the view for the play/pause button
        final ImageButton playPauseBtn = _activity.findViewById(R.id.main_bottom_appbar_btn_play);

        // Attach a listener to the button
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                Log.d(_TAG, "play/pause button pressed");

                int pbState = MediaControllerCompat.getMediaController(_activity)
                    .getPlaybackState()
                    .getState();

                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    transportControls.pause();
                }
                else if (pbState == PlaybackStateCompat.STATE_PAUSED) {
                    transportControls.play();
                }
                // else {
                //     // ArrayList<SongDataModel> data = Repository.getInstance(_activity)
                //     //     .getSongList().getValue();
                //     //
                //     // int rand = ThreadLocalRandom.current().nextInt(0, data.size());
                //     // Uri path = Uri.parse(data.get(rand).getPath());
                //     //
                //     // MediaControllerCompat.getMediaController(_activity)
                //     //     .getTransportControls()
                //     //     .playFromUri(path, null);
                // }
            }
        });

        final ImageButton btnSkipPrev = _activity
                .findViewById(R.id.main_bottom_appbar_btn_previous);
        btnSkipPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                transportControls.skipToPrevious();
            }
        });

        final ImageButton btnSkipNext = _activity.findViewById(R.id.main_bottom_appbar_btn_next);
        btnSkipNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                transportControls.skipToNext();
            }
        });

        final MediaControllerCompat mediaController =
                MediaControllerCompat.getMediaController(_activity);

        // TODO: set bottom appbar visible only when current song is available
        View bottomAppbar = _activity.findViewById(R.id.main_bottom_appbar);
        bottomAppbar.setVisibility(View.VISIBLE);

        // set bottom padding equal to height of bottom appbar, leave rest as is
        View viewPager = _activity.findViewById(R.id.main_view_pager);
        viewPager.setPadding(viewPager.getPaddingStart(), viewPager.getPaddingTop(),
                viewPager.getPaddingEnd(), bottomAppbar.getHeight());
    }
}

