package com.sharman.yukon.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.api.services.drive.model.File;
import com.sharman.yukon.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.view.activities.util.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.ExamRVInfo;
import com.sharman.yukon.view.activities.util.OnExamRVItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends GoogleRestConnectActivity {
    private RecyclerView sharedExamRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        // TODO test:
        new DriveIOHandler(getCredential()).queryFiles("mimeType = '" + EMimeType.STUDENT_CONFIG.getMimeType() + "' and sharedWithMe", new FileQueryCallback() {
            @Override
            public void onResult(List<File> driveFileList) {
                System.out.println("RESULT SIZE: " + driveFileList.size() + ", Name: " + driveFileList.get(0).getTitle());
            }
        });
        */

        List<ExamRVInfo> examRVInfoList = new ArrayList<>();
        examRVInfoList.add(new ExamRVInfo(R.drawable.common_signin_btn_icon_dark, "titulo vermelho", ""));
        examRVInfoList.add(new ExamRVInfo(R.drawable.common_signin_btn_icon_disabled_dark, "titulo cinza", ""));
        examRVInfoList.add(new ExamRVInfo(R.drawable.common_signin_btn_icon_focus_dark, "titulo borda", ""));
        examRVInfoList.add(new ExamRVInfo(R.drawable.common_signin_btn_text_normal_dark, "titulo longo", ""));

        ExamRVAdapter examRVAdapter = new ExamRVAdapter(this, examRVInfoList, new OnExamRVItemClickListener() {
            @Override
            public void onClick(String fileId) {
                // TODO open Exam
            }
        });
        sharedExamRecyclerView = (RecyclerView) findViewById(R.id.sharedExamRecyclerView);
        sharedExamRecyclerView.setAdapter(examRVAdapter);
        sharedExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void callActivityExam(View view) {
        Intent iCallActivityExam = new Intent(this, ExamCreateActivity.class);
        startActivity(iCallActivityExam);
        finish();
    }
}
