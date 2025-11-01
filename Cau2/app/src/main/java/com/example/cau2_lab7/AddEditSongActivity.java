package com.example.cau2_lab7;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditSongActivity extends AppCompatActivity {

    EditText etEditSongName, etEditReleaseDate;
    Button btnSave;


    SongDatabaseHelper dbHelper;

    private boolean isEditMode = false;
    private int songIdToEdit = -1;
    private int albumIdToAdd = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);


        etEditSongName = findViewById(R.id.etEditSongName);
        etEditReleaseDate = findViewById(R.id.etEditReleaseDate);
        btnSave = findViewById(R.id.btnSave);

        etEditSongName.setEnabled(true);
        etEditSongName.setFocusable(true);
        etEditSongName.setFocusableInTouchMode(true);

        dbHelper = new SongDatabaseHelper(this);

        Intent intent = getIntent();
        if (intent.hasExtra("SONG_ID")) {
            isEditMode = true;
            songIdToEdit = intent.getIntExtra("SONG_ID", -1);
            Song song = dbHelper.findSongById(songIdToEdit);
            if (song != null) {
                etEditSongName.setText(song.getName());
                etEditReleaseDate.setText(song.getReleaseDate());
            }
            setTitle("Sửa bài hát");

        } else if (intent.hasExtra("ALBUM_ID")) {
            isEditMode = false;
            albumIdToAdd = intent.getIntExtra("ALBUM_ID", -1);
            setTitle("Thêm bài hát mới");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSong();
            }
        });
    }

    private void saveSong() {
        String name = etEditSongName.getText().toString().trim();
        String date = etEditReleaseDate.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            Song song = dbHelper.findSongById(songIdToEdit);
            if (song != null) {
                song.setName(name);
                song.setReleaseDate(date);
                dbHelper.updateSong(song);
            }
        } else {
            dbHelper.addSong(name, date, albumIdToAdd);
        }

        setResult(RESULT_OK);
        finish();
    }
}