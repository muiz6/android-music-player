package com.muiz6.musicplayer.di;

import android.content.ContentResolver;
import android.content.Context;

import androidx.room.Room;

import com.muiz6.musicplayer.data.db.AudioDatabase;
import com.muiz6.musicplayer.data.db.DatabaseCallback;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class RoomDatabaseModule {

	@Provides
	static ContentResolver provideContentResolver(@Named("Application") Context context) {
		return context.getContentResolver();
	}

	@Provides
	@Singleton
	static AudioDatabase provideRoomDatabase(@Named("Application") Context context,
			DatabaseCallback callback) {
		final AudioDatabase db = Room.databaseBuilder(context, AudioDatabase.class, "audio_database")
				.addCallback(callback)
				.fallbackToDestructiveMigration()
				.build();
		callback.setAudioDao(db.getAudioDao());
		return db;
	}
}
