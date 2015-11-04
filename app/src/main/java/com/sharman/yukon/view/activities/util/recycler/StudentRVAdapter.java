package com.sharman.yukon.view.activities.util.recycler;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.StudentContact;

import org.json.JSONException;

import java.util.List;
import java.util.Vector;

/**
 * Created by poten on 24/10/2015.
 */
public class StudentRVAdapter extends RecyclerView.Adapter<StudentRVAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private Vector<StudentRVInfo> studentRVInfoVector = new Vector<>();
    private List<StudentContact> studentContactList;
    private OnStudentRVItemClickListener onStudentRVItemClickListener;
    private GoogleAccountCredential credential;
    final private Activity activity;

    public StudentRVAdapter(Activity activity, GoogleAccountCredential credential, Vector<StudentRVInfo> studentRVInfoVector, List<StudentContact> studentContactList, OnStudentRVItemClickListener onStudentRVItemClickListener) {
        layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.credential = credential;
        this.studentRVInfoVector = studentRVInfoVector;
        this.studentContactList = studentContactList;
        this.onStudentRVItemClickListener = onStudentRVItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = layoutInflater.inflate(R.layout.recycler_student_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onStudentRVItemClickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final StudentRVInfo currentStudentRVInfo = studentRVInfoVector.get(i);

        // *If the grade file isn't downloaded yet:
        if(currentStudentRVInfo.getStudentGradeStr() == null){
            DriveIOHandler driveIOHandler = new DriveIOHandler(credential);
            driveIOHandler.readFile(currentStudentRVInfo.getStudentGradeFileId(), new FileReadCallback() {
                @Override
                public void onSuccess(final String content) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Grade grade = new Grade(content);
                                currentStudentRVInfo.setStudentGradeStr(content);
                                viewHolder.setGrade(grade);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    // TODO error
                    exception.printStackTrace();
                }
            });

        } else{
            try {
                Grade grade = new Grade(currentStudentRVInfo.getStudentGradeStr());
                viewHolder.setGrade(grade);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }


        // *If the student image or photo was not loaded yet:
        if(currentStudentRVInfo.getStudentName() == null || currentStudentRVInfo.getStudentImageUri() == null){
            String id = currentStudentRVInfo.getStudentEmail();
            StudentContact studentContactFound = null;

            for(StudentContact studentContact : studentContactList){
                if(id.equals(studentContact.getId())){
                    studentContactFound = studentContact;
                }
            }


            if(studentContactFound != null){
                currentStudentRVInfo.setStudentName(studentContactFound.getName());
                currentStudentRVInfo.setStudentImageUri(studentContactFound.getImageUri());
            } else{
                currentStudentRVInfo.setStudentName(currentStudentRVInfo.getStudentEmail());
                currentStudentRVInfo.setStudentImageUri("");
            }
        }

        viewHolder.studentNameOut.setText(currentStudentRVInfo.getStudentName());
        viewHolder.studentEmailOut.setText(currentStudentRVInfo.getStudentEmail());

        viewHolder.setImage(currentStudentRVInfo.getStudentImageUri());

        viewHolder.studentRVInfo = currentStudentRVInfo;
    }


    @Override
    public int getItemCount() {
        return this.studentRVInfoVector.size();
    }


    // *ViewHolder:
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView studentImg;
        private TextView studentNameOut;
        private TextView studentEmailOut;
        private TextView studentGradeOut;

        private StudentRVInfo studentRVInfo;

        public ViewHolder(View itemView, final OnStudentRVItemClickListener onStudentRVItemClickListener) {
            super(itemView);

            studentImg      = (ImageView)itemView.findViewById(R.id.studentImg);
            studentNameOut  = (TextView) itemView.findViewById(R.id.studentNameOut);
            studentEmailOut = (TextView) itemView.findViewById(R.id.studentEmailOut);
            studentGradeOut = (TextView) itemView.findViewById(R.id.studentGradeOut);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStudentRVItemClickListener.onClick(studentRVInfo);
                }
            });
        }


        public void setGrade(Grade grade){
            if(grade != null) {
                double gradeGrade = grade.getGrade();
                if (gradeGrade < 0) {

                    studentGradeOut.setText(activity.getResources().getString(R.string.output_grade_notSet_symbol));
                } else {
                    studentGradeOut.setText(Double.toString(grade.getGrade()));
                }
            } else{
                studentGradeOut.setText(activity.getResources().getString(R.string.output_grade_notSet_symbol));
            }
        }

        public void setImage(String imageUri){
            new AndroidUtil(activity).formatPersonImageView_AndroidContacts(studentImg, imageUri);
        }



    }
}
