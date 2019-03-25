package info.androidhive.sqlite.database.model;


//This is a Student data model
public class Student {
    public static final String TABLE_NAME = "Students";

    //Here are the table columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_STUDENT_COURSE = "course";
    public static final String COLUMN_STUDENT_PRIORITY = "priority";


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_STUDENT_NAME + " TEXT,"
                    + COLUMN_STUDENT_COURSE + " TEXT,"
                    + COLUMN_STUDENT_PRIORITY + " TEXT"
                    + ")";



    //Variables to be used
    //For every student
    private int id;
    private String name;
    private String course;
    private String priority;


    public Student(String name, String course, String priority) {
        this.name = name;
        this.course = course;
        this.priority = priority;
    }

    public Student(int id, String name, String course, String priority) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.priority = priority;
    }

    public Student() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
