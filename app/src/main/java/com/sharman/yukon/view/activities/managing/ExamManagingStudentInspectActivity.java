package com.sharman.yukon.view.activities.managing;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.drive.model.Permission;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.UserPermissionQueryCallback;
import com.sharman.yukon.io.drive.util.ERole;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.DriveContentResourceCache;
import com.sharman.yukon.view.activities.util.FinishStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.StudentContact;
import com.sharman.yukon.view.activities.util.recycler.QuestionCorrectionRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.QuestionCorrectionRVInfo;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamManagingStudentInspectActivity extends GoogleRestConnectActivity {
    public final static int INSPECT_STUDENT_REQUEST = 44;

    public final static String REQUEST_CODE_INTENT_KEY = "requestCode";
    public final static String STUDENT_ANSWER_FILE_ID_INTENT_KEY = "studentAnswerFileId";
    public final static String TEACHER_ANSWER_FILE_ID_INTENT_KEY = "teacherAnswerFileId";
    public final static String GRADE_FILE_ID_INTENT_KEY = "gradeFileId";
    public final static String STUDENT_NAME_INTENT_KEY = "studentName";
    public final static String STUDENT_EMAIL_INTENT_KEY = "studentEmail";
    public final static String STUDENT_PHOTO_URI_INTENT_KEY = "studentPhotoURI";

    /*
    private LayoutInflater layoutInflater;
    private LinearLayout rowContainer;
    private Button acceptGradeBtn;
    private AnswerCorrectionDialog answerCorrectionDialog;

    private List<View> rowList = new ArrayList<>();
    private Boolean[] correctionArray;
    private boolean gradeSet;
    */


    private String teacherAnswerFileId;
    private String studentAnswerFileId;
    private String gradeFileId;


    private TeacherAnswers teacherAnswers;
    private StudentAnswers studentAnswers;
    private Grade grade;
    private StudentContact studentContact;


    private View infoPhotoHeader;
    private TextView gradeOut;
    private Button acceptGradeBtn;
    private RecyclerView answerRecyclerView;

    private QuestionCorrectionRVAdapter questionCorrectionRVAdapter;
    private List<QuestionCorrectionRVInfo> questionCorrectionRVInfoList;

    private boolean gradeSet;
    private boolean studentSentAnswers;
    private boolean answersCanBeCorrected;
    private boolean studentAnswerArrayIsSynchronized;



    // TODO update the xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing_student_inspect);

        questionCorrectionRVInfoList = new ArrayList<>();
        gradeSet = false;
        studentSentAnswers = false;
        answersCanBeCorrected = false;
        studentAnswerArrayIsSynchronized = false;


        // *Getting Views from XML:
        infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
        gradeOut = (TextView) findViewById(R.id.gradeOut);
        acceptGradeBtn = (Button) findViewById(R.id.acceptGradeBtn);
        answerRecyclerView = (RecyclerView) findViewById(R.id.answerRecyclerView);


        questionCorrectionRVAdapter = new QuestionCorrectionRVAdapter(this, questionCorrectionRVInfoList) {
            @Override
            protected void onItemClick(QuestionCorrectionRVInfo questionCorrectionRVInfo) {
                if(answersCanBeCorrected){
                    openCorrectionDialog(questionCorrectionRVInfo);
                } else{
                    // TODO display a text message
                }
            }

            @Override
            protected void onUpdated() {
                // *When the answers status changed:
                if(isValid() && !gradeSet){
                    acceptGradeBtn.setEnabled(true);
                } else{
                    acceptGradeBtn.setEnabled(false);
                }
            }
        };



        // TODO setup the recyclerView


        acceptGradeBtn.setEnabled(false);
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
        teacherAnswers = null;
        studentAnswers = null;
        grade = null;

        startProgressFragment();
        setProgressMessage(R.string.progress_loadingData);


        // *Getting intent data:
        // TODO filtrar por request code
        try {
            teacherAnswerFileId = getIntent().getExtras().getString(TEACHER_ANSWER_FILE_ID_INTENT_KEY);
            studentAnswerFileId = getIntent().getExtras().getString(STUDENT_ANSWER_FILE_ID_INTENT_KEY);
            gradeFileId = getIntent().getExtras().getString(GRADE_FILE_ID_INTENT_KEY);

            studentContact = new StudentContact(
                    getIntent().getExtras().getString(STUDENT_NAME_INTENT_KEY),
                    getIntent().getExtras().getString(STUDENT_EMAIL_INTENT_KEY),
                    getIntent().getExtras().getString(STUDENT_PHOTO_URI_INTENT_KEY));

        } catch (Exception e){
            onLoadFailure();
            return;
        }



        // *Setting up loading event:
        Set<String> stepByStepEventSet = new HashSet<String>();
        stepByStepEventSet.add("teacherAnswer");
        stepByStepEventSet.add("studentAnswer");
        stepByStepEventSet.add("grade");
        stepByStepEventSet.add("studentAnswers_status");
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



        // *Loading teacherAnswer file:
        new DriveContentResourceCache(getActivity(), getCredential()) {
            @Override
            public void onSuccess(String validatedResource) {
                try {
                    teacherAnswers = new TeacherAnswers(validatedResource);
                    stepByStepEvent.registerStep("teacherAnswer", true);
                } catch (JSONException e){
                    onFailure(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
                // ERROR on loading teacherAnswer file
                stepByStepEvent.registerStep("teacherAnswer", false);
            }
        }.getResource(teacherAnswerFileId);



        // *Loading studentAnswer file:
        new DriveContentResourceCache(getActivity(), getCredential()) {
            @Override
            public void onSuccess(String validatedResource) {
                try {
                    studentAnswers = new StudentAnswers(validatedResource);
                    stepByStepEvent.registerStep("studentAnswer", true);
                } catch (JSONException e){
                    onFailure(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
                // ERROR on loading studentAnswer file
                stepByStepEvent.registerStep("studentAnswer", false);
            }
        }.getResource(studentAnswerFileId);



        // *Loading grade file:
        new DriveContentResourceCache(getActivity(), getCredential()) {
            @Override
            public void onSuccess(String validatedResource) {
                try {
                    grade = new Grade(validatedResource);
                    stepByStepEvent.registerStep("grade", true);
                } catch (JSONException e){
                    onFailure(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
                // ERROR on loading grade file
                stepByStepEvent.registerStep("grade", false);
            }
        }.getResource(gradeFileId);



        // *Loading studentAnswers_status:
        new DriveIOHandler(getCredential()).queryUserPermission(studentAnswerFileId, getCredential().getSelectedAccountName(), new UserPermissionQueryCallback() {
            @Override
            public void onSuccess(Permission[] permissionArray) {
                studentSentAnswers = true;

                for (Permission permission : permissionArray) {
                    ERole eRole = ERole.getERole(permission.getRole());
                    if (eRole == ERole.WRITER) {
                        studentSentAnswers = false;
                        break;
                    }
                }

                stepByStepEvent.registerStep("studentAnswers_status", true);
            }

            @Override
            public void onFailure(Exception e) {
                // ERROR on loading studentAnswers_status
                stepByStepEvent.registerStep("studentAnswers_status", false);
            }
        });
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
                backToPreviousActivity();
            }

            @Override
            public void onNeutral() {
            }
        });
    }






    private void onLoadSuccess() {
        if (teacherAnswers == null || studentAnswers == null || grade == null) {
            onLoadFailure();
        }
        stopProgressFragment();
        final Activity activity = this;


        // *Setting the grade flag:
        gradeSet = (grade.getGrade()>=0);


        // *Verifying if the student answers is synced with the teacher answers:
        WeightTypeAnswerStruct[] teacherAnswerArray = teacherAnswers.getWeightTypeAnswerStructArray();
        Answer[] studentAnswerArray = studentAnswers.getAnswerArray();
        if(teacherAnswerArray.length == studentAnswerArray.length){
            studentAnswerArrayIsSynchronized = true;
            for (WeightTypeAnswerStruct teacherAnswer : teacherAnswerArray) {
                long teacherQID = teacherAnswer.getAnswer().getQID();
                boolean QIDSynced = false;
                for (Answer studentAnswer : studentAnswerArray) {
                    if(teacherQID == studentAnswer.getQID()){
                        QIDSynced = true;
                        break;
                    }
                }
                if(!QIDSynced){
                    studentAnswerArrayIsSynchronized = false;
                    break;
                }
            }
        } else{
            studentAnswerArrayIsSynchronized = false;
        }


        // *If the files isn't synced, do a refactor on the student answer file:
        if(!studentAnswerArrayIsSynchronized){
            // ERROR the student answer file isn't synced
            // *Ask dialog:
            new AndroidUtil(this).showAlertDialog(null, "Student's answer file is corrupted", "Do you want to syncWithTeacherAnswer this file now?", "Recover now", null, "Not now", new DialogCallback() {
                @Override
                public void onPositive() {
                    rebuildCorruptedAnswerFile();
                }

                @Override
                public void onNegative() {
                    backToPreviousActivity();
                }

                @Override
                public void onNeutral() {
                }
            });
            return;
        }


        // *Setting the answer correction flag:
        answersCanBeCorrected = studentSentAnswers && studentAnswerArrayIsSynchronized;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // *Building the toolbar:
                new AndroidUtil(activity).fillInfoPhotoToolbar_AndroidContactsImage(
                        infoPhotoHeader,
                        studentContact.getImageUri(),
                        studentContact.getName(),
                        studentContact.getId(),
                        ""
                );


                // *Displaying the student's grade:
                if (gradeSet) {
                    gradeOut.setText(new DecimalFormat("#.00").format(grade.getGrade()));
                } else {
                    gradeOut.setText(getResources().getString(R.string.output_grade_notSet_text));
                }


                // *Displaying the student's answers:
                WeightTypeAnswerStruct[] teacherAnswerArray = teacherAnswers.getWeightTypeAnswerStructArray();
                Answer[] studentAnswerArray = studentAnswers.getAnswerArray();

                questionCorrectionRVInfoList.clear();
                for (int i = 0; i < teacherAnswerArray.length; i++) {
                    WeightTypeAnswerStruct teacherAnswer = teacherAnswerArray[i];
                    if (teacherAnswer != null && teacherAnswer.getAnswer() != null) {
                        for (Answer studentAnswer : studentAnswerArray) {
                            if (studentAnswer != null) {
                                if (teacherAnswer.getAnswer().getQID() == studentAnswer.getQID()) {
                                    questionCorrectionRVInfoList.add(new QuestionCorrectionRVInfo(i, studentAnswer, teacherAnswer));
                                    break;
                                }
                            }
                        }
                    }
                }


                // *Updating the adapter:
                questionCorrectionRVAdapter.update();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Answer file syncWithTeacherAnswer methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void rebuildCorruptedAnswerFile(){
        final StudentAnswers recoveredStudentAnswers = new StudentAnswers(studentAnswers.getAnswerArray());
        recoveredStudentAnswers.syncWithTeacherAnswer(teacherAnswers);

        new DriveIOHandler(getCredential()).editFile(studentAnswerFileId, null, null, recoveredStudentAnswers.toString(), new FileEditCallback() {
            @Override
            public void onSuccess() {
                onRebuildSuccess();
            }

            @Override
            public void onFailure(String errorMessage) {
                onRebuildFailure();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Answer file syncWithTeacherAnswer callback methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void onRebuildFailure(){
        // ERROR
        // *Showing "try again" dialog
        new AndroidUtil(this).showAlertDialog(null, R.string.alertDialog_title_examManagingStudentInspect_recoveryFailed, R.string.alertDialog_content_tryAgain, R.string.dialog_yes, null, R.string.dialog_no, new DialogCallback() {
            @Override
            public void onPositive() {
                rebuildCorruptedAnswerFile();
            }

            @Override
            public void onNegative() {
                backToPreviousActivity();
            }

            @Override
            public void onNeutral() {
            }
        });
    }


    private void onRebuildSuccess(){
        new AndroidUtil(this).showToast("File recovered", Toast.LENGTH_SHORT);
        loadContent();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void openCorrectionDialog(QuestionCorrectionRVInfo questionCorrectionRVInfo){
        // TODO
        /*
        answerCorrectionDialog.setStudentAnswer(studentAnswer);
        answerCorrectionDialog.setTeacherAnswer(teacherAnswer);
        answerCorrectionDialog.setColor(color);
        answerCorrectionDialog.setIndex(index);
        answerCorrectionDialog.setCorrect(correct);
        answerCorrectionDialog.setDialogCallback(dialogCallback);

        answerCorrectionDialog.show(getFragmentManager(), "answer_correction_dialog");
        */
    }

    private void backToPreviousActivity(){
        // TODO
    }
}
