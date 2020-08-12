package com.muiz6.musicplayer.data.db;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.muiz6.musicplayer.R;
import com.muiz6.musicplayer.permission.PermissionManager;

import java.util.concurrent.TimeUnit;

@Database(entities = {AudioEntity.class},
		version = 1,
		exportSchema = false)
public abstract class AudioDatabase extends RoomDatabase {

	private final Handler _handler = new Handler(Looper.getMainLooper());
	private Callback _callback;
	private Context _context;
	private PermissionManager _permissionManager;

	public abstract AudioDao getAudioDao();

	/**
	 * deletes previous library data and rescans music library
	 */
	public void scanMusicLibrary() {
		// _actuallyScanLibrary();
		_permissionManager.requestPermissionWhenReady(Manifest.permission.READ_EXTERNAL_STORAGE,
				new PermissionManager.Callback() {

					@Override
					public void onRequestPermissionResult(boolean granted) {

						if (granted) {
							_actuallyScanLibrary();
						}
					}
				});
	}

	public void setArguments(Context context, PermissionManager manager) {
		_context = context;
		_permissionManager = manager;
	}

	/**
	 * Set callback to be called after database operations such as scanning music library.
	 * You must handle the thread yourself. By default callback will be called in background
	 * thread.
	 * @param callback callback to be called after database operations
	 */
	public void setCallback(@Nullable Callback callback) {
		_callback = callback;
	}

	private void _actuallyScanLibrary() {
		_handler.post(new Runnable() {

			@Override
			public void run() {
				final String msg = _context.getString(R.string.scanning_music_library);
				Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				getAudioDao().deleteAll();

				// these constants do work on api-21
				@SuppressLint("InlinedApi") final String[] projection =
						{MediaStore.Audio.AudioColumns.DATA,
								MediaStore.Audio.AudioColumns.DISPLAY_NAME,
								MediaStore.Audio.AudioColumns.TITLE,
								MediaStore.Audio.AudioColumns.ALBUM,
								MediaStore.Audio.AudioColumns.ARTIST,
								MediaStore.Audio.AudioColumns.DURATION};

				// ignore 30s and shorter tracks
				// todo: get ignore duration from shared preferences
				final String selection = MediaStore.Audio.AudioColumns.DURATION + " > ?";
				final String[] selectionArgs = {String.valueOf(TimeUnit.MILLISECONDS.convert(30,
						TimeUnit.SECONDS))};

				// fetch external storage content
				// todo: add permission request here
				final Cursor cursor = _context.getContentResolver()
						.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								projection,
								selection,
								selectionArgs,
								null);
				if (cursor != null) {
					while (cursor.moveToNext()) {
						_addRow(cursor, _context);
					}
					cursor.close();
				}
				if (_callback != null) {
					_callback.onCompletion(true);
				}
			}
		}).start();
	}

	private void _addRow(final Cursor cursor, Context context) {
		final String path = cursor.getString(0);
		final String displayName = cursor.getString(1);
		final String title = cursor.getString(2);
		final String album = cursor.getString(3);
		String artist = cursor.getString(4);
		if (artist.equals("<unknown>")) {
			artist = null;
		}
		final int duration = cursor.getInt(5);

		// extract genre
		// final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		// retriever.setDataSource(context, Uri.parse(path));
		// final String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
		// retriever.getEmbeddedPicture();

		final AudioEntity audioEntity = new AudioEntity(path, displayName);
		audioEntity.setTitle(title);
		audioEntity.setAlbum(album);
		audioEntity.setArtist(artist);
		audioEntity.setDuration(duration);
		// audioEntity.setGenre(genre);

		getAudioDao().insert(audioEntity);
	}

	public interface Callback {

		/**
		 * A callback called when the requested Room db operation has completed. You must handle
		 * the thread yourself. By default the callback will be called in background thread.
		 * @param success true if operation was successful, false otherwise
		 */
		void onCompletion(boolean success);
	}
}
