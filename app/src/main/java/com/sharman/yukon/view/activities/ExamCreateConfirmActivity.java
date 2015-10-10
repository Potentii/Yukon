package com.sharman.yukon.view.activities;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.Permission;
import com.sharman.yukon.DriveFileOnline;
import com.sharman.yukon.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.CreateFolderCallback;
import com.sharman.yukon.io.drive.DriveFileOffline;
import com.sharman.yukon.io.drive.DriveFolderHandler;
import com.sharman.yukon.io.drive.UploadCallback;
import com.sharman.yukon.io.drive.util.PermissionStruct;
import com.sharman.yukon.model.Exam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



public class ExamCreateConfirmActivity extends GoogleConnectActivity {
    private Exam exam;

    private StudentConfigFilePair[] studentConfigFilePairArray;
    private boolean onCreationFailOrSuccessCalled;
    private int studentFoldersCreated;

    private DriveId examDriveId;
    private DriveId correctAnswersDriveId;
    private DriveFolder examRootDriveFolder;
    private DriveFolder teacherFilesDriveFolder;
    private DriveFolder studentFilesDriveFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create_confirm);

        // *Queries the contacts's e-mail in a new thread:
        //TODO colocar thread pra ver se vai mais rapido
        new Runnable(){
            @Override
            public void run() {
                List<String> emailList = queryContactsEmail();
                String[] emailArray = new String[emailList.size()];
                for(int i=0; i<emailList.size(); i++){
                    emailArray[i] = emailList.get(i);
                }
                onQueryContactResult(emailArray);
            }
        }.run();


    }




    @Override
    public void onConnected(Bundle bundle){

        onCreationFailOrSuccessCalled = false;
        studentFoldersCreated = 0;
        // TODO pegar Exam da intent
        /*
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
        */


        //TODO escolher alunos.
        /*
        studentConfigFilePairArray = new StudentConfigFilePair[0]; // TODO alterar para a quantidade de alunos selecionados pelo "user picker"
        for(int i=0; i< studentConfigFilePairArray.length; i++){
            studentConfigFilePairArray[i] = new StudentConfigFilePair("" , null);// TODO get from "user picker"
        }
        */

        Button shareAndCreateExamBtn = (Button) findViewById(R.id.shareAndCreateExamBtn);
        shareAndCreateExamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                createExamOnDrive();
            }
        });

        stub();
        //createExamOnDrive();

    }

    // *Callback for the query thread result:
    public void onQueryContactResult(String[] emailArray){/*
        for(int i=0; i<emailArray.length; i++){
            System.out.println(emailArray[i]);
        }*/
        AutoCompleteTextView studentIn = (AutoCompleteTextView) findViewById(R.id.studentIn);
        ArrayAdapter<String> studentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emailArray);
        studentIn.setAdapter(studentAdapter);
    }

    // *Queries the contact's e-mail:
    private List<String> queryContactsEmail(){
        List<String> emailList = new ArrayList<String>();
        HashSet<String> emailHash = new HashSet<String>();
        Context context = getApplicationContext();
        ContentResolver cr = context.getContentResolver();

        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };

        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";

        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);

        if(cur.moveToFirst()) {
            do{
                //String name = cur.getString(1);
                String email = cur.getString(3);

                // *Only unique e-mails:
                if(emailHash.add(email.toLowerCase())) {
                    emailList.add(email);
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return emailList;
    }



    private void stub(){
        studentConfigFilePairArray = new StudentConfigFilePair[5];
        for(int i=0; i< studentConfigFilePairArray.length; i++){
            studentConfigFilePairArray[i] = new StudentConfigFilePair("userIdPlaceholder", null);
        }
    }


    private synchronized void onCreationSuccess(){
        if(!onCreationFailOrSuccessCalled) {
            onCreationFailOrSuccessCalled = true;
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
    }
    private synchronized void onCreationFail(){
        if(!onCreationFailOrSuccessCalled) {
            onCreationFailOrSuccessCalled = true;
            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
        }
    }





    private void createExamOnDrive(){
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *ExamRoot folder creation:
        MetadataChangeSet examRootFolderMetaData = new MetadataChangeSet.Builder()
                //.setTitle(exam.getTitle())
                .setTitle("TituloParaAFolderDeExam") // TODO alterar para getTitle
                .build();
        new DriveFolderHandler(getGoogleApiClient()).create(Drive.DriveApi.getRootFolder(getGoogleApiClient()), examRootFolderMetaData, new CreateFolderCallback(){
            @Override
            public void onComplete(DriveFolder driveFolder) {
                examRootDriveFolder = driveFolder;

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *TeacherFiles folder creation:
                MetadataChangeSet teacherFilesFolderMetaData = new MetadataChangeSet.Builder()
                        .setTitle("TeacherFiles")
                        .build();
                new DriveFolderHandler(getGoogleApiClient()).create(driveFolder, teacherFilesFolderMetaData, new CreateFolderCallback(){
                    @Override
                    public void onComplete(DriveFolder driveFolder) {
                        teacherFilesDriveFolder = driveFolder;

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *CorrectAnswers file creation:
                        new DriveFileOffline(getGoogleApiClient()).upload(
                                driveFolder,
                                "CorrectAnswers",
                                "", // TODO generate CorrectAnswers file content
                                EMimeType.JSON.getMimeType(),
                                new UploadCallback() {
                                    @Override
                                    public void onComplete(DriveId driveId) {
                                        correctAnswersDriveId = driveId;
                                    }

                                    @Override
                                    public void onFail(String errorMessage) {
                                        // TODO error CorrectAnswers file
                                        onCreationFail();
                                    }
                                });
                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        // TODO error TeacherFiles folder
                        onCreationFail();
                    }
                });

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *



                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *Exam file creation:
                new DriveFileOffline(getGoogleApiClient()).upload(
                        driveFolder,
                        "Exam",
                        //exam.toString(),
                        "", // TODO generate Exam file content
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                examDriveId = driveId;

                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *StudentFiles folder creation:
                                MetadataChangeSet studentFilesFolderMetaData = new MetadataChangeSet.Builder()
                                        .setTitle("StudentFiles")
                                        .build();
                                new DriveFolderHandler(getGoogleApiClient()).create(examRootDriveFolder, studentFilesFolderMetaData, new CreateFolderCallback() {
                                    @Override
                                    public void onComplete(DriveFolder driveFolder) {
                                        studentFilesDriveFolder = driveFolder;

                                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                        // *Students folders creation:
                                        for (int i = 0; i < studentConfigFilePairArray.length; i++) {
                                            createEachStudentFolder(driveFolder, studentConfigFilePairArray[i]);
                                        }
                                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                    }

                                    @Override
                                    public void onFail(String errorMessage) {
                                        // TODO error StudentFiles folder
                                        onCreationFail();
                                    }
                                });
                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                            }

                            @Override
                            public void onFail(String errorMessage) {
                                // TODO error Exam file
                                onCreationFail();
                            }
                        });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
            }

            @Override
            public void onFail(String errorMessage) {
                // TODO error ExamRoot folder
                onCreationFail();
            }
        });
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *


    }




    private void createEachStudentFolder(DriveFolder parentFolder, final StudentConfigFilePair studentConfigFilePair){
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *Student folder creation:
        MetadataChangeSet studentFolderMetaData = new MetadataChangeSet.Builder()
                .setTitle("Student")
                .build();
        new DriveFolderHandler(getGoogleApiClient()).create(parentFolder, studentFolderMetaData, new CreateFolderCallback() {
            @Override
            public void onComplete(final DriveFolder studentDriveFolder) {

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *Answers file creation:
                new DriveFileOffline(getGoogleApiClient()).upload(
                        studentDriveFolder,
                        "Answers",
                        "", // TODO generate Answers file content
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback(){
                            @Override
                            public void onComplete(final DriveId answersDriveId) {

                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *Grade file creation:
                                new DriveFileOffline(getGoogleApiClient()).upload(
                                        studentDriveFolder,
                                        "Grade",
                                        "", // TODO generate Grade file content
                                        EMimeType.JSON.getMimeType(),
                                        new UploadCallback() {
                                            @Override
                                            public void onComplete(final DriveId gradeDriveId) {

                                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                                // *Configs file creation:
                                                new DriveFileOffline(getGoogleApiClient()).upload(
                                                        studentDriveFolder,
                                                        "Configs",
                                                        "", // TODO generate Configs file content
                                                        EMimeType.JSON.getMimeType(),
                                                        new UploadCallback() {
                                                            @Override
                                                            public void onComplete(final DriveId configsDriveId) {
                                                                studentConfigFilePair.setConfigFile(configsDriveId);

                                                                Drive.DriveApi.requestSync(getGoogleApiClient()).setResultCallback(new ResultCallback<Status>() {
                                                                    @Override
                                                                    public void onResult(Status status) {
                                                                        new Share(
                                                                                getCredential(),
                                                                                answersDriveId,
                                                                                new PermissionStruct[]{
                                                                                        new PermissionStruct("potentii@gmail.com", "user", "reader")
                                                                                }).execute();

                                                                        new Share(
                                                                                getCredential(),
                                                                                gradeDriveId,
                                                                                new PermissionStruct[]{
                                                                                        new PermissionStruct("potentii@gmail.com", "user", "reader")
                                                                                }).execute();

                                                                        new Share(
                                                                                getCredential(),
                                                                                configsDriveId,
                                                                                new PermissionStruct[]{
                                                                                        new PermissionStruct("potentii@gmail.com", "user", "reader")
                                                                                }).execute();
                                                                    }
                                                                });



                                                                addStudentFolderCreationFlag(true);
                                                            }

                                                            @Override
                                                            public void onFail(String errorMessage) {
                                                                // TODO error Configs file
                                                                addStudentFolderCreationFlag(false);
                                                            }
                                                        });
                                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                            }

                                            @Override
                                            public void onFail(String errorMessage) {
                                                // TODO error Grade file
                                                addStudentFolderCreationFlag(false);
                                            }
                                        });
                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                            }

                            @Override
                            public void onFail(String errorMessage) {
                                // TODO error Answers file
                                addStudentFolderCreationFlag(false);
                            }
                        });
                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
            }

            @Override
            public void onFail(String errorMessage) {
                // TODO error Student folder
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
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
        // *Teacher Configs file creation:
        new DriveFileOffline(getGoogleApiClient()).upload(
                teacherFilesDriveFolder,
                "Configs",
                "", // TODO generate Teacher Configs file content
                EMimeType.JSON.getMimeType(),
                new UploadCallback() {
                    @Override
                    public void onComplete(DriveId driveId) {
                        // *If this last file has been created, then call the onCreationSuccess():
                        onCreationSuccess();
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        // TODO error Teacher Configs file
                        onCreationFail();
                    }
                });
        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *

    }






    private class Share extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.drive.Drive service = null;
        private DriveId driveId;
        private PermissionStruct[] permissionStruct;

        public Share(GoogleAccountCredential credential, DriveId driveId, PermissionStruct[] permissionStruct) {
            this.driveId = driveId;
            this.permissionStruct = permissionStruct;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            service = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Yukon")
                    .build();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(driveId == null){
                System.out.println("NULL");
            }
            String fileId = driveId.getResourceId();

            JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
                @Override
                public void onSuccess(Permission permission, HttpHeaders responseHeaders) {
                    System.out.println("Permission added to file");
                }

                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                    System.out.println("Error Message: " + e.getMessage());
                }
            };

            BatchRequest batch = service.batch();
            for(int i=0; i<permissionStruct.length; i++) {
                Permission permission = new Permission();
                permission.setValue(permissionStruct[i].getValue());
                permission.setType(permissionStruct[i].getType());
                permission.setRole(permissionStruct[i].getRole());
                try {
                    System.out.println("1");
                    service.permissions().insert(fileId, permission).queue(batch, callback);
                    System.out.println("2");
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

            try {
                System.out.println("3");
                batch.execute();
            } catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }

}
