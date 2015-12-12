package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.api.services.drive.model.File;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.view.activities.answering.ExamAnsweringActivity;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;
import com.sharman.yukon.view.activities.util.recycler.OnExamRVItemClickListener;

import org.json.JSONException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class StudentMainActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        toolbar = (Toolbar) findViewById(R.id.actionToolbar);
        mainNavigationDrawerFragment = (MainNavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer);

        examRVInfoVector = new Vector<>();

        examRVAdapter = new ExamRVAdapter(this, getCredential(), examRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(ExamRVInfo examRVInfo) {
                Intent examAnsweringIntent = new Intent(getApplicationContext(), ExamAnsweringActivity.class);
                examAnsweringIntent.putExtra("studentConfigs", examRVInfo.getConfigs());
                startActivity(examAnsweringIntent);
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
        driveIOHandler.queryFiles("mimeType = '" + EMimeType.STUDENT_CONFIG.getMimeType() + "' and sharedWithMe", new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {

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
                                StudentConfigs studentConfigs = new StudentConfigs(content);

                                examRVInfoVector.add(new ExamRVInfo(
                                        studentConfigs.getTeacherIdCache(),
                                        studentConfigs.getExamTitleCache(),
                                        studentConfigs.getExamSubjectCache(),
                                        studentConfigs.getExamDeliveryDateCache(),
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
}
