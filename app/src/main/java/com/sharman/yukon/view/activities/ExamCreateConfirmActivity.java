package com.sharman.yukon.view.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.services.plus.model.Person;
import com.sharman.yukon.EMimeType;
import com.sharman.yukon.R;

import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileCreateCallback;
import com.sharman.yukon.io.drive.callback.FileShareCallback;
import com.sharman.yukon.io.drive.callback.FolderCreateCallback;
import com.sharman.yukon.io.drive.util.PermissionStruct;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.util.StudentConfigFilePair;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;



public class ExamCreateConfirmActivity extends GoogleRestConnectActivity {
    private Exam exam;

    // *StudentPicker variables:
    private LayoutInflater layoutInflater;
    private LinearLayout studentPickerDiv;
    private List<View> studentPickerList = new ArrayList<>();
    private ArrayAdapter<String> emailsAdapter;
    private StudentConfigFilePair[] studentConfigFilePairArray;

    // *Exam creation variables:
    private String examFileId;
    private String correctAnswersFileId;
    private String examRootFolderId;
    private String teacherFilesFolderId;
    private String studentFilesFolderId;
    private boolean onCreationFailOrSuccessCalled;
    private int studentFoldersCreated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create_confirm);

        onCreationFailOrSuccessCalled = false;
        studentFoldersCreated = 0;
        layoutInflater = LayoutInflater.from(this);
        studentPickerDiv = (LinearLayout) findViewById(R.id.studentPickerDiv);


        // *Queries the contacts's e-mail:
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


        try {
            // TODO substituir pelo real:
            this.exam = new Exam("Título para o exame", new Date(), "", "Matemática", new Question[]{});
            /*
            String examStr = getIntent().getExtras().getString("exam");
            exam = new Exam(examStr);
            */

            // *Execute the creation and share of the Exam:
            Button shareAndCreateExamBtn = (Button) findViewById(R.id.shareAndCreateExamBtn);
            shareAndCreateExamBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    studentConfigFilePairArray = new StudentConfigFilePair[studentPickerList.size()-1];
                    for (int i = 0; i < studentConfigFilePairArray.length; i++) {
                        AutoCompleteTextView studentPicker = (AutoCompleteTextView) studentPickerList.get(i).findViewById(R.id.studentPickerIn);
                        studentConfigFilePairArray[i] = new StudentConfigFilePair(studentPicker.getText().toString(), "");
                    }

                    new PlusIOHandler(getCredential()).ReadPerson("me", new PersonReadCallback() {
                        @Override
                        public void onSuccess(Person person) {
                            try {
                                exam.setTeacherId(person.getId());
                                createExamOnDrive();
                            }catch (JSONException e){
                                e.printStackTrace();
                                onFailure(e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            onCreationFail();
                        }
                    });

                }
            });

        } catch (NullPointerException /*| JSONException*/ e){
            // TODO error
            e.printStackTrace();
            System.out.println("Erro ao tentar recuperar 'Exam'");
        }
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Contacts query methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
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


    // *Callback for the query thread result:
    public void onQueryContactResult(String[] emailArray){
        emailsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emailArray);
        addStudentPicker();
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * StudentPicker methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Creates a new "studentPicker", adds to the list, and sets to it the properly listener:
    private void addStudentPicker(){
        final View studentPicker = layoutInflater.inflate(R.layout.student_picker, null);
        AutoCompleteTextView studentPickerIn = (AutoCompleteTextView) studentPicker.findViewById(R.id.studentPickerIn);
        studentPickerIn.setAdapter(emailsAdapter);
        studentPickerIn.addTextChangedListener(new TextWatcher(){
            private boolean hasCreatedView = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if(!hasCreatedView && charSequence.length()>0){
                    addStudentPicker();
                    hasCreatedView = true;
                } else if(hasCreatedView && charSequence.length()==0){
                    hasCreatedView = false;
                    removeStudentPicker(studentPicker);
                }
            }
        });
        studentPickerDiv.addView(studentPicker);
        studentPickerList.add(studentPicker);
    }


    // *Remove the last "studentPicker" from the list and from the interface:
    private void removeStudentPicker(View studentPicker){
        studentPickerDiv.removeView(studentPicker);
        studentPickerList.remove(studentPicker);
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

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private synchronized void onCreationFail(){
        System.out.println("Creation Fail CALLED");
        if (!onCreationFailOrSuccessCalled) {
            onCreationFailOrSuccessCalled = true;

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void createExamOnDrive(){
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
                        TeacherAnswers teacherAnswers = new TeacherAnswers();

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
                StudentAnswers studentAnswers = new StudentAnswers();

                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                // *Answers file creation:
                new DriveIOHandler(getCredential()).createFile(studentFolderId, "Answers", "", EMimeType.JSON.getMimeType(), studentAnswers.toString(), new FileCreateCallback() {
                    @Override
                    public void onSuccess(final String answersFileId) {
                        Grade grade = new Grade(0.0);

                        // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                        // *Grade file creation:
                        new DriveIOHandler(getCredential()).createFile(studentFolderId, "Grade", "", EMimeType.JSON.getMimeType(), grade.toString(), new FileCreateCallback() {
                            @Override
                            public void onSuccess(final String gradeFileId) {
                                StudentConfigs studentConfigs = new StudentConfigs(gradeFileId, answersFileId, examFileId);

                                // * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- * ---------- *
                                // *Configs file creation:
                                new DriveIOHandler(getCredential()).createFile(studentFolderId, "Configs", "Student's configuration file", EMimeType.STUDENT_CONFIG.getMimeType(), studentConfigs.toString(), new FileCreateCallback() {
                                    @Override
                                    public void onSuccess(final String configsFileId) {
                                        studentConfigFilePair.setConfigFileId(configsFileId);

                                        new DriveIOHandler(getCredential()).shareFile(answersFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "writer")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {}

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                onCreationFail();
                                            }
                                        });

                                        new DriveIOHandler(getCredential()).shareFile(gradeFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "reader")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {}

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                onCreationFail();
                                            }
                                        });

                                        new DriveIOHandler(getCredential()).shareFile(configsFileId, null, new PermissionStruct[]{new PermissionStruct(studentConfigFilePair.getUserId(), type, "reader")}, new FileShareCallback() {
                                            @Override
                                            public void onSuccess() {}

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
        for(int i=0; i<studentConfigsFileIdArray.length; i++){
            studentConfigsFileIdArray[i] = studentConfigFilePairArray[i].getConfigFileId();
        }

        TeacherConfigs teacherConfigs = new TeacherConfigs(studentConfigsFileIdArray, correctAnswersFileId, examFileId);

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
    */
}
