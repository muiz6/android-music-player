package com.muiz6.musicplayer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.ui.adapters.SongListAdapter;
import com.muiz6.musicplayer.viewmodels.SongDataViewModel;

import java.util.ArrayList;

// TODO: may need to extend from LifecycleFragment for viewmodel provider
public class SongsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SongDataViewModel mSongDataViewModel;
    private SongListAdapter mSongListAdapter;

    public SongsFragment() {
//        this.items = items;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // initializing
        mSongListAdapter = new SongListAdapter();

        // attaching fragment to view model
        // TODO: use viewmodel provider
        // mSongDataViewModel = ViewModelProviders.of(this).get(SongDataViewModel.class);
        mSongDataViewModel = new SongDataViewModel(context);

        mSongDataViewModel = new SongDataViewModel(getActivity());
        mSongDataViewModel.getSongList().observe(this, new Observer<ArrayList<SongDataModel>>() {
            @Override
            public void onChanged(ArrayList<SongDataModel> songList) {

                // songList will be null until ready
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRecyclerView;
    }
}
