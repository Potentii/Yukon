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
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DriveContentResourceCache;
import com.sharman.yukon.view.activities.util.FinishStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.StepByStepEvent;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class ExamAnsweringActivity extends GoogleRestConnectActivity {
    private String studentConfigsFileId;

    private Exam exam;
    private StudentConfigs studentConfigs;
    private Grade grade;
    private StudentAnswers studentAnswers;

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

        startProgressFragment();
        setProgressMessage("Loading exam");
        try {
            studentConfigsFileId = getIntent().getExtras().getString("studentConfigsFileId");

            loadFiles();

        } catch (NullPointerException e){
            onLoadFailure();
        }
    }


    private void loadFiles(){
        new DriveContentResourceCache(this, getCredential()){
            @Override
            public void onSuccess(String validatedResource) {
                try {
                    studentConfigs = new StudentConfigs(validatedResource);




                    // *Setting up loading event:
                    Set<String> stepByStepEventSet = new HashSet<String>();
                    stepByStepEventSet.add("exam");
                    stepByStepEventSet.add("grade");
                    stepByStepEventSet.add("studentAnswers");
                    final StepByStepEvent stepByStepEvent = new StepByStepEvent(stepByStepEventSet).setFinishStepCallback(new FinishStepByStepEventCallback() {
                        @Override
                        public void onSuccess() {
                            onLoadSuccess();
                        }

                        @Override
                        public void onFailure(Set<String> failedSteps) {
                            onLoadFailure();
                        }
                    });




                    // *Loading exam file:
                    new DriveContentResourceCache(getActivity(), getCredential()) {
                        @Override
                        public void onSuccess(String validatedResource) {
                            try {
                                exam = new Exam(validatedResource);
                                stepByStepEvent.registerStep("exam", true);
                            } catch (JSONException e){
                                onFailure(e);
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            // *Error on load exam file
                            stepByStepEvent.registerStep("exam", false);
                        }
                    }.getResource(studentConfigs.getExamFileId());




                    // *Loading studentAnswers file:
                    new DriveContentResourceCache(getActivity(), getCredential()) {
                        @Override
                        public void onSuccess(String validatedResource) {
                            try {
                                studentAnswers = new StudentAnswers(validatedResource);
                                stepByStepEvent.registerStep("studentAnswers", true);
                            } catch (JSONException e){
                                onFailure(e);
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            // *Error on load grade file
                            stepByStepEvent.registerStep("studentAnswers", false);
                        }
                    }.getResource(studentConfigs.getAnswersFileId());




                    // *Loading grade file:
                    new DriveContentResourceCache(getActivity(), getCredential()) {
                        @Override
                        public void onSuccess(String validatedResource) {
                            try {
                                grade = new Grade(validatedResource);
                                stepByStepEvent.registerStep("grade", true);
                            } catch (JSONException e){
                                onFailure(e);
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            // *Error on load grade file
                            stepByStepEvent.registerStep("grade", false);
                        }
                    }.getResource(studentConfigs.getGradeFileId());




                } catch (JSONException e){
                    onFailure(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
                // *Error on load configs file
                onLoadFailure();
            }
        }.getResource(studentConfigsFileId);
    }




    private void onLoadFailure(){
        stopProgressFragment();
        // TODO error
    }

    private void onLoadSuccess(){
        if(exam == null || grade == null || studentAnswers == null){
            onLoadFailure();
        }
        stopProgressFragment();
        // TODO success
    }




    /*
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
    */


    // *Action of the "Start" button:
    /*
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
    */
}
