package com.sharman.yukon.view.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.creation.ExamCreateActivity;
import com.sharman.yukon.view.activities.managing.ExamManagingActivity;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;
import com.sharman.yukon.view.activities.util.recycler.OnExamRVItemClickListener;

import org.json.JSONException;

import java.util.Vector;

public class TeacherMainActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        examsQueryString = "mimeType = '" + EMimeType.TEACHER_CONFIG.getMimeType() + "'";

        toolbar = (Toolbar) findViewById(R.id.actionToolbar);
        mainNavigationDrawerFragment = (MainNavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer);

        examRVInfoVector = new Vector<>();

        examRVAdapter = new ExamRVAdapter(this, getCredential(), examRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(ExamRVInfo examRVInfo) {
                Intent examManagingIntent = new Intent(getApplicationContext(), ExamManagingActivity.class);
                examManagingIntent.putExtra("teacherConfigs", examRVInfo.getConfigFileId());
                startActivity(examManagingIntent);
            }
        });

        examRecyclerView = (RecyclerView) findViewById(R.id.examRecyclerView);
        examRecyclerView.setAdapter(examRVAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        examRecyclerView.setLayoutManager(linearLayoutManager);
    }


    public void onConfigFileRead(String content, int index){
        try{
            TeacherConfigs teacherConfigs = new TeacherConfigs(content);
            readExam(teacherConfigs.getExamFileId(), index);
        } catch (JSONException e){
            e.printStackTrace();
            stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
        }
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
