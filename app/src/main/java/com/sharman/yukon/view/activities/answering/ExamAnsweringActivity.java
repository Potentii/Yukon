package com.sharman.yukon.view.activities.answering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.drive.model.Permission;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.FileShareEditCallback;
import com.sharman.yukon.io.drive.callback.UserPermissionQueryCallback;
import com.sharman.yukon.io.drive.util.ERole;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.main.StudentMainActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.DriveContentResourceCache;
import com.sharman.yukon.view.activities.util.FinishStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.FormValidator;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.recycler.QuestionAnsweringRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.QuestionAnsweringRVInfo;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamAnsweringActivity extends GoogleRestConnectActivity {
    public final static String STUDENT_CONFIGS_FILE_ID_INTENT_KEY = "studentConfigsFileId";
    public final static String STUDENT_ANSWER_SHARED_KEY_SUFFIX = "ANS";
    public final static String STUDENT_ANSWER_SHARED_FILE = "Answer";

    private String studentConfigsFileId;
    private String studentAnswerFileId;


    private Exam exam;
    private StudentConfigs studentConfigs;
    private Grade grade;
    private StudentAnswers studentAnswers;


    private boolean alreadyAnswered;
    private boolean allContentLoaded;
    private boolean sendBtnBlocked;


    private TextView descriptionOut;
    private View infoPhotoHeader;
    private TextView gradeOut;
    private TextView questionRV_errorOut;


    private FormValidator formValidator;


    private RecyclerView questionRecyclerView;
    private QuestionAnsweringRVAdapter questionAnsweringRVAdapter;
    private List<QuestionAnsweringRVInfo> questionAnsweringRVInfoList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answering);

        final Activity activity = this;
        formValidator = new FormValidator(this);
        allContentLoaded = false;
        sendBtnBlocked = false;


        // *Getting Views from XML:
        infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
        gradeOut = (TextView) findViewById(R.id.gradeOut);
        descriptionOut = (TextView) findViewById(R.id.descriptionOut);
        questionRecyclerView = (RecyclerView) findViewById(R.id.questionRV);
        questionRV_errorOut = (TextView) findViewById(R.id.questionRV_errorOut);


        // *Setting the Question Adapter:
        questionAnsweringRVInfoList = new ArrayList<>();
        questionAnsweringRVAdapter = new QuestionAnsweringRVAdapter(this, questionAnsweringRVInfoList) {
            @Override
            protected void onItemClick(QuestionAnsweringRVInfo questionAnsweringRVInfo) {
                if(!alreadyAnswered) {
                    answerQuestion(questionAnsweringRVInfo);
                } else{
                    new AndroidUtil(activity).showToast(R.string.toast_examAnswering_answerNotAllowed, Toast.LENGTH_SHORT);
                }
            }
        };


        // *Setting the Question RecyclerView:
        questionRecyclerView.setAdapter(questionAnsweringRVAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        questionRecyclerView.setLayoutManager(linearLayoutManager);


        // *Setting the formValidator fields:
        formValidator.addComplexField(questionAnsweringRVAdapter, null, questionRV_errorOut);
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
        startProgressFragment();
        setProgressMessage(R.string.progress_loadingData);


        // *Getting intent data:
        try {
            studentConfigsFileId = getIntent().getExtras().getString(STUDENT_CONFIGS_FILE_ID_INTENT_KEY);
        } catch (Exception e){
            onLoadFailure();
            return;
        }


        // *Getting Drive/cache content:
        new DriveContentResourceCache(this, getCredential()){
            @Override
            public void onSuccess(String validatedResource) {
                try {
                    studentConfigs = new StudentConfigs(validatedResource);
                    studentAnswerFileId = studentConfigs.getAnswersFileId();



                    // *Setting up loading event:
                    Set<String> stepByStepEventSet = new HashSet<String>();
                    stepByStepEventSet.add("exam");
                    stepByStepEventSet.add("grade");
                    stepByStepEventSet.add("studentAnswers");
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
                    }.getResource(studentConfigs.getExamFileId());



                    // *Loading studentAnswers file:
                    new DriveContentResourceCache(getActivity(), getCredential()) {
                        @Override
                        public void onSuccess(String validatedResource) {
                            try {
                                studentAnswers = new StudentAnswers(validatedResource);
                                stepByStepEvent.registerStep("studentAnswers", true);
                            } catch (JSONException e){
                                onFailure(e);
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            // ERROR on loading studentAnswers file
                            stepByStepEvent.registerStep("studentAnswers", false);
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
                    }.getResource(studentConfigs.getGradeFileId());


                    // *Loading studentAnswers_status:
                    new DriveIOHandler(getCredential()).queryUserPermission(studentAnswerFileId, getCredential().getSelectedAccountName(), new UserPermissionQueryCallback() {
                        @Override
                        public void onSuccess(Permission[] permissionArray) {
                            alreadyAnswered = true;

                            for (Permission permission : permissionArray) {
                                ERole eRole = ERole.getERole(permission.getRole());
                                if(eRole == ERole.WRITER){
                                    alreadyAnswered = false;
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


                } catch (JSONException | NullPointerException e){
                    onFailure(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
                // ERROR on loading configs file
                onLoadFailure();
            }
        }.getResource(studentConfigsFileId);
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


    private void onLoadSuccess(){
        if(exam == null || grade == null){
            onLoadFailure();
        }
        stopProgressFragment();
        final Activity activity = this;
        allContentLoaded = true;

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


                // *Showing the grade of the student:
                double gradeDouble = grade.getGrade();
                if (gradeDouble < 0) {
                    gradeOut.setText(getResources().getString(R.string.output_grade_notSet_text));
                } else {
                    gradeOut.setText(new DecimalFormat("#.00").format(gradeDouble));
                }


                // *Displaying the exam's description:
                descriptionOut.setText(exam.getDescription());


                // *Displaying questions and answers:
                Question[] questionArray = exam.getQuestionArray();
                Answer[] answerArray;


                // *Retrieving answers from cache:
                try {
                    if (studentAnswers == null || !alreadyAnswered) {
                        studentAnswers = new StudentAnswers(new AndroidUtil(activity).readFromSharedPreferences(STUDENT_ANSWER_SHARED_FILE, studentConfigsFileId + STUDENT_ANSWER_SHARED_KEY_SUFFIX, null));
                    }
                    answerArray = studentAnswers.getAnswerArray();
                } catch (JSONException | NullPointerException e) {
                    studentAnswers = null;
                    answerArray = null;
                }


                // *Fixing the answers array if needed:
                if (studentAnswers == null || answerArray == null || questionArray.length != answerArray.length) {
                    // ERROR: Answers couldn't be loaded properly
                    new AndroidUtil(activity).showToast(R.string.toast_examAnswering_answerLoadFail, Toast.LENGTH_SHORT);

                    studentAnswers = new StudentAnswers(new Answer[questionArray.length]);
                    answerArray = studentAnswers.getAnswerArray();
                }


                // *Adding questions and answers to the list:
                questionAnsweringRVInfoList.clear();
                for (int i = 0; i < questionArray.length; i++) {
                    questionAnsweringRVInfoList.add(new QuestionAnsweringRVInfo(i, questionArray[i], answerArray[i]));
                }


                // *Updating the Recycler:
                questionAnsweringRVAdapter.update();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void answerQuestion(final QuestionAnsweringRVInfo questionAnsweringRVInfo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent editQuestionIntent = new Intent(getApplicationContext(), QuestionAnsweringActivity.class)
                        .putExtra(QuestionAnsweringActivity.REQUEST_CODE_INTENT_KEY, QuestionAnsweringActivity.ANSWER_QUESTION_REQUEST)
                        .putExtra(QuestionAnsweringActivity.EXAM_TITLE_INTENT_KEY, exam.getTitle())
                        .putExtra(QuestionAnsweringActivity.QUESTION_INDEX_INTENT_KEY, questionAnsweringRVInfo.getIndex())
                        .putExtra(QuestionAnsweringActivity.QUESTION_INTENT_KEY, questionAnsweringRVInfo.getQuestion().toString());
                try {
                    editQuestionIntent.putExtra(QuestionAnsweringActivity.ANSWER_INTENT_KEY, questionAnsweringRVInfo.getAnswer().toString());
                }catch (NullPointerException e){}
                startActivityForResult(editQuestionIntent, QuestionAnsweringActivity.ANSWER_QUESTION_REQUEST);
            }
        });
    }


    private void backToMainActivity(){
        Intent intent = new Intent(this, StudentMainActivity.class);
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
            case QuestionAnsweringActivity.ANSWER_QUESTION_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        int index = data.getExtras().getInt(QuestionAnsweringActivity.QUESTION_INDEX_INTENT_KEY);
                        Answer answer = new Answer(data.getExtras().getString(QuestionAnsweringActivity.ANSWER_INTENT_KEY));


                        // *Updating the cache:
                        Answer[] answerArray = studentAnswers.getAnswerArray();
                        answerArray[index] = answer;
                        studentAnswers.setAnswerArray(answerArray);
                        new AndroidUtil(this).writeToSharedPreferences(STUDENT_ANSWER_SHARED_FILE, studentConfigsFileId + STUDENT_ANSWER_SHARED_KEY_SUFFIX, studentAnswers.toString());


                        // *Updating the recycler and de RVInfo list:
                        questionAnsweringRVInfoList.get(index).setAnswer(answer);
                        questionAnsweringRVAdapter.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                        new AndroidUtil(this).showToast(R.string.toast_somethingWentWrong, Toast.LENGTH_SHORT);
                    }
                } else{

                }
                break;
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listener methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void sendFAB_onClick(View view){
        if(sendBtnBlocked){
            return;
        }
        if(!allContentLoaded){
            new AndroidUtil(this).showToast(R.string.toast_infoNotLoadedYet, Toast.LENGTH_SHORT);
            return;
        }
        if(alreadyAnswered){
            new AndroidUtil(this).showToast(R.string.toast_examAnswering_answerNotAllowed, Toast.LENGTH_SHORT);
            return;
        }
        formValidator.doVisualValidation();
        if(!formValidator.isValid()){
            new AndroidUtil(this).showToast(R.string.toast_examAnswering_answerAllQuestions, Toast.LENGTH_LONG);
            return;
        }


        sendBtnBlocked = true;


        // *Setting new StudentAnswers object:
        Answer[] newAnswerArray = new Answer[questionAnsweringRVInfoList.size()];
        for (int i = 0; i < questionAnsweringRVInfoList.size(); i++) {
            newAnswerArray[i] = questionAnsweringRVInfoList.get(i).getAnswer();
        }
        studentAnswers = new StudentAnswers(newAnswerArray);


        // *Updating the file on Drive:
        startProgressFragment();
        setProgressMessage(R.string.progress_examAnswering_sendingAnswers);
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.editFile(studentAnswerFileId, null, null, studentAnswers.toString(), new FileEditCallback() {
            @Override
            public void onSuccess() {
                // *Changing the role on Drive:
                driveIOHandler.editFileShare(studentAnswerFileId, getCredential().getSelectedAccountName(), ERole.READER.getValue(), new FileShareEditCallback() {
                    @Override
                    public void onSuccess() {
                        onAnsweringSuccess();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        onAnsweringFailure();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                onAnsweringFailure();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Answer sending callback methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void onAnsweringFailure(){
        stopProgressFragment();
        sendBtnBlocked = false;
        // ERROR answer couldn't be sent
        // *Showing "try again" dialog:
        new AndroidUtil(this).showAlertDialog(null, R.string.alertDialog_title_sendingFailed, R.string.alertDialog_content_tryAgain, R.string.dialog_yes, null, R.string.dialog_no, new DialogCallback() {
            @Override
            public void onPositive() {
                sendFAB_onClick(null);
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

    private void onAnsweringSuccess(){
        stopProgressFragment();
        sendBtnBlocked = false;

        new AndroidUtil(this).showToast(R.string.toast_examAnswering_answerSent, Toast.LENGTH_LONG);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                backToMainActivity();
            }
        });
    }
}
