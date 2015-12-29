package com.sharman.yukon.view.activities.util.recycler;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.view.activities.util.AndroidUtil;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by poten on 24/10/2015.
 */
public abstract class StudentRVAdapter extends RecyclerView.Adapter<StudentRVAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final Activity activity;

    private RecyclerView recyclerView;

    @Nonnull
    private List<StudentRVInfo> studentRVInfoList;



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public StudentRVAdapter(@Nonnull Activity activity, @Nonnull List<StudentRVInfo> studentRVInfoList) {
        layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.studentRVInfoList = studentRVInfoList;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * RecyclerView.Adapter methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(this.layoutInflater.inflate(R.layout.row_student_managing, parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public final int getItemCount() {
        return this.studentRVInfoList.size();
    }

    @Override
    public final void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final StudentRVInfo currentStudentRVInfo = studentRVInfoList.get(i);

        new AndroidUtil(activity).formatPersonImageView_AndroidContacts(viewHolder.studentImg, currentStudentRVInfo.getStudentContact().getImageUri());

        viewHolder.nameOut.setText(currentStudentRVInfo.getStudentContact().getName());
        viewHolder.emailOut.setText(currentStudentRVInfo.getStudentContact().getId());

        double gradeDouble = currentStudentRVInfo.getGrade().getGrade();
        if(gradeDouble<0){
            // *Grade not set yet
            viewHolder.gradeOut.setText(activity.getResources().getString(R.string.output_grade_notSet_text));
        } else{
            // *Grade set
            viewHolder.gradeOut.setText(new DecimalFormat("#.00").format(gradeDouble));
        }


        viewHolder.studentRVInfo = currentStudentRVInfo;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * ViewHolder:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameOut;
        private TextView emailOut;
        private TextView gradeOut;
        private ImageView studentImg;

        private StudentRVInfo studentRVInfo;

        public ViewHolder(View itemView) {
            super(itemView);

            nameOut     = (TextView) itemView.findViewById(R.id.nameOut);
            emailOut    = (TextView) itemView.findViewById(R.id.emailOut);
            gradeOut    = (TextView) itemView.findViewById(R.id.gradeOut);
            studentImg  = (ImageView) itemView.findViewById(R.id.studentImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick(studentRVInfo);
                }
            });
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected abstract void onItemClick(StudentRVInfo studentRVInfo);

    public void update(){
        super.notifyDataSetChanged();
        if(recyclerView != null){
            recyclerView.setMinimumHeight((int) (getItemCount() * activity.getResources().getDimension(R.dimen.row_student_managing_height)));
        }
    }
}
