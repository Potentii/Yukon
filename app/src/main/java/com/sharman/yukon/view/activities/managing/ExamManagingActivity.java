package com.sharman.yukon.view.activities.managing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;

import org.json.JSONException;

import java.text.SimpleDateFormat;

public class ExamManagingActivity extends GoogleRestConnectActivity {
    private TeacherConfigs teacherConfigs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing);
    }



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            // *Gets the teacherConfigs from intent:
            teacherConfigs = new TeacherConfigs(getIntent().getExtras().getString("teacherConfigs"));

            loadInfo();

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }


    private void loadInfo(){
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        final Activity activity = this;

        startProgressFragment();
        setProgressMessage("Loading data");
        driveIOHandler.readFile(teacherConfigs.getExamFileId(), new FileReadCallback() {
            @Override
            public void onSuccess(String content, Long lastModifiedDate) {
                stopProgressFragment();
                try {
                    final Exam exam = new Exam(content);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView descriptionOut = (TextView) findViewById(R.id.descriptionOut);
                            descriptionOut.setText(exam.getDescription());

                            // *Building the toolbar:
                            View infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
                            new AndroidUtil(activity).fillInfoPhotoToolbar_GPlusImage(
                                    infoPhotoHeader,
                                    getCredential(),
                                    exam.getTeacherId(),
                                    exam.getTitle(),
                                    exam.getSubject(),
                                    new SimpleDateFormat("dd/MM/yyyy").format(exam.getDeliverDate())
                            );
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                //TODO error
                stopProgressFragment();
                exception.printStackTrace();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listeners methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void studentsBtn_onClick(View view){
        Intent examManagingStudentsIntent = new Intent(this, ExamManagingStudentsActivity.class);
        examManagingStudentsIntent.putExtra("teacherConfigs", teacherConfigs.toString());
        /*
        examManagingStudentsIntent.putExtra("studentConfigsFileIdArray", teacherConfigs.getStudentConfigsFileIdArray());
        examManagingStudentsIntent.putExtra("teacherAnswerFileId", teacherConfigs.getCorrectAnswersFileId());
        */
        startActivity(examManagingStudentsIntent);
    }
}
