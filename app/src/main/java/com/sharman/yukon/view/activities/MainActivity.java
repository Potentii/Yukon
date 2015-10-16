package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.api.services.drive.model.File;
import com.sharman.yukon.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.util.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.ExamRVInfo;
import com.sharman.yukon.view.activities.util.OnExamRVItemClickListener;

import org.json.JSONException;

import java.util.List;
import java.util.Vector;


public class MainActivity extends GoogleRestConnectActivity {
    private ExamRVAdapter myExamRVAdapter;
    private ExamRVAdapter sharedExamRVAdapter;

    private RecyclerView myExamRecyclerView;
    private RecyclerView sharedExamRecyclerView;

    private Vector<ExamRVInfo> myExamRVInfoVector;
    private Vector<ExamRVInfo> sharedExamRVInfoVector;

    private int myExamLoaded;
    private boolean onMyUpdateCalled;
    private int sharedExamLoaded;
    private boolean onSharedUpdateCalled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myExamRVInfoVector = new Vector<>();
        sharedExamRVInfoVector = new Vector<>();

        myExamRVAdapter = new ExamRVAdapter(this, getCredential(), myExamRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(Exam exam) {
                // TODO open Exam
                System.out.println(exam.getTitle());
            }
        });

        sharedExamRVAdapter = new ExamRVAdapter(this, getCredential(), sharedExamRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(Exam exam) {
                // TODO open Exam
                System.out.println(exam.getTitle());
            }
        });

        myExamRecyclerView = (RecyclerView) findViewById(R.id.myExamRecyclerView);
        myExamRecyclerView.setAdapter(myExamRVAdapter);
        myExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedExamRecyclerView = (RecyclerView) findViewById(R.id.sharedExamRecyclerView);
        sharedExamRecyclerView.setAdapter(sharedExamRVAdapter);
        sharedExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateMyExamList();
        updateSharedExamList();
    }


    @Override
    protected void onConnect(){}


    public void addExamBtn_onClick(View view) {
        Intent addExamIntent = new Intent(this, ExamCreateActivity.class);
        startActivity(addExamIntent);
        finish();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Update List:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *My:
    private void updateMyExamList(){
        myExamLoaded = 0;
        onMyUpdateCalled = false;
        myExamRVInfoVector.clear();

        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.queryFiles("mimeType = '" + EMimeType.TEACHER_CONFIG.getMimeType() + "'", new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {
                System.out.println("RESULT SIZE: " + driveFileList.size());

                for(int i=0; i<driveFileList.size(); i++) {
                    driveIOHandler.readFile(driveFileList.get(i), new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                TeacherConfigs teacherConfigs = new TeacherConfigs(content);
                                String examFileId = teacherConfigs.getExamFileId();
                                if(!examFileId.equals("")){
                                    driveIOHandler.readFile(examFileId, new FileReadCallback() {
                                        @Override
                                        public void onSuccess(String content) {
                                            try {
                                                Exam exam  =  new Exam(content);
                                                myExamRVInfoVector.add(new ExamRVInfo(exam));
                                                myExamLoaded++;
                                                if(myExamLoaded == driveFileList.size()){
                                                    onMyExamUpdateSuccess();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                onFailure(e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            // TODO Error Exam file
                                            onMyExamUpdateFailure();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // TODO Error Configs file
                            onMyExamUpdateFailure();
                        }
                    });
                }
            }
        });
    }




    // *Shared:
    private void updateSharedExamList() {
        sharedExamLoaded = 0;
        onSharedUpdateCalled = false;
        sharedExamRVInfoVector.clear();

        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.queryFiles("mimeType = '" + EMimeType.STUDENT_CONFIG.getMimeType() + "' and sharedWithMe", new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {
                System.out.println("RESULT SIZE: " + driveFileList.size());

                for (int i = 0; i < driveFileList.size(); i++) {
                    driveIOHandler.readFile(driveFileList.get(i), new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                StudentConfigs studentConfigs = new StudentConfigs(content);
                                String examFileId = studentConfigs.getExamFileId();
                                if (!examFileId.equals("")) {
                                    driveIOHandler.readFile(examFileId, new FileReadCallback() {
                                        @Override
                                        public void onSuccess(String content) {
                                            try {
                                                Exam exam = new Exam(content);
                                                sharedExamRVInfoVector.add(new ExamRVInfo(exam));
                                                sharedExamLoaded++;
                                                if (sharedExamLoaded == driveFileList.size()) {
                                                    onSharedExamUpdateSuccess();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                onFailure(e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            // TODO Error Exam file
                                            onSharedExamUpdateFailure();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // TODO Error Configs file
                            onSharedExamUpdateFailure();
                        }
                    });
                }
            }
        });
    }





    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * OnUpdate:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *My:
    private void onMyExamUpdateSuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onMyUpdateCalled){
                    onMyUpdateCalled = true;
                    System.out.println("SUCCESS");
                    myExamRecyclerView.getAdapter().notifyDataSetChanged();
                    myExamRecyclerView.setAdapter(myExamRVAdapter);
                    myExamRecyclerView.getLayoutParams().height = 138 * myExamRVInfoVector.size();
                }
            }
        });
    }
    private void onMyExamUpdateFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onMyUpdateCalled){
                    onMyUpdateCalled = true;
                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    System.out.println("FAILURE");
                }
            }
        });
    }



    // *Shared:
    private void onSharedExamUpdateSuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onSharedUpdateCalled){
                    onSharedUpdateCalled = true;
                    System.out.println("SUCCESS");
                    sharedExamRecyclerView.getAdapter().notifyDataSetChanged();
                    sharedExamRecyclerView.setAdapter(sharedExamRVAdapter);
                    sharedExamRecyclerView.getLayoutParams().height = 138 * sharedExamRVInfoVector.size();
                }
            }
        });
    }
    private void onSharedExamUpdateFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onSharedUpdateCalled){
                    onSharedUpdateCalled = true;
                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    System.out.println("FAILURE");
                }
            }
        });
    }
}
