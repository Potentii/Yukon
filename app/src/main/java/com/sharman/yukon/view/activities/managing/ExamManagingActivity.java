package com.sharman.yukon.view.activities.managing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.main.TeacherMainActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.DriveContentResourceCache;
import com.sharman.yukon.view.activities.util.FinishStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.StudentContact;
import com.sharman.yukon.view.activities.util.recycler.StudentRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.StudentRVInfo;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamManagingActivity extends GoogleRestConnectActivity {
    public final static String TEACHER_CONFIGS_FILE_ID_INTENT_KEY = "teacherConfigsFileId";


    private String teacherConfigsFileId;
    private String teacherAnswersFileId;
    private String[] studentConfigFileIdArray;


    private Exam exam;
    private TeacherConfigs teacherConfigs;
    private StudentConfigs[] studentConfigsArray;
    private Grade[] gradeArray;


    private View infoPhotoHeader;
    private TextView descriptionOut;


    private RecyclerView studentRecyclerView;
    private StudentRVAdapter studentRVAdapter;
    private List<StudentRVInfo> studentRVInfoList;
    private List<StudentContact> studentContactList;


    // TODO update the xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing);


        // *Getting Views from XML:
        infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
        descriptionOut = (TextView) findViewById(R.id.descriptionOut);
        studentRecyclerView = (RecyclerView) findViewById(R.id.studentRecyclerView);


        // *Setting the Student Adapter:
        studentRVInfoList = new ArrayList<>();
        studentRVAdapter = new StudentRVAdapter(this, studentRVInfoList) {
            @Override
            protected void onItemClick(StudentRVInfo studentRVInfo) {
                inspectStudent(studentRVInfo);
            }
        };


        // *Setting the Student RecyclerView:
        studentRecyclerView.setAdapter(studentRVAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        studentRecyclerView.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        loadContent();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Load methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void loadContent(){
        exam = null;
        studentConfigsArray = null;
        gradeArray = null;
        studentContactList = null;

        startProgressFragment();
        setProgressMessage(R.string.progress_loadingData);


        // *Getting contacts list:
        studentContactList = new AndroidUtil(this).queryContacts();


        // *Getting intent data:
        try {
            teacherConfigsFileId = getIntent().getExtras().getString(TEACHER_CONFIGS_FILE_ID_INTENT_KEY);
        } catch (Exception e){
            onLoadFailure();
            return;
        }


        new DriveContentResourceCache(this, getCredential()){
            @Override
            public void onSuccess(String validatedResource) {
                try {
                    teacherConfigs = new TeacherConfigs(validatedResource);
                    teacherAnswersFileId = teacherConfigs.getCorrectAnswersFileId();
                    studentConfigFileIdArray = teacherConfigs.getStudentConfigsFileIdArray();
                    studentConfigsArray = new StudentConfigs[studentConfigFileIdArray.length];
                    gradeArray = new Grade[studentConfigFileIdArray.length];


                    // *Setting up loading event:
                    Set<String> stepByStepEventSet = new HashSet<String>();
                    stepByStepEventSet.add("exam");
                    for (int i = 0; i < studentConfigFileIdArray.length; i++) {
                        stepByStepEventSet.add("studentConfigs" + i);
                    }
                    final StepByStepEvent stepByStepEvent = new StepByStepEvent(stepByStepEventSet).setFinishStepCallback(new FinishStepByStepEventCallback() {
                        @Override
                        public void onSuccess() {
                            onLoadSuccess();
                        }

                        @Override
                        public void onFailure(Set<String> failedSteps) {
                            onLoadFailure();
                        }
                    });



                    // *Loading exam file:
                    new DriveContentResourceCache(getActivity(), getCredential()) {
                        @Override
                        public void onSuccess(String validatedResource) {
                            try {
                                exam = new Exam(validatedResource);
                                stepByStepEvent.registerStep("exam", true);
                            } catch (JSONException e){
                                onFailure(e);
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            // ERROR on loading exam file
                            stepByStepEvent.registerStep("exam", false);
                        }
                    }.getResource(teacherConfigs.getExamFileId());



                    // *Loading each studentConfigs file:
                    for (int i = 0; i < studentConfigFileIdArray.length; i++) {
                        final int index = i;
                        new DriveContentResourceCache(getActivity(), getCredential()) {
                            @Override
                            public void onSuccess(String validatedResource) {
                                try {
                                    studentConfigsArray[index] = new StudentConfigs(validatedResource);


                                    // *Loading grade file:
                                    new DriveContentResourceCache(getActivity(), getCredential()) {
                                        @Override
                                        public void onSuccess(String validatedResource) {
                                            try {
                                                gradeArray[index] = new Grade(validatedResource);
                                                stepByStepEvent.registerStep("studentConfigs" + index, true);
                                            } catch (JSONException | ArrayIndexOutOfBoundsException e){
                                                onFailure(e);
                                            }
                                        }
                                        @Override
                                        public void onFailure(Exception e) {
                                            // ERROR on loading grade file
                                            stepByStepEvent.registerStep("studentConfigs" + index, false);
                                        }
                                    }.getResource(studentConfigsArray[index].getGradeFileId());


                                } catch (JSONException | ArrayIndexOutOfBoundsException e){
                                    onFailure(e);
                                }
                            }
                            @Override
                            public void onFailure(Exception e) {
                                // ERROR on loading each studentConfigs file
                                stepByStepEvent.registerStep("studentConfigs" + index, false);
                            }
                        }.getResource(studentConfigFileIdArray[i]);
                    }



                } catch (JSONException | NullPointerException e){
                    onFailure(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
                // ERROR on loading configs file
                onLoadFailure();
            }
        }.getResource(teacherConfigsFileId);
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Load callback methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void onLoadFailure(){
        stopProgressFragment();
        // ERROR loading event error
        // *Showing "try again" dialog:
        new AndroidUtil(this).showAlertDialog(null, R.string.alertDialog_title_loadingFailed, R.string.alertDialog_content_tryAgain, R.string.dialog_yes, null, R.string.dialog_no, new DialogCallback() {
            @Override
            public void onPositive() {
                loadContent();
            }

            @Override
            public void onNegative() {
                backToMainActivity();
            }

            @Override
            public void onNeutral() {
            }
        });
    }


    private void onLoadSuccess() {
        if (exam == null || studentConfigsArray == null || gradeArray == null || studentContactList == null || gradeArray.length != studentConfigsArray.length) {
            onLoadFailure();
        }
        stopProgressFragment();
        final Activity activity = this;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // *Building the toolbar:
                new AndroidUtil(activity).fillInfoPhotoToolbar_GPlusImage(
                        infoPhotoHeader,
                        getCredential(),
                        exam.getTeacherId(),
                        exam.getTitle(),
                        exam.getSubject(),
                        new SimpleDateFormat("dd/MM/yyyy").format(exam.getDeliverDate())
                );


                // *Displaying the exam's description:
                descriptionOut.setText(exam.getDescription());


                // *Filling the StudentRVInfo list:
                studentRVInfoList.clear();
                for (int i = 0; i < studentConfigsArray.length; i++) {
                    StudentConfigs studentConfigs = studentConfigsArray[i];
                    String studentEmail = studentConfigs.getStudent();


                    // *Searching for this student on contacts list:
                    StudentContact studentContactFound = null;
                    for (StudentContact studentContact : studentContactList) {
                        if (studentEmail.equals(studentContact.getId())) {
                            studentContactFound = studentContact;
                            break;
                        }
                    }


                    // *If not found:
                    if (studentContactFound == null) {
                        studentContactFound = new StudentContact(studentEmail, studentEmail, "");
                    }


                    // *Add the info to the list:
                    studentRVInfoList.add(new StudentRVInfo(
                            studentConfigs.getGradeFileId(),
                            studentConfigs.getAnswersFileId(),
                            gradeArray[i],
                            studentContactFound));
                }


                // *Updating the Recycler:
                studentRVAdapter.update();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void inspectStudent(final StudentRVInfo studentRVInfo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO
                /*
                Intent inspectStudentIntent = new Intent(getApplicationContext(), ExamManagingStudentInspectActivity.class)
                        .putExtra(ExamManagingStudentInspectActivity.REQUEST_CODE_INTENT_KEY, ExamManagingStudentInspectActivity.ANSWER_QUESTION_REQUEST)
                        .putExtra(ExamManagingStudentInspectActivity.EXAM_TITLE_INTENT_KEY, exam.getTitle())
                        .putExtra(ExamManagingStudentInspectActivity.QUESTION_INDEX_INTENT_KEY, questionAnsweringRVInfo.getIndex())
                        .putExtra(ExamManagingStudentInspectActivity.QUESTION_INTENT_KEY, questionAnsweringRVInfo.getQuestion().toString());

                startActivityForResult(inspectStudentIntent, ExamManagingStudentInspectActivity.ANSWER_QUESTION_REQUEST);
                */

            }
        });
    }


    private void backToMainActivity(){
        Intent intent = new Intent(this, TeacherMainActivity.class);
        startActivity(intent);
        finish();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Activity result:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ExamManagingStudentInspectActivity.INSPECT_REQUEST:
                // TODO teste se realmente é uma RESULT_CANCELED que é gerada quando o usuário aperta para voltar
                if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED){
                    try {


                        // TODO update the recycler


                    } catch (Exception e) {
                        e.printStackTrace();
                        new AndroidUtil(this).showToast(R.string.toast_somethingWentWrong, Toast.LENGTH_SHORT);
                    }
                } else{
                    new AndroidUtil(this).showToast(R.string.toast_somethingWentWrong, Toast.LENGTH_SHORT);
                }
                break;
        }
    }
}
