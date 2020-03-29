package com.muiz6.musicplayer.models;

// fragile: handle with care
public class SongDataModel {

    private String _title;
    private String _path;
    private String _artist;

    public void setTitle(String title) {
        _title = title;
    }

    public void setPath(String path) {
        _path = path;
    }

    public void setArtist(String artist) {
        _artist = artist;
    }

    public String getTitle() {
        return _title;
    }

    public String getPath() {
        return _path;
    }

    public String getArtist() {
        return _artist;
    }
}
