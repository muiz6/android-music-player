package com.muiz6.musicplayer.models;

// fragile: handle with care
public class SongDataModel {

    private String title;
    private String path;
    private String artist;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getArtist() {
        return artist;
    }
}
