package info.androidhive.sqlite.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.DatabaseHelper;
import info.androidhive.sqlite.database.model.Student;
import info.androidhive.sqlite.utils.MyDividerItemDecoration;
import info.androidhive.sqlite.utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private StudentListAdapter mAdapter;
    private List<Student> studentsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;

    private String priority;

    private TextView noStudentsView;

    //Data base methods object reference
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting the toolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Getting views reference
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noStudentsView = findViewById(R.id.empty_students_view);

        db = new DatabaseHelper(this);

        //Prepare all data from the database to display
        studentsList.addAll(db.getAllStudents());

        //The button we use to add new student
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        //Setting up the recycler view
        mAdapter = new StudentListAdapter(this, studentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }


    /**
     * Inserting new name in db
     * and refreshing the list
     */
    private void addNewStudent(Student student) {

        Log.i(TAG, "addNewStudent: " + student.getPriority());

        // inserting student in db and getting
        // newly inserted name id
        long id = db.insertStudent(student);

        // get the newly inserted name from db
        Student n = db.getStudent(id);

        if (n != null) {
            // adding new name to the list.
            studentsList.add(n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }


    /**
     * Updating student data in db and updating
     * item in the list by its position
     */
    private void updateStudent(Student student, int position) {

        //Get the student position to get its data.
        Student n = studentsList.get(position);

        // updating name text
        n.setName(student.getName());
        n.setCourse(student.getCourse());
        n.setPriority(student.getPriority());

        // updating name in db
        db.updateStudent(n);

        // refreshing the list
        studentsList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }


    /**
     * Deleting student from SQLite and removing the
     * item from the list by its position
     */
    private void deleteStudent(int position) {

        // deleting the name from db
        db.deleteStudent(studentsList.get(position));

        // removing the name from the list
        studentsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }


    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 1
     */
    private void showActionsDialog(final int position) {

        //The buttons we need to show
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, studentsList.get(position), position);
                } else {
                    deleteStudent(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a student.
     * when shouldUpdate=true, it automatically displays old student and changes the
     * button text to UPDATE
     */
    View view;

    private void showNoteDialog(final boolean shouldUpdate, final Student student, final int position) {

        //Getting the design of the dialog
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        view = layoutInflaterAndroid.inflate(R.layout.student_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        //Getting the reference of the views.
        final EditText studentName = view.findViewById(R.id.student);
        final EditText studentCourse = view.findViewById(R.id.course);
        final RadioGroup studentPriority = view.findViewById(R.id.radioGroup);

        //For the title
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_student_title) : getString(R.string.lbl_edit_student_title)); //To check if clicked edit or add new student

        /*
         * if he want to edit and the student data != null
         * then we display the old data of this student
         * to be updated.
         */
        if (shouldUpdate && student != null) {

            //Show the data to be updated
            studentName.setText(student.getName());
            studentCourse.setText(student.getCourse());

            //Set the student priority
            if (student.getPriority().equals("1st Year")) {

                //Set the student priority
                studentPriority.check(R.id.firstYear);
                priority = "1st Year";

            } else if (student.getPriority().equals("2nd Year")) {

                studentPriority.check(R.id.secondYear);
                priority = "2nd Year";

            } else if (student.getPriority().equals("3rd Year")) {

                studentPriority.check(R.id.thirdYear);
                priority = "3rd Year";

            } else if (student.getPriority().equals("4th Year")) {

                studentPriority.check(R.id.fourthYear);
                priority = "4th Year";

            } else {

                studentPriority.check(R.id.graduated);
                priority = "Graduated";

            }
        }


        //init the buttons of the dialog
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(studentName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter student!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating student
                if (shouldUpdate && student != null) {
                    // update student by it's id
                    getSelectedRadioButton(studentPriority.getCheckedRadioButtonId()); //get selected priority

                    Student s = new Student(
                            studentName.getText().toString(),
                            studentCourse.getText().toString(),
                            priority
                    );

                    updateStudent(s, position);

                } else {
                    // create new student
                    getSelectedRadioButton(studentPriority.getCheckedRadioButtonId()); //get selected priority

                    Student newStudent = new Student(
                            studentName.getText().toString(),
                            studentCourse.getText().toString(),
                            priority
                    );

                    addNewStudent(newStudent);
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check studentsList.size() > 0
        // if there is no data in the SQLite database
        // we will show there is no data.
        if (db.getStudentsCount() > 0) {
            noStudentsView.setVisibility(View.GONE);
        } else {
            noStudentsView.setVisibility(View.VISIBLE);
        }
    }


    //Getting the selected radio button
    private void getSelectedRadioButton(int selectedId) {

        // Check which radio button was clicked
        switch (selectedId) {

            case R.id.firstYear:
                priority = "1st Year";
                break;

            case R.id.secondYear:
                priority = "2nd Year";
                break;

            case R.id.thirdYear:
                priority = "3rd Year";
                break;

            case R.id.fourthYear:
                priority = "4th Year";
                break;

            case R.id.graduated:
                priority = "Graduated";
                break;
        }
    }

}
