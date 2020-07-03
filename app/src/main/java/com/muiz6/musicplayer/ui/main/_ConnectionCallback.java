package com.muiz6.musicplayer.ui.main;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.ui.MyConnectionCallback;

class _ConnectionCallback extends MyConnectionCallback {

    private static final String _TAG = "MainActivityMBCC";
    private final MainActivity _activity;
    private final MediaControllerCompat.Callback _controllerCallback;

    public _ConnectionCallback(MainActivity activity, MediaControllerCompat.Callback callback) {
        super(activity, callback);
        _activity = activity;
        _controllerCallback = callback;
    }

    @Override
    public void onConnected() {
        super.onConnected();

        Log.d(_TAG,"Connected!");
        _buildTransportControls();
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

                final PlaybackStateCompat pbState = MediaControllerCompat.getMediaController(_activity)
                        .getPlaybackState();
                if (pbState != null) {
                    if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                        transportControls.pause();
                    }
                    else if (pbState.getState() == PlaybackStateCompat.STATE_PAUSED) {
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
        // View bottomAppbar = _activity.findViewById(R.id.main_bottom_appbar);
        // bottomAppbar.setVisibility(View.VISIBLE);

        // set bottom padding equal to height of bottom appbar, leave rest as is
        // View viewPager = _activity.findViewById(R.id.main_view_pager);
        // viewPager.setPadding(viewPager.getPaddingStart(), viewPager.getPaddingTop(),
        //         viewPager.getPaddingEnd(), bottomAppbar.getHeight());
    }
}

