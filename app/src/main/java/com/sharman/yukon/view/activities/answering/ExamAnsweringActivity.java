package com.sharman.yukon.view.activities.answering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;

import org.json.JSONException;

import java.text.SimpleDateFormat;

public class ExamAnsweringActivity extends GoogleRestConnectActivity {
    private String studentAnswerFileId;
    private String examFileId;
    private String gradeFileId;

    private Exam exam;

    private boolean allFilesLoaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answering);
        allFilesLoaded = false;
    }

    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();


        //new AndroidUtil(this).showToast("Loading exam...", Toast.LENGTH_SHORT);

        try {
            // *Gets the studentConfigs from intent:
            StudentConfigs studentConfigs = new StudentConfigs(getIntent().getExtras().getString("studentConfigs"));

            studentAnswerFileId = studentConfigs.getAnswersFileId();
            examFileId = studentConfigs.getExamFileId();
            gradeFileId = studentConfigs.getGradeFileId();

            loadFiles();

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }

    private void loadFiles(){
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        final Activity activity = this;

        startProgressFragment();
        setProgressMessage("Loading exam");
        driveIOHandler.readFile(examFileId, new FileReadCallback() {
            @Override
            public void onSuccess(String content, Long lastModifiedDate) {
                try {
                    exam = new Exam(content);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // *Showing the description of the exam:
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

                    setProgressMessage("Loading grade");
                    driveIOHandler.readFile(gradeFileId, new FileReadCallback() {
                        @Override
                        public void onSuccess(String content, Long lastModifiedDate) {

                            setProgressMessage("Loading answers");
                            driveIOHandler.readFile(gradeFileId, new FileReadCallback() {
                                @Override
                                public void onSuccess(String content, Long lastModifiedDate) {
                                    stopProgressFragment();
                                    // TODO mostrar respostas do aluno caso ele ja respondeu
                                    // TODO e não permitir que ele responda o exam

                                    allFilesLoaded = true;
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    //TODO error
                                    stopProgressFragment();
                                }
                            });


                            try {
                                final Grade grade = new Grade(content);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // *Showing the grade of the student:
                                        TextView gradeOut = (TextView) findViewById(R.id.gradeOut);
                                        double gradeGrade = grade.getGrade();

                                        if(gradeGrade<0){
                                            gradeOut.setText(getResources().getString(R.string.output_grade_notSet_text));
                                        } else{
                                            gradeOut.setText(Double.toString(gradeGrade));
                                        }

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            //TODO error
                            stopProgressFragment();
                        }
                    });

                } catch (JSONException e){
                    e.printStackTrace();
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                //TODO error
                stopProgressFragment();
            }
        });
    }


    // *Action of the "Start" button:
    private void examAnsweringStartActionButton_onClick(){
        if(allFilesLoaded) {
            Intent questionAnsweringIntent = new Intent(this, QuestionAnsweringActivity.class);
            questionAnsweringIntent.putExtra("questionIndex", 0);
            questionAnsweringIntent.putExtra("exam", exam.toString());
            questionAnsweringIntent.putExtra("studentAnswerFileId", studentAnswerFileId);
            startActivity(questionAnsweringIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_answering, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.examAnsweringStartActionButton:
                examAnsweringStartActionButton_onClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
