package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.api.services.drive.model.File;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.creation.ExamCreateActivity;
import com.sharman.yukon.view.activities.dialog.AlertDialog;
import com.sharman.yukon.view.activities.managing.ExamManagingActivity;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;
import com.sharman.yukon.view.activities.util.recycler.OnExamRVItemClickListener;

import org.json.JSONException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class TeacherMainActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        toolbar = (Toolbar) findViewById(R.id.actionToolbar);
        mainNavigationDrawerFragment = (MainNavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer);

        examRVInfoVector = new Vector<>();

        examRVAdapter = new ExamRVAdapter(this, getCredential(), examRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(ExamRVInfo examRVInfo) {
                Intent examManagingIntent = new Intent(getApplicationContext(), ExamManagingActivity.class);
                examManagingIntent.putExtra("teacherConfigs", examRVInfo.getConfigs());
                startActivity(examManagingIntent);
            }
        });

        examRecyclerView = (RecyclerView) findViewById(R.id.examRecyclerView);
        examRecyclerView.setAdapter(examRVAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        examRecyclerView.setLayoutManager(linearLayoutManager);
    }



    @Override
    protected void updateExamList() {
        examRVInfoVector.clear();

        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.queryFiles("mimeType = '" + EMimeType.TEACHER_CONFIG.getMimeType() + "'", new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {

                // TODO fazer tratamento para exceptions de servidor e de internet como essa, de um jeito mais automatizado
                if(driveFileList == null){
                    AlertDialog alertDialog = new AlertDialog();
                    alertDialog.setTitleTxt("Internet error");
                    alertDialog.setContentTxt("Do you want to try again?");
                    alertDialog.setPositiveBtnTxt("Try again");
                    alertDialog.setNegativeBtnTxt("No");
                    alertDialog.setDialogCallback(new DialogCallback() {
                        @Override
                        public void onPositive() {
                            Intent i = new Intent(getApplicationContext(), TeacherMainActivity.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onNegative() {

                        }

                        @Override
                        public void onNeutral() {

                        }
                    });
                    alertDialog.show(getFragmentManager(), "alert_dialog_internet_error");
                    return;
                }

                Set<String> loadingExamsSet = new HashSet<String>();
                for (int i = 0; i < driveFileList.size(); i++) {
                    loadingExamsSet.add(String.valueOf(i));
                }

                final StepByStepEvent stepByStepEvent_loadingExams = new StepByStepEvent(loadingExamsSet)
                        .setFinishStepCallback(finishStepByStepEventCallback)
                        .setRegisterStepCallback(registerStepByStepEventCallback);


                for (int i = 0; i < driveFileList.size(); i++) {
                    final int index = i;
                    driveIOHandler.readFile(driveFileList.get(i), new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                TeacherConfigs teacherConfigs = new TeacherConfigs(content);

                                examRVInfoVector.add(new ExamRVInfo(
                                        teacherConfigs.getTeacherIdCache(),
                                        teacherConfigs.getExamTitleCache(),
                                        teacherConfigs.getExamSubjectCache(),
                                        teacherConfigs.getExamDeliveryDateCache(),
                                        content));
                                stepByStepEvent_loadingExams.registerStep(String.valueOf(index), true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
                        }
                    });
                }
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listeners methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void addExamBtn_onClick(View view) {
        Intent addExamIntent = new Intent(this, ExamCreateActivity.class);
        startActivity(addExamIntent);
    }
}
