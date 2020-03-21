package com.muiz6.musicplayer.ui.fragments;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.ui.adapters.SongListAdapter;
import com.muiz6.musicplayer.viewmodels.SongDataViewModel;

import java.util.ArrayList;

public class SongFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SongListAdapter mSongListAdapter;
    // private CoordinatorLayout mLayout;

    public SongFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // initializing adapter with empty songlist
        mSongListAdapter = new SongListAdapter();

        // attaching fragment to view model
        final SongDataViewModel mSongDataViewModel = new ViewModelProvider(this).get(SongDataViewModel.class);
        mSongDataViewModel.getSongList().observe(this, new Observer<ArrayList<SongDataModel>>() {
            @Override
            public void onChanged(ArrayList<SongDataModel> songList) {

                // songList may be null until ready
                if (songList != null) {
                    mSongListAdapter.setSongList(songList);
                }
            }
        });

        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setAdapter(mSongListAdapter);

        // mLayout = new CoordinatorLayout(context);
        // FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        //         FrameLayout.LayoutParams.MATCH_PARENT,
        //         FrameLayout.LayoutParams.MATCH_PARENT
        // );
        // mLayout.setLayoutParams(params);
        // mLayout.addView(mRecyclerView);
        //
        // LayoutInflater.from(context).inflate(R.layout.fab, mLayout);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // inflater.inflate(R.layout.fab, mRecyclerView);
        return mRecyclerView;
    }
}
