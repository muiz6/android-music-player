package com.muiz6.musicplayer.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AudioEntity.class},
		version = 1,
		exportSchema = false)
public abstract class AudioDatabase extends RoomDatabase {

	public abstract AudioDao getAudioDao();
}
