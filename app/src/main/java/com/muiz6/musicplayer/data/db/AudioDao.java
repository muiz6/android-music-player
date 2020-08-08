package com.muiz6.musicplayer.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AudioDao {

	@Insert
	void insert(AudioEntity audio);

	@Query("DELETE FROM audio_table;")
	void deleteAll();

	@Query("SELECT path, display_name, title, artist, duration FROM audio_table ORDER BY display_name ASC;")
	List<SongPojo> getAllSongList();

	// group by artist and not album because different artist may share same album name
	@Query("SELECT album, artist FROM audio_table GROUP BY artist;")
	List<AlbumPojo> getAllAlbumList();

	@Query("Select artist FROM audio_table WHERE artist IS NOT NULL GROUP BY artist;")
	List<ArtistPojo> getAllArtistList();

	@Query("Select genre FROM audio_table WHERE genre IS NOT NULL GROUP BY genre;")
	List<GenrePojo> getGenreList();

	@Query("SELECT path, display_name, title, artist, duration FROM audio_table WHERE artist=:artist ORDER BY display_name ASC;")
	List<SongPojo> getSongListByArtist(String artist);

	@Query("SELECT path, display_name, title, artist, duration FROM audio_table WHERE genre=:genre ORDER BY display_name ASC;")
	List<SongPojo> getSongListByGenre(String genre);

	@Query("SELECT artist FROM audio_table WHERE genre=:genre AND artist IS NOT NULL GROUP BY artist;")
	List<ArtistPojo> getArtistListByGenre(String genre);

	@Query("SELECT album, artist FROM audio_table WhERE artist=:artist GROUP BY artist;")
	List<AlbumPojo> getAlbumListByArtist(String artist);

	@Query("SELECT album, artist FROM audio_table WhERE genre=:genre GROUP BY artist;")
	List<AlbumPojo> getAlbumListByGenre(String genre);

	@Query("SELECT path, display_name, title, artist, duration FROM audio_table WHERE album=:album ORDER BY display_name ASC;")
	List<SongPojo> getSongListByAlbum(String album);
}
