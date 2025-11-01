package com.example.cau1_lab7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TaskManager.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DATETIME = "datetime";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_CONTENT + " TEXT, " +
            COLUMN_DATETIME + " TEXT)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Them Cong viec moi
    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_DATETIME, task.getDatetime());
        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    // Sua lai cong viec
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_CONTENT, task.getContent());
        values.put(COLUMN_DATETIME, task.getDatetime());
        int rows = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
        db.close();
        return rows;
    }

    // xoa cong viec
    public void deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    // Lay tat ca cong viec
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int datetimeIndex = cursor.getColumnIndex(COLUMN_DATETIME);

            do {
                long id = cursor.getLong(idIndex);
                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                String datetime = cursor.getString(datetimeIndex);
                tasks.add(new Task(id, title, content, datetime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    // Lay moi cong viec dua tren Id
    public Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        Task task = null;
        if (cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndex(COLUMN_CONTENT);
            int datetimeIndex = cursor.getColumnIndex(COLUMN_DATETIME);

            String title = cursor.getString(titleIndex);
            String content = cursor.getString(contentIndex);
            String datetime = cursor.getString(datetimeIndex);
            task = new Task(id, title, content, datetime);
        }
        cursor.close();
        db.close();
        return task;
    }
}
