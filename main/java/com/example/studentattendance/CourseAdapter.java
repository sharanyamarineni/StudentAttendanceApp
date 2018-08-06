package com.example.studentattendance;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.courseVH> {

    String[] cID,cName,Grade,mName;

    public CourseAdapter(String[] id, String[] c_name, String[] grade, String[] m_name){
        cID = id;
        cName = c_name;
        Grade = grade;
        mName = m_name;

    }

    @NonNull
    @Override
    public courseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.course_recycle, parent, false);
        courseVH vh = new courseVH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull courseVH holder, int position) {
        holder.idDisplay.setText(cID[position]);
        holder.nameDisplay.setText(cName[position]);
        holder.gradeDisplay.setText(Grade[position]);
        holder.mentorDisplay.setText(mName[position]);
    }

    @Override
    public int getItemCount() {
        return cID.length;
    }

    public class courseVH extends RecyclerView.ViewHolder{

        TextView idDisplay,nameDisplay,gradeDisplay,mentorDisplay;
        public courseVH(View itemView) {
            super(itemView);
            idDisplay = itemView.findViewById(R.id.tv_courseIDDataViewHolder);
            nameDisplay = itemView.findViewById(R.id.tv_courseNameDataViewHolder);
            gradeDisplay = itemView.findViewById(R.id.tv_courseGradeDataViewHolder);
            mentorDisplay = itemView.findViewById(R.id.tv_courseMentorDataViewHolder);
        }
    }
}
