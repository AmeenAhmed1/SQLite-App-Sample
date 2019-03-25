package info.androidhive.sqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.database.model.Student;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "student_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create students table
        db.execSQL(Student.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertStudent(Student student) {

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // `id` will be inserted automatically.
        // no need to add them
        values.put(Student.COLUMN_STUDENT_NAME, student.getName());
        values.put(Student.COLUMN_STUDENT_COURSE, student.getCourse());
        values.put(Student.COLUMN_STUDENT_PRIORITY, student.getPriority());

        // insert row
        long id = db.insert(Student.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Student getStudent(long id) {

        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.COLUMN_ID, Student.COLUMN_STUDENT_NAME, Student.COLUMN_STUDENT_COURSE, Student.COLUMN_STUDENT_PRIORITY},
                Student.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare student object
        Student student = new Student(
                cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_NAME)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_COURSE)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_PRIORITY)));

        // close the db connection
        cursor.close();

        //return student data
        return student;
    }

    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Student.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        // to get every student.
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)));
                student.setName(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_NAME)));
                student.setCourse(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_COURSE)));
                student.setPriority(cursor.getString(cursor.getColumnIndex(Student.COLUMN_STUDENT_PRIORITY)));

                //Add the student to the list
                students.add(student);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return students list
        return students;
    }

    public int getStudentsCount() {
        String countQuery = "SELECT  * FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public long updateStudent(Student student) {

        SQLiteDatabase db = this.getWritableDatabase();

        //Sending the new values
        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_STUDENT_NAME, student.getName());
        values.put(Student.COLUMN_STUDENT_COURSE, student.getCourse());
        values.put(Student.COLUMN_STUDENT_PRIORITY, student.getPriority());

        // updating row
        long newRowUpdate = db.update(Student.TABLE_NAME,
                values,
                Student.COLUMN_ID + " =?",
                new String[]{String.valueOf(student.getId())});

        return newRowUpdate;
    }

    public void deleteStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Student.TABLE_NAME, Student.COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
        db.close();
    }
}
