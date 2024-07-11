package com.example.Student_Record_Data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TaskDBHelper mHelper;
    private RecyclerView mStudentRecyclerView;
    private StudentAdapter mAdapter;
    private ArrayList<Student> studentList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelper = new TaskDBHelper(this);
        mStudentRecyclerView = findViewById(R.id.recycler_students);
        mStudentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_task) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_add_student, null);

            EditText nameEditText = dialogView.findViewById(R.id.edit_name);
            EditText nimEditText = dialogView.findViewById(R.id.edit_nim);
            EditText courseEditText = dialogView.findViewById(R.id.edit_course);
            EditText ipkEditText = dialogView.findViewById(R.id.edit_ipk);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Tambahkan Data Mahasiswa Baru")
                    .setView(dialogView)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = nameEditText.getText().toString();
                            String nim = nimEditText.getText().toString();
                            String course = courseEditText.getText().toString();
                            String ipk = ipkEditText.getText().toString();

                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(TaskContract.StudentEntry.COL_NAME, name);
                            values.put(TaskContract.StudentEntry.COL_NIM, nim);
                            values.put(TaskContract.StudentEntry.COL_COURSE, course);
                            values.put(TaskContract.StudentEntry.COL_IPK, ipk);

                            db.insertWithOnConflict(TaskContract.StudentEntry.TABLE,
                                    null,
                                    values,
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            db.close();
                            updateUI();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public class TaskContract {
        public static final String DB_NAME = "com.example.students.data.db";
        public static final int DB_VERSION = 3;

        public class StudentEntry implements BaseColumns {
            public static final String TABLE = "students";
            public static final String COL_NAME = "name";
            public static final String COL_NIM = "nim";
            public static final String COL_IPK = "ipk";
            public static final String COL_COURSE = "course";
        }
    }

    public class TaskDBHelper extends SQLiteOpenHelper {
        public TaskDBHelper(Context context) {
            super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            String createStudentTable = "CREATE TABLE " + TaskContract.StudentEntry.TABLE + " ( "
                    + TaskContract.StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TaskContract.StudentEntry.COL_NAME + " TEXT NOT NULL, "
                    + TaskContract.StudentEntry.COL_NIM + " TEXT NOT NULL, "
                    + TaskContract.StudentEntry.COL_IPK + " REAL NOT NULL, "
                    + TaskContract.StudentEntry.COL_COURSE + " TEXT NOT NULL);";
            db.execSQL(createStudentTable);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TaskContract.StudentEntry.TABLE);
            onCreate(db);
        }
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView nimTextView = (TextView) parent.findViewById(R.id.student_nim);
        TextView nameTextView = (TextView) parent.findViewById(R.id.student_name);
        String nim = String.valueOf(nimTextView.getText()).replace("NIM: ", ""); // Ensure the NIM is correctly formatted
        String nama = String.valueOf(nameTextView.getText()).replace("Nama: ", "");
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.StudentEntry.TABLE,
                TaskContract.StudentEntry.COL_NIM + " = ? AND " +
                        TaskContract.StudentEntry.COL_NAME + " = ?",
                new String[]{nim, nama});
        db.close();
        updateUI();
    }

    private void updateUI() {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.StudentEntry.TABLE,
                new String[]{
                        TaskContract.StudentEntry._ID,
                        TaskContract.StudentEntry.COL_NAME,
                        TaskContract.StudentEntry.COL_NIM,
                        TaskContract.StudentEntry.COL_IPK,
                        TaskContract.StudentEntry.COL_COURSE},
                null, null, null, null, null);

        // Check if cursor contains the expected columns
        int nameIndex = cursor.getColumnIndex(TaskContract.StudentEntry.COL_NAME);
        int nimIndex = cursor.getColumnIndex(TaskContract.StudentEntry.COL_NIM);
        int courseIndex = cursor.getColumnIndex(TaskContract.StudentEntry.COL_COURSE);
        int ipkIndex = cursor.getColumnIndex(TaskContract.StudentEntry.COL_IPK);
        int idIndex = cursor.getColumnIndex(TaskContract.StudentEntry._ID);

        if (nameIndex == -1 || nimIndex == -1 || courseIndex == -1 || ipkIndex == -1 || idIndex == -1) {
            cursor.close();
            db.close();
            throw new IllegalStateException("Unexpected column index -1. Check column names in the database schema.");
        }

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIndex);
            String name = cursor.getString(nameIndex);
            String nim = cursor.getString(nimIndex);
            String course = cursor.getString(courseIndex);
            String ipk = cursor.getString(ipkIndex);

            studentList.add(new Student(name, nim, course, ipk));
        }
        cursor.close();
        db.close();

        if (mAdapter == null) {
            mAdapter = new StudentAdapter(this, new ArrayList<>(studentList));
            mStudentRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateStudentList(new ArrayList<>(studentList));
        }
    }

}
