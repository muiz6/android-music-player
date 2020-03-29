package com.muiz6.musicplayer.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.models.SongDataModel;
import com.muiz6.musicplayer.ui.adapters.SongListAdapter;
import com.muiz6.musicplayer.viewmodels.SongDataViewModel;

import java.util.ArrayList;

public class SongFragment extends Fragment {

    private RecyclerView _recyclerView;

    // required public ctor
    public SongFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // initializing adapter with empty songlist
        final SongListAdapter songListAdapter = new SongListAdapter();

        final SongDataViewModel songDataViewModel =
            new ViewModelProvider(this).get(SongDataViewModel.class);

        songDataViewModel.getSongList()
            .observe(this, new Observer<ArrayList<SongDataModel>>() {
                @Override
                public void onChanged(ArrayList<SongDataModel> songList) {

                    // songList may be null until ready
                    if (songList != null) {
                        songListAdapter.setSongList(songList);
                    }
                }
            });

        _recyclerView = (RecyclerView) LayoutInflater.from(context)
            .inflate(R.layout.fragment_list, null);

        _recyclerView.setAdapter(songListAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return _recyclerView;
    }
}
