package com.sharman.yukon.view.activities.creation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.plus.model.Person;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.R;

import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileCreateCallback;
import com.sharman.yukon.io.drive.callback.FileShareCallback;
import com.sharman.yukon.io.drive.callback.FolderCreateCallback;
import com.sharman.yukon.io.drive.util.PermissionStruct;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.TeacherMainActivity;
import com.sharman.yukon.view.activities.dialog.StudentPickerDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.StepByStepEvent;
import com.sharman.yukon.view.activities.util.FinishStepByStepEventCallback;
import com.sharman.yukon.view.activities.util.StudentConfigFilePair;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ExamCreateConfirmActivity extends GoogleRestConnectActivity implements DialogCallback {
    private Exam exam;
    private TeacherAnswers teacherAnswers;
    private StudentConfigFilePair[] studentConfigFilePairArray;

    // *Exam creation variables
    private String teacherId = null;
    private String examFileId;
    private String correctAnswersFileId;
    private String teacherConfigsFileId;
    private String examRootFolderId;
    private String studentFilesFolderId;

    private List<String> idList = new ArrayList<>();
    private StudentPickerDialog studentPickerDialog;

    private StepByStepEvent stepByStepEvent_examCreation;
    private StepByStepEvent stepByStepEvent_examSharing;
    private Set<String> steps_studentFilesAndSharing = new HashSet<>();


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
        setContentView(R.layout.activity_exam_create_confirm);

        studentPickerDialog = new StudentPickerDialog();

        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.STUDENT_FOLDER_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.GRADE_FILE_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_CREATION.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.ANSWERS_FILE_SHARING.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.GRADE_FILE_SHARING.getName());
        steps_studentFilesAndSharing.add(EStudentFilesAndSharingCreationStep.CONFIGS_FILE_SHARING.getName());

        Set<String> steps_examCreation = new HashSet<>();
        steps_examCreation.add(EExamCreationStep.EXAM_FOLDER_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.STUDENTS_FOLDER_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.TEACHER_FOLDER_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.EXAM_FILE_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.EXAM_FILE_SHARING.getName());
        steps_examCreation.add(EExamCreationStep.TEACHER_CONFIGS_FILE_CREATION.getName());
        steps_examCreation.add(EExamCreationStep.TEACHER_ANSWERS_FILE_CREATION.getName());

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

                                        TeacherConfigs teacherConfigs = new TeacherConfigs(studentConfigsFileIdArray, correctAnswersFileId, examFileId, exam.getTitle(), exam.getDeliverDate(), exam.getSubject(), exam.getTeacherId());

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

    }



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            exam = new Exam(getIntent().getExtras().getString("exam"));
            teacherAnswers = new TeacherAnswers(getIntent().getExtras().getString("teacherAnswers"));

            System.out.println(teacherAnswers.toString());

            final View infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
            final ImageView infoImg           = (ImageView) infoPhotoHeader.findViewById(R.id.infoImg);
            final TextView primaryInfoOut     = (TextView) infoPhotoHeader.findViewById(R.id.primaryInfoOut);
            final TextView secondaryInfoOut   = (TextView) infoPhotoHeader.findViewById(R.id.secondaryInfoOut);
            final TextView tertiaryInfoOut    = (TextView) infoPhotoHeader.findViewById(R.id.tertiaryInfoOut);

            primaryInfoOut.setText(exam.getTitle());
            secondaryInfoOut.setText(exam.getSubject());
            tertiaryInfoOut.setText(new SimpleDateFormat("dd/MM/yyyy").format(exam.getDeliverDate()));

            startProgressFragment();
            setProgressMessage("Loading data");
            final PlusIOHandler plusIOHandler = new PlusIOHandler(getCredential());
            plusIOHandler.readPerson("me", new PersonReadCallback() {
                @Override
                public void onSuccess(Person person) {

                    teacherId = person.getId();

                    try {
                        exam.setTeacherId(teacherId);
                    } catch (JSONException e){
                        teacherId = null;
                        e.printStackTrace();
                    }

                    plusIOHandler.readPersonImg(person, new PersonImgReadCallback() {
                        @Override
                        public void onSuccess(final Bitmap bitmap) {
                            stopProgressFragment();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                    roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                                    roundedBitmapDrawable.setAntiAlias(true);
                                    infoImg.setImageDrawable(roundedBitmapDrawable);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            //TODO error
                            stopProgressFragment();
                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    // TODO error
                    stopProgressFragment();
                }
            });


        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
            System.out.println("Erro ao tentar recuperar 'Exam'");
        }
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Exam creation methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */

    private void onCreationSuccess(){
        stopProgressFragment();
        System.out.println(">> Creation Success");
        new AndroidUtil(this).showToast(R.string.toast_examCreateConfirm_examCreated, Toast.LENGTH_SHORT);
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

        new AndroidUtil(this).showToast(R.string.toast_somethingWentWrong, Toast.LENGTH_SHORT);
    }



    private void createExamOnDrive(){
        startProgressFragment();
        setProgressMessage("Creating exam on drive");


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
                StudentAnswers studentAnswers = new StudentAnswers(new Answer[]{});
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
                                StudentConfigs studentConfigs = new StudentConfigs(gradeFileId, answersFileId, examFileId, studentConfigFilePair.getUserId(), exam.getTitle(), exam.getDeliverDate(), exam.getSubject(), exam.getTeacherId());
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
     *  * DialogCallback methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public void onPositive() {
        idList = studentPickerDialog.getIdList();
    }

    @Override
    public void onNegative() {
        studentPickerDialog.setIdList(idList);
    }

    @Override
    public void onNeutral() {

    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listeners methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void studentIn_onClick(View view){
        if(!isConnected()){
            return;
        }

        studentPickerDialog.setDialogCallback(this);
        studentPickerDialog.setIdList(idList);

        studentPickerDialog.show(getFragmentManager(), "student_picker_dialog");
    }



    private void examCreateConfirmFinishActionButton_onClick(){
        if(!isConnected() || idList.isEmpty() || teacherId == null || stepByStepEvent_examCreation == null){
            return;
        }

        studentConfigFilePairArray = new StudentConfigFilePair[idList.size()];
        for (int i = 0; i < studentConfigFilePairArray.length; i++) {
            studentConfigFilePairArray[i] = new StudentConfigFilePair(idList.get(i), "");
        }

        createExamOnDrive();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * ActionBar methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exam_create_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.examCreateConfirmFinishActionButton:
                examCreateConfirmFinishActionButton_onClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
