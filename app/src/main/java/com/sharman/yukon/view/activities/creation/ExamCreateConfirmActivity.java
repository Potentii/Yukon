package com.sharman.yukon.view.activities.creation;

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
import com.sharman.yukon.view.activities.MainActivity;
import com.sharman.yukon.view.activities.dialog.StudentPickerDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.StudentConfigFilePair;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ExamCreateConfirmActivity extends GoogleRestConnectActivity implements DialogCallback {
    private Exam exam;
    private TeacherAnswers teacherAnswers;
    private StudentConfigFilePair[] studentConfigFilePairArray;

    // *Exam creation variables
    private String teacherId = null;
    private String examFileId;
    private String correctAnswersFileId;
    private String examRootFolderId;
    private String teacherFilesFolderId;
    private String studentFilesFolderId;
    private boolean onCreationFailOrSuccessCalled;
    private int studentFoldersCreated;

    // *Student picker dialog variables
    private List<String> idList = new ArrayList<>();
    private StudentPickerDialog studentPickerDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create_confirm);

        onCreationFailOrSuccessCalled = false;
        studentFoldersCreated = 0;
        studentPickerDialog = new StudentPickerDialog();
    }


    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            exam = new Exam(getIntent().getExtras().getString("exam"));
            teacherAnswers = new TeacherAnswers(getIntent().getExtras().getString("teacherAnswers"));

            final View infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
            final ImageView infoImg           = (ImageView) infoPhotoHeader.findViewById(R.id.infoImg);
            final TextView primaryInfoOut     = (TextView) infoPhotoHeader.findViewById(R.id.primaryInfoOut);
            final TextView secondaryInfoOut   = (TextView) infoPhotoHeader.findViewById(R.id.secondaryInfoOut);
            final TextView tertiaryInfoOut    = (TextView) infoPhotoHeader.findViewById(R.id.tertiaryInfoOut);

            primaryInfoOut.setText(exam.getTitle());
            secondaryInfoOut.setText(exam.getSubject());
            tertiaryInfoOut.setText(new SimpleDateFormat("dd/MM/yyyy").format(exam.getDeliverDate()));

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
                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    // TODO error
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
    private synchronized void onCreationSuccess(){
        System.out.println("Creation Success CALLED");
        if (!onCreationFailOrSuccessCalled) {
            onCreationFailOrSuccessCalled = true;

            new AndroidUtil(this).showToast("Success", Toast.LENGTH_SHORT);

            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private synchronized void onCreationFail(){
        System.out.println("Creation Fail CALLED");
        if (!onCreationFailOrSuccessCalled) {
            onCreationFailOrSuccessCalled = true;

            new AndroidUtil(this).showToast("Fail", Toast.LENGTH_SHORT);
        }
    }


    private void createExamOnDrive(){
        new AndroidUtil(this).showToast("Working...", Toast.LENGTH_LONG);

        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *ExamRoot folder creation:
        new DriveIOHandler(getCredential()).createFolder("", exam.getTitle(), "Yukon exam folder", new FolderCreateCallback() {
            @Override
            public void onSuccess(String folderId) {
                examRootFolderId = folderId;

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *TeacherFiles folder creation:
                new DriveIOHandler(getCredential()).createFolder(folderId, "TeacherFiles", "", new FolderCreateCallback() {
                    @Override
                    public void onSuccess(String folderId) {
                        teacherFilesFolderId = folderId;

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *CorrectAnswers file creation:
                        new DriveIOHandler(getCredential()).createFile(folderId, "CorrectAnswers", "", EMimeType.JSON.getMimeType(), teacherAnswers.toString(), new FileCreateCallback() {
                            @Override
                            public void onSuccess(String fileId) {
                                correctAnswersFileId = fileId;
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // TODO Error: CorrectAnswers file
                                onCreationFail();
                            }
                        });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO Error: TeacherFiles folder
                        onCreationFail();
                    }
                });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *


                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *Exam file creation:
                new DriveIOHandler(getCredential()).createFile(folderId, "Exam", "Exam file", EMimeType.JSON.getMimeType(), exam.toString(), new FileCreateCallback() {
                    @Override
                    public void onSuccess(String fileId) {
                        examFileId = fileId;

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *StudentFiles folder creation:
                        new DriveIOHandler(getCredential()).createFolder(examRootFolderId, "StudentFiles", "", new FolderCreateCallback() {
                            @Override
                            public void onSuccess(String folderId) {
                                studentFilesFolderId = folderId;

                                PermissionStruct[] permissionStructArray = new PermissionStruct[studentConfigFilePairArray.length];

                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *Students folders creation:
                                for (int i = 0; i < studentConfigFilePairArray.length; i++) {
                                    permissionStructArray[i] = new PermissionStruct(studentConfigFilePairArray[i].getUserId(), "user", "reader");
                                    createEachStudentFolder(folderId, studentConfigFilePairArray[i]);
                                }
                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *


                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *Exam file share:
                                new DriveIOHandler(getCredential()).shareFile(examFileId, null, permissionStructArray, new FileShareCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        // TODO Error: Exam file share
                                        onCreationFail();
                                    }
                                });
                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // TODO Error: StudentFiles folder
                                onCreationFail();
                            }
                        });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO Error: Exam file
                        onCreationFail();
                    }
                });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

            }

            @Override
            public void onFailure(String errorMessage) {
                // TODO Error: ExamRoot folder
                onCreationFail();
            }
        });
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
    }



    private void createEachStudentFolder(String parentFolderId, final StudentConfigFilePair studentConfigFilePair){
        final String type = "user";

        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *Student folder creation:
        new DriveIOHandler(getCredential()).createFolder(parentFolderId, "Student", "", new FolderCreateCallback() {
            @Override
            public void onSuccess(final String studentFolderId) {
                StudentAnswers studentAnswers = new StudentAnswers(new Answer[]{});

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *Answers file creation:
                new DriveIOHandler(getCredential()).createFile(studentFolderId, "Answers", "", EMimeType.JSON.getMimeType(), studentAnswers.toString(), new FileCreateCallback() {
                    @Override
                    public void onSuccess(final String answersFileId) {
                        Grade grade = new Grade(-1, new Boolean[]{});

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *Grade file creation:
                        new DriveIOHandler(getCredential()).createFile(studentFolderId, "Grade", "", EMimeType.JSON.getMimeType(), grade.toString(), new FileCreateCallback() {
                            @Override
                            public void onSuccess(final String gradeFileId) {
                                StudentConfigs studentConfigs = new StudentConfigs(gradeFileId, answersFileId, examFileId, studentConfigFilePair.getUserId(), exam.getTitle(), exam.getDeliverDate(), exam.getSubject(), exam.getTeacherId());

                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *Configs file creation:
                                new DriveIOHandler(getCredential()).createFile(studentFolderId, "Configs", "Student's configuration file", EMimeType.STUDENT_CONFIG.getMimeType(), studentConfigs.toString(), new FileCreateCallback() {
                                    @Override
                                    public void onSuccess(final String configsFileId) {
                                        studentConfigFilePair.setConfigFileId(configsFileId);

                                        new DriveIOHandler(getCredential()).shareFile(answersFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "writer")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                onCreationFail();
                                            }
                                        });

                                        new DriveIOHandler(getCredential()).shareFile(gradeFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "reader")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                onCreationFail();
                                            }
                                        });

                                        new DriveIOHandler(getCredential()).shareFile(configsFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "reader")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                onCreationFail();
                                            }
                                        });

                                        addStudentFolderCreationFlag(true);
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        // TODO Error: Student Configs file
                                        addStudentFolderCreationFlag(false);
                                    }
                                });
                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // TODO Error: Grade file
                                addStudentFolderCreationFlag(false);
                            }
                        });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO Error: Answers file
                        addStudentFolderCreationFlag(false);
                    }
                });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

            }

            @Override
            public void onFailure(String errorMessage) {
                // TODO Error: Student folder
                addStudentFolderCreationFlag(false);
            }
        });
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
    }



    // *To create the teacher Configs file I have to get all the student individual folders reference, so that's because of this I had to create this method:
    private synchronized void addStudentFolderCreationFlag(boolean success){
        // *For each student folder that have been created, the method verifies if it have been successful:
        if(success){
            // *If successful, it verifies if all the students folders has been created already:
            studentFoldersCreated++;
            if(studentFoldersCreated == studentConfigFilePairArray.length){
                // *If all the job has been done, then the teacher Config file is generated:
                generateTeacherConfigFile();
            }
        } else{
            // *If unsuccessful, report a problem to the user:
            onCreationFail();
        }
    }


    private void generateTeacherConfigFile(){

        String[] studentConfigsFileIdArray = new String[studentConfigFilePairArray.length];

        for(int i=0; i<studentConfigFilePairArray.length; i++){
            studentConfigsFileIdArray[i] = studentConfigFilePairArray[i].getConfigFileId();
        }

        TeacherConfigs teacherConfigs = new TeacherConfigs(studentConfigsFileIdArray, correctAnswersFileId, examFileId, exam.getTitle(), exam.getDeliverDate(), exam.getSubject(), exam.getTeacherId());

        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *Teacher Configs file creation:
        new DriveIOHandler(getCredential()).createFile(teacherFilesFolderId, "Configs", "Teacher's configuration file", EMimeType.TEACHER_CONFIG.getMimeType(), teacherConfigs.toString(), new FileCreateCallback() {
            @Override
            public void onSuccess(String fileId) {
                // *If this last file has been created, then call the onCreationSuccess():
                onCreationSuccess();
            }

            @Override
            public void onFailure(String errorMessage) {
                // TODO Error: Teacher Configs file
                onCreationFail();
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
        if(!isConnected() || idList.isEmpty() || teacherId == null){
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
