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

    private static Repository repo;
    private final MutableLiveData<ArrayList<SongDataModel>> data = new MutableLiveData<>();

    private Repository(Context context) {

        // scan device once when repo is created
        refresh(context);
    }

    public static Repository getInstance(Context context) {
        if (repo == null) {
            repo = new Repository(context);
        }
        return repo;
    }

    public LiveData<ArrayList<SongDataModel>> getSongList() {
        return data;
    }

    public void refresh(final Context context) {
        final ArrayList<SongDataModel> items = new ArrayList<>();

//        AsyncQueryHandler handler = new AsyncQueryHandler(context.getContentResolver()) {
//            @Override
//            public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
//                super.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
//            }
//
//            @Override
//            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//                super.onQueryComplete(token, cookie, cursor);
//            }
//        };

//        handler.startQuery(0, new Object(), uri);

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

                data.setValue(items);
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}