package com.example.cau2_lab7;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SongDatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "song_manager.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ALBUMS = "albums";
    private static final String COL_ALBUM_ID = "album_id";
    private static final String COL_ALBUM_NAME = "album_name";

    private static final String TABLE_SONGS = "songs";
    private static final String COL_SONG_ID = "song_id";
    private static final String COL_SONG_NAME = "song_name";
    private static final String COL_SONG_DATE = "song_release_date";
    private static final String COL_SONG_ALBUM_ID = "album_id";


    private static final String CREATE_TABLE_ALBUMS = "CREATE TABLE " + TABLE_ALBUMS + "(" +
            COL_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_ALBUM_NAME + " TEXT" +
            ")";

    private static final String CREATE_TABLE_SONGS = "CREATE TABLE " + TABLE_SONGS + "(" +
            COL_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_SONG_NAME + " TEXT, " +
            COL_SONG_DATE + " TEXT, " +
            COL_SONG_ALBUM_ID + " INTEGER" +
            ")";


    public SongDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_ALBUMS);
        db.execSQL(CREATE_TABLE_SONGS);

        addSampleAlbum(db, "Album 1: Nhạc Piano");
        addSampleAlbum(db, "Album 2: Nhạc Phonk");
        addSampleAlbum(db, "Album 3: Nhạc Pops");

        addSampleSong(db, "My Heart Will go on", "21-02-2012", 1);
        addSampleSong(db, "Never gonna give you up", "21-02-2013", 1);
        addSampleSong(db, "Rave", "20-01-2022", 2);
        addSampleSong(db, "Aura", "15-05-2009", 2);
        addSampleSong(db, "Murder in My Mind", "20-05-2020",2);
        addSampleSong(db, "Nhac Pop 1", "10-01-2016", 3);
        addSampleSong(db, "Nhap Pop 2", "20-02-2022",3);
        addSampleSong(db, "Nhac Pop 3", "21-03-2024",3);
    }


    private void addSampleAlbum(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put(COL_ALBUM_NAME, name);
        db.insert(TABLE_ALBUMS, null, values);
    }


    private void addSampleSong(SQLiteDatabase db, String name, String date, int albumId) {
        ContentValues values = new ContentValues();
        values.put(COL_SONG_NAME, name);
        values.put(COL_SONG_DATE, date);
        values.put(COL_SONG_ALBUM_ID, albumId);
        db.insert(TABLE_SONGS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        onCreate(db);
    }



    public List<Album> getAllAlbums() {
        List<Album> albumList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALBUMS, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COL_ALBUM_ID);
                int nameIndex = cursor.getColumnIndex(COL_ALBUM_NAME);

                if(idIndex != -1 && nameIndex != -1) {
                    Album album = new Album(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex)
                    );
                    albumList.add(album);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return albumList;
    }

    public List<Song> getSongsByAlbum(int albumId) {
        List<Song> songList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String selection = COL_SONG_ALBUM_ID + " = ?";
        String[] selectionArgs = { String.valueOf(albumId) };

        Cursor cursor = db.query(TABLE_SONGS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COL_SONG_ID);
                int nameIndex = cursor.getColumnIndex(COL_SONG_NAME);
                int dateIndex = cursor.getColumnIndex(COL_SONG_DATE);
                int albumIdIndex = cursor.getColumnIndex(COL_SONG_ALBUM_ID);

                if (idIndex != -1 && nameIndex != -1 && dateIndex != -1 && albumIdIndex != -1) {
                    Song song = new Song(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(dateIndex),
                            cursor.getInt(albumIdIndex)
                    );
                    songList.add(song);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songList;
    }

    public Song findSongById(int songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_SONG_ID + " = ?";
        String[] selectionArgs = { String.valueOf(songId) };

        Cursor cursor = db.query(TABLE_SONGS, null, selection, selectionArgs, null, null, null);
        Song song = null;

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COL_SONG_ID);
            int nameIndex = cursor.getColumnIndex(COL_SONG_NAME);
            int dateIndex = cursor.getColumnIndex(COL_SONG_DATE);
            int albumIdIndex = cursor.getColumnIndex(COL_SONG_ALBUM_ID);

            if (idIndex != -1 && nameIndex != -1 && dateIndex != -1 && albumIdIndex != -1) {
                song = new Song(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(dateIndex),
                        cursor.getInt(albumIdIndex)
                );
            }
        }
        cursor.close();
        db.close();
        return song;
    }

    public void addSong(String name, String date, int albumId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SONG_NAME, name);
        values.put(COL_SONG_DATE, date);
        values.put(COL_SONG_ALBUM_ID, albumId);


        db.insert(TABLE_SONGS, null, values);
        db.close();
    }

    public void updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SONG_NAME, song.getName());
        values.put(COL_SONG_DATE, song.getReleaseDate());

        String whereClause = COL_SONG_ID + " = ?";
        String[] whereArgs = { String.valueOf(song.getId()) };

        db.update(TABLE_SONGS, values, whereClause, whereArgs);
        db.close();
    }

    public void deleteSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COL_SONG_ID + " = ?";
        String[] whereArgs = { String.valueOf(song.getId()) };

        db.delete(TABLE_SONGS, whereClause, whereArgs);
        db.close();
    }
}