package com.sharman.yukon.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.api.services.drive.model.File;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.callback.LastModifiedDateCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.YukonAccountKeeper;
import com.sharman.yukon.view.activities.creation.ExamCreateActivity;
import com.sharman.yukon.view.activities.dialog.AlertDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.FinishStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.GetResourceCacheCallback;
import com.sharman.yukon.view.activities.util.RegisterResourceCacheCallback;
import com.sharman.yukon.view.activities.util.RegisterStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.ResourceCache;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.ValidateResourceCacheCallback;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;

import org.json.JSONException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public abstract class MainActivity extends GoogleRestConnectActivity {
    protected ExamRVAdapter examRVAdapter;
    protected RecyclerView examRecyclerView;
    protected Vector<ExamRVInfo> examRVInfoVector;

    protected StepByStepEvent stepByStepEvent_loadingExams;

    protected MainNavigationDrawerFragment mainNavigationDrawerFragment;
    protected Toolbar toolbar;

    private Activity activity;

    protected String examsQueryString;

    protected FinishStepByStepEventCallback finishStepByStepEventCallback = new FinishStepByStepEventCallback() {
        @Override
        public void onSuccess() {
            stopProgressFragment();
        }

        @Override
        public void onFailure(Set<String> failedSteps) {
            try {
                new AndroidUtil(activity).showToast(R.string.toast_examsNotLoaded, Toast.LENGTH_SHORT);
            } catch (Exception e){
                e.printStackTrace();
            }
            onSuccess();
        }
    };

    protected RegisterStepByStepEventCallback registerStepByStepEventCallback = new RegisterStepByStepEventCallback() {
        @Override
        public void onRegisterSuccess(String succeededStep) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    examRecyclerView.getAdapter().notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onRegisterFailure(String failedStep) { }
    };



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        activity = this;

        try {
            updateMainAccountInfo();

        } catch (NullPointerException e){
            e.printStackTrace();
        }

        startProgressFragment();
        setProgressMessage(R.string.progress_main_loadingExams);
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



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Exam load methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Retrieve from cache all the config files of exams:
    private void updateExamList() {
        final Activity activity = this;
        examRVInfoVector.clear();

        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.queryFiles(examsQueryString, new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {
                final AndroidUtil androidUtil = new AndroidUtil(activity);


                // *Setting configId array and examSet indexes:
                String[] configIdArray = new String[driveFileList.size()];
                Set<String> loadingExamsSet = new HashSet<String>();
                for (int i = 0; i < driveFileList.size(); i++) {
                    configIdArray[i] = driveFileList.get(i).getId();
                    loadingExamsSet.add(String.valueOf(i));
                }


                // *Creating the StepByStep object based on the examSet indexes:
                stepByStepEvent_loadingExams = new StepByStepEvent(loadingExamsSet)
                        .setFinishStepCallback(finishStepByStepEventCallback)
                        .setRegisterStepCallback(registerStepByStepEventCallback);


                // *Trying to get the exam file ids from cache that correspond with the config id:
                for (int i = 0; i < configIdArray.length; i++) {
                    final int index = i;
                    final String configId = configIdArray[i];


                    // *Getting all the configs files from cache
                    new ResourceCache(activity).getResource_DriveFileContent(configId, new GetResourceCacheCallback<String, Long>() {
                        @Override
                        public void onFound(String cachedResource) {
                            // *Do nothing (not validated yet)
                        }

                        @Override
                        public void onNotFound(final RegisterResourceCacheCallback<String, Long> registerResourceCacheCallback) {
                            new DriveIOHandler(getCredential()).readFile(configId, new FileReadCallback() {
                                @Override
                                public void onSuccess(String content, Long lastModifiedDate) {
                                    registerResourceCacheCallback.register(content, lastModifiedDate);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    // *Error on loading this file
                                    e.printStackTrace();
                                    stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
                                }
                            });
                        }

                        @Override
                        public void onValidationRequested(final ValidateResourceCacheCallback<Long> validateResourceCacheCallback) {
                            new DriveIOHandler(getCredential()).getLastModifiedDate(configId, new LastModifiedDateCallback() {
                                @Override
                                public void onSuccess(Long lastModifiedDate) {
                                    validateResourceCacheCallback.validate(lastModifiedDate);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    e.printStackTrace();
                                    stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
                                }
                            });
                        }

                        @Override
                        public void onValidatedCache(String validatedResource) {
                            // *Cache validated and ready to use:

                            onConfigFileRead(validatedResource, index);

                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                stopProgressFragment();

                // *Showing a dialog asking to try again:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog();
                        alertDialog.setTitleTxt("Exams couldn't be loaded");
                        alertDialog.setContentTxt("Do you want to try again?");
                        alertDialog.setPositiveBtnTxt("Yes");
                        alertDialog.setNegativeBtnTxt("No");
                        alertDialog.setDialogCallback(new DialogCallback() {
                            @Override
                            public void onPositive() {
                                startProgressFragment();
                                setProgressMessage(R.string.progress_main_loadingExams);
                                updateExamList();
                            }

                            @Override
                            public void onNegative() {
                            }

                            @Override
                            public void onNeutral() {
                            }
                        });

                        alertDialog.show(getFragmentManager(), "alert_dialog_cant_load_exams");
                    }
                });
            }
        });
    }


    // *Retrieve from cache all the exam files:
    protected void readExam(final String examFileId, final int index){
        new ResourceCache(this).getResource_DriveFileContent(examFileId, new GetResourceCacheCallback<String, Long>() {
            @Override
            public void onFound(String resource) {

            }

            @Override
            public void onNotFound(final RegisterResourceCacheCallback<String, Long> registerResourceCacheCallback) {
                new DriveIOHandler(getCredential()).readFile(examFileId, new FileReadCallback() {
                    @Override
                    public void onSuccess(String content, Long lastModifiedDate) {
                        registerResourceCacheCallback.register(content, lastModifiedDate);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
                    }
                });
            }


            @Override
            public void onValidationRequested(final ValidateResourceCacheCallback<Long> validateResourceCacheCallback) {
                new DriveIOHandler(getCredential()).getLastModifiedDate(examFileId, new LastModifiedDateCallback() {
                    @Override
                    public void onSuccess(Long lastModifiedDate) {
                        validateResourceCacheCallback.validate(lastModifiedDate);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
                    }
                });
            }


            @Override
            public void onValidatedCache(String validatedResource) {
                try {
                    Exam exam = new Exam(validatedResource);

                    examRVInfoVector.add(new ExamRVInfo(
                            exam.getTeacherId(),
                            exam.getTitle(),
                            exam.getSubject(),
                            exam.getDeliverDate(),
                            validatedResource));

                    stepByStepEvent_loadingExams.registerStep(String.valueOf(index), true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    stepByStepEvent_loadingExams.registerStep(String.valueOf(index), false);
                }
            }
        });
    }


    protected abstract void onConfigFileRead(String content, int index);



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
