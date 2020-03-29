package com.muiz6.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.muiz6.musicplayer.models.SongDataModel;

import java.util.ArrayList;

// singleton pattern
// TODO: async task should be static
public class Repository {

    private static Repository _repo;
    private final MutableLiveData<ArrayList<SongDataModel>> _data = new MutableLiveData<>();

    private Repository(Context context) {

        // scan device once when repo is created
        refresh(context);
    }

    public static Repository getInstance(Context context) {
        if (_repo == null) {
            _repo = new Repository(context);
        }
        return _repo;
    }

    public LiveData<ArrayList<SongDataModel>> getSongList() {
        return _data;
    }

    public void refresh(final Context context) {
        final ArrayList<SongDataModel> items = new ArrayList<>();

        // TODO: asynnc task should be static
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.ArtistColumns.ARTIST,};
                Cursor cursor = context.getContentResolver().query(uri,
                        projection,
                        null,
                        null,
                        null);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        SongDataModel audioModel = new SongDataModel();
                        String path = cursor.getString(0);
                        String name = cursor.getString(1);
//                String album = c.getString(2);
                        String artist = cursor.getString(3);

                        audioModel.setTitle(name);
//                audioModel.setaAlbum(album);
                        if (artist.equals("<unknown>")) {
                            audioModel.setArtist("Unknown Artist");
                        }
                        else {
                            audioModel.setArtist(artist);
                        }
                        audioModel.setPath(path);

                        items.add(audioModel);
                    }
                    cursor.close();
                }

                return "Song Library Refreshed";
            }

            @Override
            protected void onPostExecute(String str) {
                super.onPostExecute(str);

                _data.setValue(items);
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}