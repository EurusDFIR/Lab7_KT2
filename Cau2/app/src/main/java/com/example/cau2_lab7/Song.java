package com.example.cau2_lab7;

public class Song {
    private int id;
    private String name;
    private String releaseDate;
    private int albumId;

    public Song(int id, String name, String releaseDate, int albumId) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.albumId = albumId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public int getAlbumId() { return albumId; }
    public void setAlbumId(int albumId) { this.albumId = albumId; }


    @Override
    public String toString() {
        return this.id + ". " + this.name + "   " + this.releaseDate;
    }
}