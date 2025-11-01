package com.example.cau1_lab7;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText etCongViec, etNoiDung;
    private Button btnDate, btnTime, btnAdd;
    private ListView lvTasks;
    private TextView tvDateDisplay, tvTimeDisplay;

    private DBHelper dbHelper;
    private ArrayAdapter<Task> adapter;
    private ArrayList<Task> tasks = new ArrayList<>();

    private String selectedDate = "22/02/2013";
    private String selectedTime = "8:30 AM";

    private static final int MENU_EDIT = 1;
    private static final int MENU_DELETE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // anh xa cac view
        etCongViec = findViewById(R.id.etCongViec);
        etNoiDung = findViewById(R.id.etNoiDung);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        btnAdd = findViewById(R.id.btnAdd);
        lvTasks = findViewById(R.id.lvTasks);

        // Khoi tao Database
        dbHelper = new DBHelper(this);

        // KHoi tao listView adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        lvTasks.setAdapter(adapter);

        // Dang ky mot context menu
        registerForContextMenu(lvTasks);

        loadTasksFromDb();

        btnDate.setOnClickListener(v -> showDatePicker());
        btnTime.setOnClickListener(v -> showTimePicker());
        btnAdd.setOnClickListener(v -> addTask());

        btnDate.setText(selectedDate);
        btnTime.setText(selectedTime);
    }

    private void loadTasksFromDb() {
        tasks.clear();
        tasks.addAll(dbHelper.getAllTasks());
        adapter.notifyDataSetChanged();
    }

    private void addTask() {
        String title = etCongViec.getText().toString().trim();
        String content = etNoiDung.getText().toString().trim();
        String datetime = selectedDate + " - " + selectedTime;

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(-1, title, content, datetime);
        long id = dbHelper.insertTask(task);

        if (id != -1) {
            task.setId(id);
            tasks.add(task);
            adapter.notifyDataSetChanged();

            etCongViec.setText("");
            etNoiDung.setText("");
            selectedDate = "22/02/2013";
            selectedTime = "8:30 AM";
            btnDate.setText(selectedDate);
            btnTime.setText(selectedTime);

            Toast.makeText(this, "Đã thêm công việc", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    btnDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String amPm = selectedHour >= 12 ? "PM" : "AM";
                    int hour12 = selectedHour % 12;
                    if (hour12 == 0)
                        hour12 = 12;
                    selectedTime = String.format("%d:%02d %s", hour12, selectedMinute, amPm);
                    btnTime.setText(selectedTime);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvTasks) {
            menu.setHeaderTitle("Chọn hành động");
            menu.add(0, MENU_EDIT, 0, "Sửa");
            menu.add(0, MENU_DELETE, 1, "Xoá");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Task task = tasks.get(position);

        if (item.getItemId() == MENU_EDIT) {
            showEditDialog(task);
            return true;
        } else if (item.getItemId() == MENU_DELETE) {
            showDeleteConfirmation(task);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showEditDialog(Task task) {
        // Create dialog layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etEditTitle = new EditText(this);
        etEditTitle.setHint("Tên công việc");
        etEditTitle.setText(task.getTitle());
        layout.addView(etEditTitle);

        final EditText etEditContent = new EditText(this);
        etEditContent.setHint("Nội dung");
        etEditContent.setText(task.getContent());
        layout.addView(etEditContent);

        new AlertDialog.Builder(this)
                .setTitle("Sửa công việc")
                .setView(layout)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newTitle = etEditTitle.getText().toString().trim();
                    String newContent = etEditContent.getText().toString().trim();

                    if (newTitle.isEmpty()) {
                        Toast.makeText(this, "Tên công việc không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    task.setTitle(newTitle);
                    task.setContent(newContent);
                    dbHelper.updateTask(task);
                    loadTasksFromDb();
                    Toast.makeText(this, "Đã cập nhật công việc", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirmation(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá công việc")
                .setMessage("Bạn có chắc muốn xoá công việc này?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    dbHelper.deleteTask(task.getId());
                    loadTasksFromDb();
                    Toast.makeText(this, "Đã xoá công việc", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}