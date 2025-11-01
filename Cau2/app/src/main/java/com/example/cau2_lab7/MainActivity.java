package com.example.cau2_lab7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_EDIT = 101;

    Spinner spinnerAlbum;
    EditText etSongName, etReleaseDate;
    Button btnAddSong;
    ListView lvSongs;

    SongDatabaseHelper dbHelper;

    ArrayAdapter<Album> albumAdapter;
    ArrayAdapter<Song> songAdapter;

    List<Album> albumList = new ArrayList<>();
    List<Song> currentSongList = new ArrayList<>();
    Album selectedAlbum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new SongDatabaseHelper(this);

        spinnerAlbum = findViewById(R.id.spinnerAlbum);
        etSongName = findViewById(R.id.etSongName);
        etReleaseDate = findViewById(R.id.etReleaseDate);
        btnAddSong = findViewById(R.id.btnAddSong);
        lvSongs = findViewById(R.id.lvSongs);

        setupSpinner();
        setupListView();
        setupListeners();

        registerForContextMenu(lvSongs);

        loadAlbums();
    }

    private void loadAlbums() {
        albumList.clear();
        albumList.addAll(dbHelper.getAllAlbums());
        albumAdapter.notifyDataSetChanged();

        if (!albumList.isEmpty()) {
            spinnerAlbum.setSelection(0);
            selectedAlbum = albumList.get(0);
            updateSongList(selectedAlbum.getId());
        }
    }

    private void setupSpinner() {
        albumAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                albumList);
        albumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlbum.setAdapter(albumAdapter);
    }

    private void setupListView() {
        songAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                currentSongList);
        lvSongs.setAdapter(songAdapter);
    }

    private void setupListeners() {
        spinnerAlbum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAlbum = (Album) parent.getItemAtPosition(position);
                updateSongList(selectedAlbum.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAlbum = null;
                updateSongList(-1);
            }
        });

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = currentSongList.get(position);
                etSongName.setText(song.getName());
                etReleaseDate.setText(song.getReleaseDate());
            }
        });

        btnAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAlbum == null) {
                    Toast.makeText(MainActivity.this, "Vui lòng chọn album trước", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, AddEditSongActivity.class);
                intent.putExtra("ALBUM_ID", selectedAlbum.getId());
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        });
    }

    private void updateSongList(int albumId) {
        currentSongList.clear();
        if (albumId != -1) {
            currentSongList.addAll(dbHelper.getSongsByAlbum(albumId));
        }
        songAdapter.notifyDataSetChanged();

        etSongName.setText("");
        etReleaseDate.setText("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvSongs) {
            getMenuInflater().inflate(R.menu.song_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Song selectedSong = currentSongList.get(position);

        int itemId = item.getItemId();
        if (itemId == R.id.menu_add) {
            btnAddSong.performClick();
            return true;
        } else if (itemId == R.id.menu_edit) {
            Intent intent = new Intent(MainActivity.this, AddEditSongActivity.class);
            intent.putExtra("SONG_ID", selectedSong.getId());
            startActivityForResult(intent, REQUEST_CODE_EDIT);
            return true;
        } else if (itemId == R.id.menu_delete) {
            showDeleteConfirmation(selectedSong);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void showDeleteConfirmation(Song song) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá bài hát '" + song.getName() + "'?")
                .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteSong(song);
                        updateSongList(selectedAlbum.getId());
                        Toast.makeText(MainActivity.this, "Đã xoá", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == REQUEST_CODE_ADD || requestCode == REQUEST_CODE_EDIT)) {
            if (selectedAlbum != null) {
                updateSongList(selectedAlbum.getId());
                Toast.makeText(this, "Đã cập nhật danh sách", Toast.LENGTH_SHORT).show();
            }
        }
    }
}