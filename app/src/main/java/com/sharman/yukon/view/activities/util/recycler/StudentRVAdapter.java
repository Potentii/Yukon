package com.sharman.yukon.view.activities.util.recycler;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.Grade;

import org.json.JSONException;

import java.util.Vector;

/**
 * Created by poten on 24/10/2015.
 */
public class StudentRVAdapter extends RecyclerView.Adapter<StudentRVAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private Vector<StudentRVInfo> studentRVInfoVector = new Vector<>();
    private OnStudentRVItemClickListener onStudentRVItemClickListener;
    private GoogleAccountCredential credential;
    final private Activity context;

    public StudentRVAdapter(Activity context, GoogleAccountCredential credential, Vector<StudentRVInfo> studentRVInfoVector, OnStudentRVItemClickListener onStudentRVItemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.credential = credential;
        this.studentRVInfoVector = studentRVInfoVector;
        this.onStudentRVItemClickListener = onStudentRVItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = this.layoutInflater.inflate(R.layout.rv_row_student, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onStudentRVItemClickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final StudentRVInfo currentStudentRVInfo = studentRVInfoVector.get(i);

        viewHolder.studentRVInfo = currentStudentRVInfo;

        viewHolder.studentNameOut.setText(currentStudentRVInfo.getStudentName());
        viewHolder.studentGradeOut.setText(currentStudentRVInfo.getStudentGrade());


        DriveIOHandler driveIOHandler = new DriveIOHandler(credential);
        driveIOHandler.readFile(currentStudentRVInfo.getStudentGradeFileId(), new FileReadCallback() {
            @Override
            public void onSuccess(final String content) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Grade grade = new Grade(content);
                            currentStudentRVInfo.setStudentGrade(content);
                            viewHolder.studentGradeOut.setText(Double.toString(grade.getGrade()));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO error
                System.out.println(exception.getMessage());
            }
        });

        final PlusIOHandler plusIOHandler = new PlusIOHandler(credential);
        plusIOHandler.readPerson(currentStudentRVInfo.getStudentEmail(), new PersonReadCallback() {
            @Override
            public void onSuccess(final Person person) {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentStudentRVInfo.setStudentName(person.getDisplayName());
                        viewHolder.studentNameOut.setText(currentStudentRVInfo.getStudentName());
                    }
                });

                plusIOHandler.readPersonImg(person, new PersonImgReadCallback() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.studentImg.setImageBitmap(bitmap);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        //TODO error
                        //onFailure(errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO
                System.out.println(exception.getMessage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.studentRVInfoVector.size();
    }


    // *ViewHolder:
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView studentImg;
        private TextView studentNameOut;
        private TextView studentGradeOut;

        private StudentRVInfo studentRVInfo;

        public ViewHolder(View itemView, final OnStudentRVItemClickListener onStudentRVItemClickListener) {
            super(itemView);

            studentImg = (ImageView) itemView.findViewById(R.id.studentImg);
            studentNameOut = (TextView) itemView.findViewById(R.id.studentNameOut);
            studentGradeOut = (TextView) itemView.findViewById(R.id.studentGradeOut);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStudentRVItemClickListener.onClick(studentRVInfo);
                }
            });
        }
    }
}
