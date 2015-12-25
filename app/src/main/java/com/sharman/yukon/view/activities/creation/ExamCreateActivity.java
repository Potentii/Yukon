package com.sharman.yukon.view.activities.creation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileCreateCallback;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.FileShareCallback;
import com.sharman.yukon.io.drive.callback.FolderCreateCallback;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.io.drive.util.PermissionStruct;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.main.TeacherMainActivity;
import com.sharman.yukon.view.activities.dialog.AlertDialog;
import com.sharman.yukon.view.activities.dialog.DeliveryDateDialog;
import com.sharman.yukon.view.activities.dialog.StudentPickerDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.FinishStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.FormValidator;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.StudentConfigFilePair;
import com.sharman.yukon.view.activities.util.recycler.QuestionCreationRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.QuestionCreationRVInfo;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ExamCreateActivity extends GoogleRestConnectActivity {
    public static final String REQUEST_CODE_INTENT_KEY = "requestCode";
    public static final String QUESTION_INDEX_INTENT_KEY = "questionIndex";
    public static final String QUESTION_INTENT_KEY = "question";
    public static final String WTA_STRUCT_INTENT_KEY = "wtaStruct";

    public static final String EXAM_INTENT_KEY = "exam";
    public static final String TEACHER_ANSWERS_INTENT_KEY = "teacherAnswers";
    public static final String STUDENTS_ID_ARRAY_INTENT_KEY = "studentsIdArray";

    public static final int ADD_QUESTION_REQUEST = 1010;
    public static final int EDIT_QUESTION_REQUEST = 2020;

    private Exam exam;
    private TeacherAnswers teacherAnswers;
    private int questionQty;

    private List<QuestionCreationRVInfo> questionCreationRVInfoList;
    private QuestionCreationRVAdapter questionCreationRVAdapter;
    private RecyclerView questionsRecyclerView;

    private List<String> idList;

    private DeliveryDateDialog deliveryDateDialog;
    private StudentPickerDialog studentPickerDialog;


    private EditText examTitleIn;
    private EditText examDescriptionIn;
    private EditText examSubjectIn;
    private EditText examDeliveryDateIn;
    private EditText examStudentsIn;

    private TextView examTitleIn_errorOut;
    private TextView examDescriptionIn_errorOut;
    private TextView examSubjectIn_errorOut;
    private TextView examDeliveryDateIn_errorOut;
    private TextView examStudentsIn_errorOut;
    private TextView questionsRecyclerView_errorOut;

    private FormValidator formValidator;


    // *Exam creation data
    private String teacherId;
    private String examFileId;
    private String correctAnswersFileId;
    private String teacherConfigsFileId;
    private String examRootFolderId;
    private String studentFilesFolderId;

    // *Exam creation events
    private StepByStepEvent stepByStepEvent_examCreation;
    private StepByStepEvent stepByStepEvent_examSharing;
    private Set<String> steps_studentFilesAndSharing = new HashSet<>();

    // *Exam creation events Enums
    private enum EExamCreationStep{
        EXAM_FOLDER_CREATION("exam_folder_creation"),
        STUDENTS_FOLDER_CREATION("students_folder_creation"),
        TEACHER_FOLDER_CREATION("teacher_folder_creation"),
        EXAM_FILE_CREATION("exam_file_creation"),
        EXAM_FILE_SHARING("exam_file_sharing"),
        TEACHER_CONFIGS_FILE_CREATION("teacher_configs_file_creation"),
        TEACHER_ANSWERS_FILE_CREATION("teacher_answers_file_creation");

        private String name;
        private EExamCreationStep(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }

    private enum EStudentFilesAndSharingCreationStep{
        STUDENT_FOLDER_CREATION("student_folder_creation"),
        ANSWERS_FILE_CREATION("answers_file_creation"),
        GRADE_FILE_CREATION("grade_file_creation"),
        CONFIGS_FILE_CREATION("configs_file_creation"),
        ANSWERS_FILE_SHARING("answers_file_sharing"),
        GRADE_FILE_SHARING("grade_file_sharing"),
        CONFIGS_FILE_SHARING("configs_file_sharing");

        private String name;
        private EStudentFilesAndSharingCreationStep(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create);



        // *Intent data:
        boolean gotEverything = false;
        try{
            // *Those extras only exists when the user is editing the exam:
            exam = new Exam(getIntent().getExtras().getString(EXAM_INTENT_KEY));
            teacherAnswers = new TeacherAnswers(getIntent().getExtras().getString(TEACHER_ANSWERS_INTENT_KEY));
            idList = new ArrayList<>(Arrays.asList(getIntent().getExtras().getStringArray(STUDENTS_ID_ARRAY_INTENT_KEY)));
            teacherId = exam.getTeacherId();

            Question[] questionArray = exam.getQuestionArray();
            WeightTypeAnswerStruct[] weightTypeAnswerStructArray = teacherAnswers.getWeightTypeAnswerStructArray();

            if(questionArray.length == weightTypeAnswerStructArray.length){
                for (int i = 0; i < questionArray.length; i++) {
                    questionCreationRVInfoList.add(new QuestionCreationRVInfo(i, questionArray[i], weightTypeAnswerStructArray[i]));
                }
            } else{
                exam = null;
                teacherAnswers = null;
            }

            if(exam != null && teacherAnswers != null && idList != null && questionCreationRVInfoList != null){
                gotEverything = true;
            }
        } catch (Exception e){}



        // *Resets all fields if something couldn't be retrieved on intent data:
        if(!gotEverything){
            exam = new Exam("", "", new Date(), null, "", new Question[0]);
            teacherAnswers = new TeacherAnswers(new WeightTypeAnswerStruct[0]);
            questionCreationRVInfoList = new ArrayList<>();
            idList = new ArrayList<>();
            teacherId = null;
        }



        // *Loads the teacher information if it's null:
        if(teacherId == null){
            loadTeacherInfo();
        }



        // *Recycler view:
        questionsRecyclerView = (RecyclerView) findViewById(R.id.questionsRecyclerView);
        questionCreationRVAdapter = new QuestionCreationRVAdapter(this, questionCreationRVInfoList) {
            @Override
            protected void onItemClick(QuestionCreationRVInfo questionCreationRVInfo) {
                editQuestion(questionCreationRVInfo);
            }

            @Override
            protected void onItemRemove(QuestionCreationRVInfo questionCreationRVInfo) {
                askToRemoveQuestion(questionCreationRVInfo);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        questionsRecyclerView.setLayoutManager(linearLayoutManager);
        questionsRecyclerView.setAdapter(questionCreationRVAdapter);



        // *Dialogs:
        deliveryDateDialog = new DeliveryDateDialog();
        deliveryDateDialog.setContext(this);
        deliveryDateDialog.setDialogCallback(new DialogCallback() {
            @Override
            public void onPositive() {
                deliveryDateIn_format();
            }

            @Override
            public void onNegative() { }

            @Override
            public void onNeutral() { }
        });

        studentPickerDialog = new StudentPickerDialog();
        studentPickerDialog.setContext(this);
        studentPickerDialog.setIdList(idList);
        studentPickerDialog.setDialogCallback(new DialogCallback() {
            @Override
            public void onPositive() {
                idList = studentPickerDialog.getIdList();
                studentsIn_format();
            }

            @Override
            public void onNegative() {
            }

            @Override
            public void onNeutral() {
            }
        });



        // *Fields:
        examTitleIn = (EditText) findViewById(R.id.examTitleIn);
        examDescriptionIn = (EditText) findViewById(R.id.examDescriptionIn);
        examSubjectIn = (EditText) findViewById(R.id.examSubjectIn);
        examDeliveryDateIn = (EditText) findViewById(R.id.examDeliveryDateIn);
        examStudentsIn = (EditText) findViewById(R.id.examStudentsIn);

        examTitleIn_errorOut = (TextView) findViewById(R.id.examTitleIn_errorOut);
        examDescriptionIn_errorOut = (TextView) findViewById(R.id.examDescriptionIn_errorOut);
        examSubjectIn_errorOut = (TextView) findViewById(R.id.examSubjectIn_errorOut);
        examDeliveryDateIn_errorOut = (TextView) findViewById(R.id.examDeliveryDateIn_errorOut);
        examStudentsIn_errorOut = (TextView) findViewById(R.id.examStudentsIn_errorOut);
        questionsRecyclerView_errorOut = (TextView) findViewById(R.id.questionsRecyclerView_errorOut);



        // *Validator:
        formValidator = new FormValidator(this)
                .addField(examTitleIn, examTitleIn_errorOut, EnumSet.of(FormValidator.EValidation.REQUIRED))
                .addField(examDescriptionIn, examDescriptionIn_errorOut, EnumSet.of(FormValidator.EValidation.REQUIRED))
                .addField(examSubjectIn, examSubjectIn_errorOut, EnumSet.of(FormValidator.EValidation.REQUIRED))
                .addComplexField(deliveryDateDialog, examDeliveryDateIn, examDeliveryDateIn_errorOut)
                .addComplexField(studentPickerDialog, examStudentsIn, examStudentsIn_errorOut)
                .addComplexField(questionCreationRVAdapter, null, questionsRecyclerView_errorOut);



        // *Auto fill:
        examTitleIn.setText(exam.getTitle());
        examDescriptionIn.setText(exam.getDescription());
        examSubjectIn.setText(exam.getSubject());
        deliveryDateIn_format();
        studentsIn_format();
        questionCreationRVAdapter.update();
    }



    private void studentsIn_format(){
        if (idList.size() == 0) {
            examStudentsIn.setText("");
        } else if (idList.size() == 1) {
            examStudentsIn.setText(idList.size() + " student");
        } else {
            examStudentsIn.setText(idList.size() + " students");
        }
    }

    private void deliveryDateIn_format() {
        Date date = deliveryDateDialog.getDate();
        if (date == null) {
            examDeliveryDateIn.setText("");
        } else{
            examDeliveryDateIn.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
        }
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
        case ADD_QUESTION_REQUEST:
            if(resultCode == Activity.RESULT_OK){
                try {
                    Question question = new Question(data.getExtras().getString(QuestionsCreateActivity.QUESTION_INTENT_KEY));
                    WeightTypeAnswerStruct weightTypeAnswerStruct = new WeightTypeAnswerStruct(data.getExtras().getString(QuestionsCreateActivity.WTA_STRUCT_INTENT_KEY));
                    int questionIndex = data.getExtras().getInt(QuestionsCreateActivity.QUESTION_INDEX_INTENT_KEY);

                    // *Updating the recycler and de RVInfo list:
                    questionCreationRVInfoList.add(new QuestionCreationRVInfo(questionIndex, question, weightTypeAnswerStruct));
                    questionCreationRVAdapter.update();
                    //TODO nao está mostrando, talvez seja o bug do tamanho com os recyclerview

                } catch (JSONException e){
                    e.printStackTrace();
                }
            } else{

            }
            break;
        case EDIT_QUESTION_REQUEST:
            if(resultCode == Activity.RESULT_OK){
                try {
                    Question question = new Question(data.getExtras().getString(QuestionsCreateActivity.QUESTION_INTENT_KEY));
                    WeightTypeAnswerStruct weightTypeAnswerStruct = new WeightTypeAnswerStruct(data.getExtras().getString(QuestionsCreateActivity.WTA_STRUCT_INTENT_KEY));
                    int questionIndex = data.getExtras().getInt(QuestionsCreateActivity.QUESTION_INDEX_INTENT_KEY);

                    // *Updating the recycler and de RVInfo list:
                    questionCreationRVInfoList.remove(questionIndex);
                    questionCreationRVInfoList.add(questionIndex, new QuestionCreationRVInfo(questionIndex, question, weightTypeAnswerStruct));
                    questionCreationRVAdapter.update();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            } else{

            }
            break;
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listeners methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void deliveryDateIn_onClick(View view){
        deliveryDateDialog.show(getFragmentManager(), "delivery_date_dialog");
    }


    public void studentsIn_onClick(View view){
        studentPickerDialog.setIdList(idList);
        studentPickerDialog.show(getFragmentManager(), "student_picker_dialog");
    }


    public void addQuestionFAB_onClick(View view){
        Intent addQuestionIntent = new Intent(this, QuestionsCreateActivity.class)
                .putExtra(REQUEST_CODE_INTENT_KEY, ADD_QUESTION_REQUEST)
                .putExtra(QUESTION_INDEX_INTENT_KEY, questionCreationRVInfoList.size());
        startActivityForResult(addQuestionIntent, ADD_QUESTION_REQUEST);
    }


    private void editQuestion(final QuestionCreationRVInfo questionCreationRVInfo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent editQuestionIntent = new Intent(getApplicationContext(), QuestionsCreateActivity.class)
                        .putExtra(REQUEST_CODE_INTENT_KEY, EDIT_QUESTION_REQUEST)
                        .putExtra(QUESTION_INDEX_INTENT_KEY, questionCreationRVInfo.getIndex())
                        .putExtra(QUESTION_INTENT_KEY, questionCreationRVInfo.getQuestion().toString())
                        .putExtra(WTA_STRUCT_INTENT_KEY, questionCreationRVInfo.getWeightTypeAnswerStruct().toString());
                startActivityForResult(editQuestionIntent, EDIT_QUESTION_REQUEST);
            }
        });
    }


    private void askToRemoveQuestion(final QuestionCreationRVInfo questionCreationRVInfo){
        // TODO CHANGE THIS: remove the question and show a snackbar with the UNDO command

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog();
                alertDialog.setTitleTxt("Remove question");
                alertDialog.setContentTxt("Sure you want to remove question " + (questionCreationRVInfo.getIndex() + 1) + " from exam?");
                alertDialog.setPositiveBtnTxt("Yes");
                alertDialog.setNegativeBtnTxt("No");
                alertDialog.setDialogCallback(new DialogCallback() {
                    @Override
                    public void onPositive() {
                        // *Removing the question:
                        questionCreationRVInfoList.remove(questionCreationRVInfo);

                        // *Updating the indexes of the questions:
                        for (int i = 0; i < questionCreationRVInfoList.size(); i++) {
                            questionCreationRVInfoList.get(i).setIndex(i);
                        }

                        // *Update the recyclerView:
                        questionCreationRVAdapter.update();
                    }

                    @Override
                    public void onNegative() {
                    }

                    @Override
                    public void onNeutral() {
                    }
                });

                alertDialog.show(getFragmentManager(), "alert_dialog_remove_question");
            }
        });
    }


    public void confirmFAB_onClick(View view){
        formValidator.doVisualValidation();
        if(!formValidator.isValid()){
            new AndroidUtil(this).showToast(R.string.toast_invalidFields, Toast.LENGTH_LONG);
            return;
        }
        if(teacherId == null){
            // TODO show the "try again" dialog
            return;
        }


        // *Building the WTA and Question array:
        Question[] questionArray = new Question[questionCreationRVInfoList.size()];
        WeightTypeAnswerStruct[] wtaArray = new WeightTypeAnswerStruct[questionCreationRVInfoList.size()];
        for (int i = 0; i < questionCreationRVInfoList.size(); i++) {
            questionArray[i] = questionCreationRVInfoList.get(i).getQuestion();
            wtaArray[i] = questionCreationRVInfoList.get(i).getWeightTypeAnswerStruct();
        }


        questionQty = questionArray.length;


        // *Putting them on a new Exam and TeacherAnswer object:
        exam = new Exam(
                examTitleIn.getText().toString(),
                examDescriptionIn.getText().toString(),
                deliveryDateDialog.getDate(),
                teacherId,
                examSubjectIn.getText().toString(),
                questionArray);

        teacherAnswers = new TeacherAnswers(wtaArray);


        create(teacherId, idList, exam, teacherAnswers);
    }



    // *Loads the teacher's user id on GPlus:
    private void loadTeacherInfo(){
        final PlusIOHandler plusIOHandler = new PlusIOHandler(getCredential());
        plusIOHandler.readPerson("me", new PersonReadCallback() {
            @Override
            public void onSuccess(Person person) {
                teacherId = person.getId();
            }

            @Override
            public void onFailure(Exception exception) {
                teacherId = null;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog();
                        alertDialog.setTitleTxt("Can't load your information");
                        alertDialog.setContentTxt("Do you want to try again, or discard this exam?");
                        alertDialog.setPositiveBtnTxt("Try again");
                        alertDialog.setNeutralBtnTxt("Exit, but save as draft");
                        alertDialog.setNegativeBtnTxt("Discard");
                        alertDialog.setDialogCallback(new DialogCallback() {
                            @Override
                            public void onPositive() {
                                loadTeacherInfo();
                            }

                            @Override
                            public void onNegative() {
                                Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onNeutral() {
                                // TODO save as draft
                            }
                        });

                        alertDialog.show(getFragmentManager(), "alert_dialog_cant_load_information");
                    }
                });

            }
        });
    }





    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Exam creation methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void create(final String teacherId, final List<String> idList, final Exam exam, final TeacherAnswers teacherAnswers){
        startProgressFragment();
        setProgressMessage("Creating exam on drive");


        // *Setting StudentConfigFilePair array:
        final StudentConfigFilePair[] studentConfigFilePairArray = new StudentConfigFilePair[idList.size()];
        for (int i = 0; i < studentConfigFilePairArray.length; i++) {
            studentConfigFilePairArray[i] = new StudentConfigFilePair(idList.get(i), "");
        }


        // *Setting steps for exam sharing event:
        steps_studentFilesAndSharing.clear();
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.STUDENT_FOLDER_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.GRADE_FILE_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_SHARING.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.GRADE_FILE_SHARING.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_SHARING.getName());


        // *Setting steps for exam creation event:
        Set<String> steps_examCreation = new HashSet<>();
        steps_examCreation.add(EExamCreationStep.EXAM_FOLDER_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.STUDENTS_FOLDER_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.TEACHER_FOLDER_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.EXAM_FILE_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.EXAM_FILE_SHARING.getName());
        steps_examCreation.add(EExamCreationStep.TEACHER_CONFIGS_FILE_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.TEACHER_ANSWERS_FILE_CREATION.getName());


        // *Setting exam creation event, and callbacks:
        stepByStepEvent_examCreation = new StepByStepEvent(steps_examCreation)
                .setFinishStepCallback(new FinishStepByStepEventCallback() {
                    @Override
                    public void onSuccess() {

                        Set<String> steps_examSharing = new HashSet<>();
                        for (int i = 0; i < studentConfigFilePairArray.length; i++) {
                            steps_examSharing.add("student_" + i);
                        }

                        stepByStepEvent_examSharing = new StepByStepEvent(steps_examSharing)
                                .setFinishStepCallback(new FinishStepByStepEventCallback() {
                                    @Override
                                    public void onSuccess() {
                                        setProgressMessage("Finishing");

                                        String[] studentConfigsFileIdArray = new String[studentConfigFilePairArray.length];

                                        for(int i=0; i<studentConfigFilePairArray.length; i++){
                                            studentConfigsFileIdArray[i] = studentConfigFilePairArray[i].getConfigFileId();
                                        }

                                        TeacherConfigs teacherConfigs = new TeacherConfigs(studentConfigsFileIdArray, correctAnswersFileId, examFileId);

                                        new DriveIOHandler(getCredential()).editFile(teacherConfigsFileId, null, null, teacherConfigs.toString(), new FileEditCallback() {
                                            @Override
                                            public void onSuccess() {
                                                onCreationSuccess();
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                Set<String> failedSteps = new HashSet<>();
                                                failedSteps.add("teacher_configs_file_edit");
                                                onCreationFailure(failedSteps);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Set<String> failedSteps) {
                                        onCreationFailure(failedSteps);
                                    }
                                });


                        setProgressMessage("Sharing exam");
                        for (int i = 0; i < studentConfigFilePairArray.length; i++) {
                            createEachStudentFolderAndShare(studentFilesFolderId, studentConfigFilePairArray[i], i);
                        }
                    }

                    @Override
                    public void onFailure(Set<String> failedSteps) {
                        onCreationFailure(failedSteps);
                    }
                });




        // *Starting to create exam on Google Drive:

        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *ExamRoot folder creation:
        new DriveIOHandler(getCredential()).createFolder("", exam.getTitle(), "Yukon exam folder \n(All the actions that you may need to make with this folder/exam has to be done under the Yukon App interface)\n(Do not share this folder with any of your students)\n(Do not delete, change or move any file inside this folder)", new FolderCreateCallback() {
            @Override
            public void onSuccess(String folderId) {
                setProgressDetailMessage("Exam folder created");
                examRootFolderId = folderId;
                stepByStepEvent_examCreation.registerStep(EExamCreationStep.EXAM_FOLDER_CREATION.getName(), true);

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *TeacherFiles folder creation:
                new DriveIOHandler(getCredential()).createFolder(folderId, "TeacherFiles", "", new FolderCreateCallback() {
                    @Override
                    public void onSuccess(String folderId) {
                        setProgressDetailMessage("Teacher folder created");
                        //teacherFilesFolderId = folderId;
                        stepByStepEvent_examCreation.registerStep(EExamCreationStep.TEACHER_FOLDER_CREATION.getName(), true);

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *CorrectAnswers file creation:
                        new DriveIOHandler(getCredential()).createFile(folderId, "CorrectAnswers", "", EMimeType.JSON.getMimeType(), teacherAnswers.toString(), new FileCreateCallback() {
                            @Override
                            public void onSuccess(String fileId) {
                                setProgressDetailMessage("Correct answers file created");
                                correctAnswersFileId = fileId;
                                stepByStepEvent_examCreation.registerStep(EExamCreationStep.TEACHER_ANSWERS_FILE_CREATION.getName(), true);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // TODO Error: CorrectAnswers file
                                stepByStepEvent_examCreation.registerStep(EExamCreationStep.TEACHER_ANSWERS_FILE_CREATION.getName(), false);
                            }
                        });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *


                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *TeacherConfigs file creation:
                        new DriveIOHandler(getCredential()).createFile(folderId, "Configs", "Teacher's configuration file", EMimeType.TEACHER_CONFIG.getMimeType(), "", new FileCreateCallback() {
                            @Override
                            public void onSuccess(String fileId) {
                                setProgressDetailMessage("Teacher configs file created");
                                teacherConfigsFileId = fileId;
                                stepByStepEvent_examCreation.registerStep(EExamCreationStep.TEACHER_CONFIGS_FILE_CREATION.getName(), true);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // TODO Error: TeacherConfigs file
                                stepByStepEvent_examCreation.registerStep(EExamCreationStep.TEACHER_CONFIGS_FILE_CREATION.getName(), false);
                            }
                        });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO Error: TeacherFiles folder
                        stepByStepEvent_examCreation.registerStep(EExamCreationStep.TEACHER_FOLDER_CREATION.getName(), false);
                    }
                });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *


                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *Exam file creation:
                new DriveIOHandler(getCredential()).createFile(folderId, "Exam", exam.getDescription(), EMimeType.JSON.getMimeType(), exam.toString(), new FileCreateCallback() {
                    @Override
                    public void onSuccess(String fileId) {
                        setProgressDetailMessage("Exam file created");
                        examFileId = fileId;
                        stepByStepEvent_examCreation.registerStep(EExamCreationStep.EXAM_FILE_CREATION.getName(), true);

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *StudentFiles folder creation:
                        new DriveIOHandler(getCredential()).createFolder(examRootFolderId, "StudentFiles", "", new FolderCreateCallback() {
                            @Override
                            public void onSuccess(String folderId) {
                                setProgressDetailMessage("Students folder created");
                                studentFilesFolderId = folderId;
                                stepByStepEvent_examCreation.registerStep(EExamCreationStep.STUDENTS_FOLDER_CREATION.getName(), true);

                                PermissionStruct[] permissionStructArray = new PermissionStruct[studentConfigFilePairArray.length];
                                for (int i = 0; i < studentConfigFilePairArray.length; i++) {
                                    permissionStructArray[i] = new PermissionStruct(studentConfigFilePairArray[i].getUserId(), "user", "reader");
                                }


                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *Exam file share:
                                new DriveIOHandler(getCredential()).shareFile(examFileId, null, permissionStructArray, new FileShareCallback() {
                                    @Override
                                    public void onSuccess() {
                                        setProgressDetailMessage("Exam file shared");
                                        stepByStepEvent_examCreation.registerStep(EExamCreationStep.EXAM_FILE_SHARING.getName(), true);
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        // TODO Error: Exam file share
                                        stepByStepEvent_examCreation.registerStep(EExamCreationStep.EXAM_FILE_SHARING.getName(), false);
                                    }
                                });
                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // TODO Error: StudentFiles folder
                                stepByStepEvent_examCreation.registerStep(EExamCreationStep.STUDENTS_FOLDER_CREATION.getName(), false);
                            }
                        });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO Error: Exam file
                        stepByStepEvent_examCreation.registerStep(EExamCreationStep.EXAM_FILE_CREATION.getName(), false);
                    }
                });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

            }

            @Override
            public void onFailure(String errorMessage) {
                // TODO Error: ExamRoot folder
                stepByStepEvent_examCreation.registerStep(EExamCreationStep.EXAM_FOLDER_CREATION.getName(), false);
            }
        });
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

    }






    private void createEachStudentFolderAndShare(String parentFolderId, final StudentConfigFilePair studentConfigFilePair, final int index){
        final String type = "user";


        final StepByStepEvent stepByStepEvent_studentFilesAndSharing = new StepByStepEvent(steps_studentFilesAndSharing)
                .setFinishStepCallback(new FinishStepByStepEventCallback() {
                    @Override
                    public void onSuccess() {
                        setProgressDetailMessage("Shared with " + studentConfigFilePair.getUserId());
                        stepByStepEvent_examSharing.registerStep("student_" + index, true);
                    }

                    @Override
                    public void onFailure(Set<String> failedSteps) {
                        stepByStepEvent_examSharing.registerStep("student_" + index, false);
                    }
                });


        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *Student folder creation:
        new DriveIOHandler(getCredential()).createFolder(parentFolderId, "Student[" + studentConfigFilePair.getUserId() + "]", "", new FolderCreateCallback() {
            @Override
            public void onSuccess(final String studentFolderId) {
                StudentAnswers studentAnswers = new StudentAnswers(new Answer[questionQty]);
                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.STUDENT_FOLDER_CREATION.getName(), true);

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *Answers file creation:
                new DriveIOHandler(getCredential()).createFile(studentFolderId, "Answers", studentConfigFilePair.getUserId() + "'s answers", EMimeType.JSON.getMimeType(), studentAnswers.toString(), new FileCreateCallback() {
                    @Override
                    public void onSuccess(final String answersFileId) {
                        Grade grade = new Grade(-1, new Boolean[]{});
                        stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_CREATION.getName(), true);

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *Grade file creation:
                        new DriveIOHandler(getCredential()).createFile(studentFolderId, "Grade", studentConfigFilePair.getUserId() + "'s grade", EMimeType.JSON.getMimeType(), grade.toString(), new FileCreateCallback() {
                            @Override
                            public void onSuccess(final String gradeFileId) {
                                StudentConfigs studentConfigs = new StudentConfigs(gradeFileId, answersFileId, examFileId, studentConfigFilePair.getUserId());
                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.GRADE_FILE_CREATION.getName(), true);

                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *Configs file creation:
                                new DriveIOHandler(getCredential()).createFile(studentFolderId, "Configs", studentConfigFilePair.getUserId() + "'s configuration file", EMimeType.STUDENT_CONFIG.getMimeType(), studentConfigs.toString(), new FileCreateCallback() {
                                    @Override
                                    public void onSuccess(final String configsFileId) {
                                        studentConfigFilePair.setConfigFileId(configsFileId);
                                        stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_CREATION.getName(), true);

                                        new DriveIOHandler(getCredential()).shareFile(answersFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "writer")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {
                                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_SHARING.getName(), true);
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_SHARING.getName(), false);
                                            }
                                        });

                                        new DriveIOHandler(getCredential()).shareFile(gradeFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "reader")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {
                                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.GRADE_FILE_SHARING.getName(), true);
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.GRADE_FILE_SHARING.getName(), false);
                                            }
                                        });

                                        new DriveIOHandler(getCredential()).shareFile(configsFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "reader")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {
                                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_SHARING.getName(), true);
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_SHARING.getName(), false);
                                            }
                                        });

                                        //addStudentFolderCreationFlag(true);
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        // TODO Error: Student Configs file
                                        stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_CREATION.getName(), false);
                                    }
                                });
                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // TODO Error: Grade file
                                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.GRADE_FILE_CREATION.getName(), false);
                            }
                        });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO Error: Answers file
                        stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_CREATION.getName(), false);
                    }
                });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

            }

            @Override
            public void onFailure(String errorMessage) {
                // TODO Error: Student folder
                stepByStepEvent_studentFilesAndSharing.registerStep(EStudentFilesAndSharingCreationStep.STUDENT_FOLDER_CREATION.getName(), false);
            }
        });
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Exam creation callbacks:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void onCreationSuccess(){
        stopProgressFragment();
        System.out.println(">> Creation Success");
        new AndroidUtil(this).showToast(R.string.toast_examCreate_examCreated, Toast.LENGTH_SHORT);
        final Activity activity = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, TeacherMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onCreationFailure(Set<String> failedSteps){
        stopProgressFragment();
        System.out.print(">> Creation Failed");

        System.out.print(">> Failed steps: [");
        Iterator<String> iterator = failedSteps.iterator();
        while(iterator.hasNext()){
            System.out.print(iterator.next());
            if(iterator.hasNext()){
                System.out.print(", ");
            }
        }
        System.out.print("]");


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog();
                alertDialog.setTitleTxt("The exam could not be created");
                alertDialog.setContentTxt("Do you want to try again, or discard this exam?");
                alertDialog.setPositiveBtnTxt("Try again");
                alertDialog.setNeutralBtnTxt("Exit, but save as draft");
                alertDialog.setNegativeBtnTxt("Discard");
                alertDialog.setDialogCallback(new DialogCallback() {
                    @Override
                    public void onPositive() {
                        loadTeacherInfo();
                    }

                    @Override
                    public void onNegative() {
                        Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNeutral() {
                        // TODO save as draft
                    }
                });

                alertDialog.show(getFragmentManager(), "alert_dialog_cant_load_information");
            }
        });
    }
}
