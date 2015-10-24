package com.sharman.yukon.view.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Grade;

import org.json.JSONException;

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


        try {
            // TODO CHANGE: intent should pass the fileId of exam file
            // *Gets the exam from intent:
            studentAnswerFileId = getIntent().getExtras().getString("studentAnswerFileId");
            examFileId = getIntent().getExtras().getString("examFileId");
            gradeFileId = getIntent().getExtras().getString("gradeFileId");

            // TODO get other cached data
            String examTitleCache = getIntent().getExtras().getString("examTitleCache");

            // *Sets the exam's cached title to the actionBar title:
            getSupportActionBar().setTitle(examTitleCache);

            loadFiles();

        } catch (NullPointerException /*| JSONException*/ e){
            // TODO error
            e.printStackTrace();
        }
    }

    private void loadFiles(){
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.readFile(examFileId, new FileReadCallback() {
            @Override
            public void onSuccess(String content) {
                try {
                    exam = new Exam(content);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSupportActionBar().setTitle(exam.getTitle());
                        }
                    });

                    driveIOHandler.readFile(gradeFileId, new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                final Grade grade = new Grade(content);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO show the grade of the student
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }

                            allFilesLoaded = true;
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            //TODO error
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
            finish();
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
