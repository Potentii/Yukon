package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.api.services.drive.model.File;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.model.YukonAccount;
import com.sharman.yukon.model.YukonAccountKeeper;
import com.sharman.yukon.view.activities.answering.ExamAnsweringActivity;
import com.sharman.yukon.view.activities.creation.ExamCreateActivity;
import com.sharman.yukon.view.activities.managing.ExamManagingActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;
import com.sharman.yukon.view.activities.util.recycler.OnExamRVItemClickListener;
import org.json.JSONException;

import java.util.List;
import java.util.Vector;


public abstract class MainActivity extends GoogleRestConnectActivity {
    protected ExamRVAdapter examRVAdapter;

    protected RecyclerView examRecyclerView;

    protected Vector<ExamRVInfo> examRVInfoVector;

    protected int examLoaded;
    protected boolean onUpdateCalled;

    protected MainNavigationDrawerFragment mainNavigationDrawerFragment;
    protected Toolbar toolbar;



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            updateMainAccountInfo();

        } catch (NullPointerException e){
            e.printStackTrace();
        }

        updateExamList();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Main account info callback methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    protected void onMainAccountInfoUpdateSuccess(YukonAccountKeeper yukonAccountKeeper){
        super.onMainAccountInfoUpdateSuccess(yukonAccountKeeper);
        try {
            mainNavigationDrawerFragment.setUp(this, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, yukonAccountKeeper, getCredential());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onMainAccountInfoUpdateFail(YukonAccountKeeper yukonAccountKeeper){
        super.onMainAccountInfoUpdateFail(yukonAccountKeeper);
        try {
            mainNavigationDrawerFragment.setUp(this, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, yukonAccountKeeper, getCredential());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }



    protected abstract void updateExamList();



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Update callback methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected void onExamUpdateSuccess(){
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!onUpdateCalled) {
                    onUpdateCalled = true;
                    examRecyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
    }
    protected void onExamUpdateFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!onUpdateCalled) {
                    onUpdateCalled = true;
                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
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

    public void asStudentBtn_onClick(View view){
        if(!(this instanceof StudentMainActivity)){
            new AndroidUtil(this).writeToSharedPreferences(CONFIG_FILE, CONFIG_FAV_ROLE_KEY, CONFIG_FAV_ROLE_STUDENT);
            Intent asStudentIntent = new Intent(this, StudentMainActivity.class);
            startActivity(asStudentIntent);
        }
    }

    public void asTeacherBtn_onClick(View view){
        if(!(this instanceof TeacherMainActivity)){
            new AndroidUtil(this).writeToSharedPreferences(CONFIG_FILE, CONFIG_FAV_ROLE_KEY, CONFIG_FAV_ROLE_TEACHER);
            Intent asTeacherIntent = new Intent(this, TeacherMainActivity.class);
            startActivity(asTeacherIntent);
        }
    }

    public void settings_onClick(View view){
    }

    public void helpAndFeedback_onClick(View view){
    }

    public void addAccountBtn_onClick(View view){
        addAccount();
    }

}
