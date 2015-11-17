package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.api.services.drive.model.File;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.model.YukonAccountKeeper;
import com.sharman.yukon.view.activities.answering.ExamAnsweringActivity;
import com.sharman.yukon.view.activities.creation.ExamCreateActivity;
import com.sharman.yukon.view.activities.managing.ExamManagingActivity;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;
import com.sharman.yukon.view.activities.util.recycler.OnExamRVItemClickListener;

import org.json.JSONException;

import java.util.List;
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
        examLoaded = 0;
        onUpdateCalled = false;
        examRVInfoVector.clear();

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

                                examRVInfoVector.add(new ExamRVInfo(
                                        teacherConfigs.getTeacherIdCache(),
                                        teacherConfigs.getExamTitleCache(),
                                        teacherConfigs.getExamSubjectCache(),
                                        teacherConfigs.getExamDeliveryDateCache(),
                                        content));

                                examLoaded++;
                                if (examLoaded == driveFileList.size()) {
                                    onExamUpdateSuccess();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        examRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            // TODO Error Configs file
                            onExamUpdateFailure();
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
