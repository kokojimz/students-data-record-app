package com.example.Student_Record_Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private Context mContext;
    private ArrayList<Student> mStudentList;

    public StudentAdapter(Context context, ArrayList<Student> studentList) {
        this.mContext = context;
        this.mStudentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = mStudentList.get(position);
        holder.nameTextView.setText("Nama: " + student.getName());
        holder.nimTextView.setText("NIM: " + student.getNim());
        holder.courseTextView.setText("Mata Kuliah: " + student.getCourse());
        holder.ipkTextView.setText("IPK: " + student.getIpk());
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }

    public void updateStudentList(ArrayList<Student> studentList) {
        this.mStudentList = studentList;
        notifyDataSetChanged();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView nimTextView;
        TextView courseTextView;
        TextView ipkTextView;

        public StudentViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.student_name);
            nimTextView = itemView.findViewById(R.id.student_nim);
            courseTextView = itemView.findViewById(R.id.student_course);
            ipkTextView = itemView.findViewById(R.id.student_ipk);
        }
    }
}
