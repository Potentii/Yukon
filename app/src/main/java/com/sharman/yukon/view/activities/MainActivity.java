package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
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
            public void onClick(ExamRVInfo examRVInfo) {
                //TODO mudar para o shared:
                /*
                Intent examAnsweringIntent = new Intent(getApplicationContext(), ExamAnsweringActivity.class);
                examAnsweringIntent.putExtra("exam", exam.toString());
                startActivity(examAnsweringIntent);
                finish();
                */
            }
        });

        sharedExamRVAdapter = new ExamRVAdapter(this, getCredential(), sharedExamRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(ExamRVInfo examRVInfo) {
                // TODO open Exam
                Intent examAnsweringIntent = new Intent(getApplicationContext(), ExamAnsweringActivity.class);
                examAnsweringIntent.putExtra("studentAnswerFileId", examRVInfo.getStudentAnswerFileId());
                examAnsweringIntent.putExtra("examFileId", examRVInfo.getExamFileId());
                examAnsweringIntent.putExtra("gradeFileId", examRVInfo.getGradeFileId());

                examAnsweringIntent.putExtra("examTitleCache", examRVInfo.getExamTitle());
                startActivity(examAnsweringIntent);
                //finish();
            }
        });

        myExamRecyclerView = (RecyclerView) findViewById(R.id.myExamRecyclerView);
        myExamRecyclerView.setAdapter(myExamRVAdapter);
        myExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedExamRecyclerView = (RecyclerView) findViewById(R.id.sharedExamRecyclerView);
        sharedExamRecyclerView.setAdapter(sharedExamRVAdapter);
        sharedExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        updateMyExamList();
        updateSharedExamList();
    }


    public void addExamBtn_onClick(View view) {
        Intent addExamIntent = new Intent(this, ExamCreateActivity.class);
        startActivity(addExamIntent);
        //finish();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Update List:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void updateMyExamList(){
        myExamLoaded = 0;
        onMyUpdateCalled = false;
        myExamRVInfoVector.clear();

        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.queryFiles("mimeType = '" + EMimeType.TEACHER_CONFIG.getMimeType() + "'", new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {

                for (int i = 0; i < driveFileList.size(); i++) {

                    driveIOHandler.readFile(driveFileList.get(i), new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                TeacherConfigs teacherConfigs = new TeacherConfigs(content);

                                myExamRVInfoVector.add(new ExamRVInfo(
                                        teacherConfigs.getTeacherIdCache(),
                                        teacherConfigs.getExamTitleCache(),
                                        teacherConfigs.getExamSubjectCache(),
                                        teacherConfigs.getExamDeliveryDateCache(),
                                        teacherConfigs.getExamFileId(),
                                        teacherConfigs.getCorrectAnswersFileId()));

                                myExamLoaded++;
                                if (myExamLoaded == driveFileList.size()) {
                                    onMyExamUpdateSuccess();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
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

                for (int i = 0; i < driveFileList.size(); i++) {
                    driveIOHandler.readFile(driveFileList.get(i), new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                StudentConfigs studentConfigs = new StudentConfigs(content);

                                sharedExamRVInfoVector.add(new ExamRVInfo(
                                        studentConfigs.getTeacherIdCache(),
                                        studentConfigs.getExamTitleCache(),
                                        studentConfigs.getExamSubjectCache(),
                                        studentConfigs.getExamDeliveryDateCache(),
                                        studentConfigs.getExamFileId(),
                                        studentConfigs.getAnswersFileId(),
                                        studentConfigs.getGradeFileId()));

                                sharedExamLoaded++;
                                if (sharedExamLoaded == driveFileList.size()) {
                                    onSharedExamUpdateSuccess();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
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
