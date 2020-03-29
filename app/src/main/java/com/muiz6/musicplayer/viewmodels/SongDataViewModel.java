package com.muiz6.musicplayer.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.muiz6.musicplayer.Repository;
import com.muiz6.musicplayer.models.SongDataModel;

import java.util.ArrayList;

public class SongDataViewModel extends AndroidViewModel {

    private LiveData<ArrayList<SongDataModel>> _songList;

    public SongDataViewModel(Application application) {
        super(application);

        _songList = Repository.getInstance(application).getSongList();
    }

    public LiveData<ArrayList<SongDataModel>> getSongList() {
        return _songList;
    }
}
