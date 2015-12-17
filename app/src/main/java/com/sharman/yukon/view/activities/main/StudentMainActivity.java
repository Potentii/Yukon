package com.sharman.yukon.view.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.view.activities.answering.ExamAnsweringActivity;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;
import com.sharman.yukon.view.activities.util.recycler.OnExamRVItemClickListener;

import org.json.JSONException;

import java.util.Vector;

public class StudentMainActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        examsQueryString = "mimeType = '" + EMimeType.STUDENT_CONFIG.getMimeType() + "' and sharedWithMe";

        toolbar = (Toolbar) findViewById(R.id.actionToolbar);
        mainNavigationDrawerFragment = (MainNavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer);

        examRVInfoVector = new Vector<>();

        examRVAdapter = new ExamRVAdapter(this, getCredential(), examRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(ExamRVInfo examRVInfo) {
                Intent examAnsweringIntent = new Intent(getApplicationContext(), ExamAnsweringActivity.class);
                examAnsweringIntent.putExtra("studentConfigs", examRVInfo.getConfigFileId());
                startActivity(examAnsweringIntent);
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
            StudentConfigs studentConfigs = new StudentConfigs(content);
            readExam(studentConfigs.getExamFileId(), index);
        } catch (JSONException e){
            e.printStackTrace();
            stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
        }
    }
}