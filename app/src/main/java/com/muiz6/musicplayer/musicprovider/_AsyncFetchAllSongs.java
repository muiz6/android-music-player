package com.muiz6.musicplayer.musicprovider;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import androidx.media.MediaBrowserServiceCompat;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * needs media browser service reference to notify children change
 */
class _AsyncFetchAllSongs extends AsyncTask<Void, Void, String> {

    private final List<MediaBrowserCompat.MediaItem> _itemList;
    private final WeakReference<MediaBrowserServiceCompat> _refService;
    private final MediaDescriptionCompat.Builder _descriptionBuilder;

    public _AsyncFetchAllSongs(WeakReference<MediaBrowserServiceCompat> service,
            List<MediaBrowserCompat.MediaItem> itemList) {
        _refService = service;
        _itemList = itemList;
        _descriptionBuilder = new MediaDescriptionCompat.Builder();
    }

    @Override
    protected String doInBackground(Void... voids) {
        final MediaBrowserServiceCompat service = _refService.get();
        if (service != null) {
            final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            final String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    MediaStore.Audio.ArtistColumns.ARTIST};
            final Cursor cursor = service.getContentResolver().query(uri,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.AudioColumns.TITLE + " ASC");

            if (cursor != null) {
                int i = 0;
                while (cursor.moveToNext()) {
                    final String path = cursor.getString(0);
                    final String name = cursor.getString(1);
                    final String album = cursor.getString(2);
                    String artist = cursor.getString(3);
                    if (artist.equals("<unknown>")) {
                        artist = null;
                    }
                    final String mediaId = MusicProvider.MEDIA_ID_ALL_SONGS
                            + MusicProvider.SEPARATOR_MEDIA_ID
                            + i++;
                    _itemList.add(new MediaBrowserCompat.MediaItem(_getDescription(mediaId,
                            Uri.parse(path),
                            name,
                            artist,
                            album),
                            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                }
                cursor.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        final MediaBrowserServiceCompat service = _refService.get();
        if (service != null) {
            service.notifyChildrenChanged(MusicProvider.MEDIA_ID_ALL_SONGS);
        }
    }

    private MediaDescriptionCompat _getDescription(String mediaId,
            Uri mediaUri,
            String title,
            String subtitle,
            String description) {
        _descriptionBuilder.setExtras(null);
        _descriptionBuilder.setIconBitmap(null);
        _descriptionBuilder.setIconUri(null);
        _descriptionBuilder.setMediaId(mediaId);
        _descriptionBuilder.setMediaUri(mediaUri);
        _descriptionBuilder.setTitle(title);
        _descriptionBuilder.setSubtitle(subtitle);
        _descriptionBuilder.setDescription(description);
        return _descriptionBuilder.build();
    }
}
