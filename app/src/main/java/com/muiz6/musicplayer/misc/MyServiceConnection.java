package com.muiz6.musicplayer.misc;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MyServiceConnection implements ServiceConnection {
    private boolean mBound = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MyBinder binder = (MyBinder) service;
        // mService = binder.getService();
        mBound = true;

        // bind progressbar to service
        // final ProgressBar progressBar = findViewById(R.id.main_bottom_appbar_progressbar);
        // mService.getProgressPercentage().observe(MainActivity.this,
        //     new Observer<Integer>() {
        //         @Override
        //         public void onChanged(Integer integer) {
        //             if (integer != null) {
        //                 progressBar.setProgress(integer);
        //             }
        //             else {
        //                 progressBar.setProgress(0);
        //             }
        //         }
        //     });

        // bind play button to service
        // final ImageButton playButton = findViewById(R.id.main_bottom_appbar_btn_play);
        // mService.getPlayState().observe(MainActivity.this, new Observer<Boolean>(){
        //     @Override
        //     public void onChanged(Boolean bool) {
        //         if (bool != null) {
        //             if (bool) {
        //                 playButton.setImageDrawable(getResources()
        //                     .getDrawable(R.drawable.ic_pause_black_24dp));
        //
        //                 int color = ContextCompat.getColor(MainActivity.this,
        //                     R.color.colorAccent);
        //
        //                 playButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        //             }
        //             else {
        //                 playButton.setImageDrawable(getResources()
        //                     .getDrawable(R.drawable.ic_play_arrow_black_24dp));
        //
        //                 int color = ContextCompat.getColor(MainActivity.this,
        //                     R.color.textPrimary);
        //
        //                 playButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        //             }
        //         }
        //     }
        // });

        // bind current song text to service
        // final TextView view = findViewById(R.id.main_bottom_appbar_song_title);
        // view.setSelected(true);
        // mService.getCurrentSong().observe(MainActivity.this, new Observer<SongDataModel>() {
        //     @Override
        //     public void onChanged(SongDataModel data) {
        //         if (data != null) {
        //
        //             view.setText(data.getTitle() + " | " + data.getArtist());
        //         }
        //     }
        // });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // mBinder.postValue(null);
        mBound = false;
    }
}
