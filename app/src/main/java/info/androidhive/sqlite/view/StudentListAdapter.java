package info.androidhive.sqlite.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.model.Student;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> {

    private Context context;
    private List<Student> studentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //View variables
        public TextView name;
        public TextView dot;
        public TextView course;
        public TextView priority;

        public MyViewHolder(View view) {
            super(view);

            //Getting the reference of the views.
            name = view.findViewById(R.id.student);
            dot = view.findViewById(R.id.dot);
            course = view.findViewById(R.id.course);
            priority = view.findViewById(R.id.priority);
        }
    }


    public StudentListAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentList.get(position);

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        //Display the student data
        holder.name.setText(student.getName());
        holder.course.setText(student.getCourse());
        holder.priority.setText(student.getPriority());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

}
