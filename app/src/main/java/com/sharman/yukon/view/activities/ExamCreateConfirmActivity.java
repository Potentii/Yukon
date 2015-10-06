package com.sharman.yukon.view.activities;

import android.os.Bundle;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.sharman.yukon.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.CreateFolderCallback;
import com.sharman.yukon.io.drive.DriveFileOffline;
import com.sharman.yukon.io.drive.DriveFolderHandler;
import com.sharman.yukon.io.drive.UploadCallback;
import com.sharman.yukon.model.Exam;

public class ExamCreateConfirmActivity extends GoogleConnectActivity {
    private Exam exam;
    private int currentOperationIndex;
    private boolean[] operationStatusArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create_confirm);

        //TODO escolher alunos.


        String[] studentPersonIdArray = new String[0]; //
        operationStatusArray = new boolean[8 + (studentPersonIdArray.length * 3)];
        currentOperationIndex = 0;
        //confirmCreation(studentPersonIdArray);


        // TODO Mudar para um código só, sem modularização
        // TODO Ou ao menos criar os arquivos de file somente depois de criar todos os outros, dentro de um callback
    }


    // *Sets the status of the last operation and call the onSuccess or onFail when all operations finished
    private synchronized void operationReport(boolean wasSuccessful){
        if(operationStatusArray.length<1){
            onCreationSuccess();
            return;
        }
        operationStatusArray[currentOperationIndex] = wasSuccessful;
        currentOperationIndex++;

        if(currentOperationIndex == operationStatusArray.length){
            for(int i=0; i<operationStatusArray.length; i++){
                if(operationStatusArray[i] == false){
                    onCreationFail();
                    return;
                }
            }
            onCreationSuccess();
        }
    }

    private void onCreationFail(){

    }
    private void onCreationSuccess(){

    }

    private void confirmCreation(final String[] studentPersonIdArray){
        MetadataChangeSet examRootFolderMetaData = new MetadataChangeSet.Builder()
                .setTitle(exam.getTitle())
                .build();

        // TODO Alterar aqui para o usuario poder escolher a pasta
        Drive.DriveApi.getRootFolder(googleApiClient).createFolder(googleApiClient, examRootFolderMetaData).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                if (!driveFolderResult.getStatus().isSuccess()) {
                    operationReport(false);
                    return;
                }
                createStudentsAnswerFolder(driveFolderResult.getDriveFolder(), studentPersonIdArray);
                createStudentFilesFolder(driveFolderResult.getDriveFolder());
                createTeacherFilesFolder(driveFolderResult.getDriveFolder());
                operationReport(true);
            }
        });
    }



    // *Creating the StudentsAnswer folder:
    private void createStudentsAnswerFolder(DriveFolder parentFolder, final String[] studentPersonIdArray){
        MetadataChangeSet folderMetaData = new MetadataChangeSet.Builder()
                .setTitle("StudentsAnswer")
                .build();
        new DriveFolderHandler(googleApiClient).create(parentFolder, folderMetaData, new CreateFolderCallback() {
            @Override
            public void onComplete(DriveFolder driveFolder) {
                for(int i=0; i<studentPersonIdArray.length; i++){
                    createStudentFolder(driveFolder, studentPersonIdArray[i]);
                }
                operationReport(true);
            }

            @Override
            public void onFail(String errorMessage) {
                operationReport(false);
            }
        });
    }



    private void createStudentFolder(DriveFolder parentFolder, final String studentPersonId){
        MetadataChangeSet folderMetaData = new MetadataChangeSet.Builder()
                .setTitle("Student")
                .build();
        new DriveFolderHandler(googleApiClient).create(parentFolder, folderMetaData, new CreateFolderCallback() {
            @Override
            public void onComplete(DriveFolder driveFolder) {
                // *Creating the Answer file:
                new DriveFileOffline(googleApiClient).upload(
                        driveFolder,
                        "Answer",
                        "", //TODO
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                operationReport(true);
                            }
                            @Override
                            public void onFail(String errorMessage) {
                                operationReport(false);
                            }
                        });

                // *Creating the Grade file:
                new DriveFileOffline(googleApiClient).upload(
                        driveFolder,
                        "Grade",
                        "", //TODO
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                operationReport(true);
                            }
                            @Override
                            public void onFail(String errorMessage) {
                                operationReport(false);
                            }
                        });

                // TODO compartilhar com o aluno.
                operationReport(true);
            }

            @Override
            public void onFail(String errorMessage) {
                operationReport(false);
            }
        });
    }



    // *Creating the StudentFiles folder:
    private void createStudentFilesFolder(DriveFolder parentFolder){
        MetadataChangeSet folderMetaData = new MetadataChangeSet.Builder()
                .setTitle("StudentFiles")
                .build();
        new DriveFolderHandler(googleApiClient).create(parentFolder, folderMetaData, new CreateFolderCallback() {
            @Override
            public void onComplete(DriveFolder driveFolder) {
                // *Creating the Exam file:
                new DriveFileOffline(googleApiClient).upload(
                        driveFolder,
                        "Exam",
                        exam.toString(),
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                operationReport(true);
                            }
                            @Override
                            public void onFail(String errorMessage) {
                                // TODO error
                                operationReport(false);
                            }
                        });

                // *Creating the StudentConfigurations file:
                new DriveFileOffline(googleApiClient).upload(
                        driveFolder,
                        "StudentConfigurations",
                        "", //TODO
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                operationReport(true);
                            }
                            @Override
                            public void onFail(String errorMessage) {
                                // TODO error
                                operationReport(false);
                            }
                        });
                operationReport(true);
            }

            @Override
            public void onFail(String errorMessage) {
                // TODO error
                operationReport(false);
            }
        });
    }



    // *Creating the TeacherFiles folder:
    private void createTeacherFilesFolder(DriveFolder parentFolder){
        MetadataChangeSet folderMetaData = new MetadataChangeSet.Builder()
                .setTitle("TeacherFiles")
                .build();
        new DriveFolderHandler(googleApiClient).create(parentFolder, folderMetaData, new CreateFolderCallback() {
            @Override
            public void onComplete(DriveFolder driveFolder) {
                // *Creating the CorrectAnswers file:
                new DriveFileOffline(googleApiClient).upload(
                        driveFolder,
                        "CorrectAnswers",
                        "", //TODO
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                operationReport(true);
                            }
                            @Override
                            public void onFail(String errorMessage) {
                                // TODO error
                                operationReport(false);
                            }
                        });

                // *Creating the TeacherConfigurations file:
                new DriveFileOffline(googleApiClient).upload(
                        driveFolder,
                        "TeacherConfigurations",
                        "", //TODO
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                operationReport(true);
                            }
                            @Override
                            public void onFail(String errorMessage) {
                                // TODO error
                                operationReport(false);
                            }
                        });
                operationReport(true);
            }

            @Override
            public void onFail(String errorMessage) {
                // TODO error
                operationReport(false);
            }
        });
    }
}
