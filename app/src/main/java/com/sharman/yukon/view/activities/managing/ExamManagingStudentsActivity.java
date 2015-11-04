package com.sharman.yukon.view.activities.managing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.callback.MultipleFilesReadCallback;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.StudentPickerDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.StudentContact;
import com.sharman.yukon.view.activities.util.recycler.OnStudentRVItemClickListener;
import com.sharman.yukon.view.activities.util.recycler.StudentRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.StudentRVInfo;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ExamManagingStudentsActivity extends GoogleRestConnectActivity implements DialogCallback {
    final private Vector<StudentRVInfo> studentRVInfoVector = new Vector<>();
    private StudentRVAdapter studentRVAdapter;
    private RecyclerView studentRecyclerView;

    private TeacherConfigs teacherConfigs;
    private String teacherAnswersStr;

    private StudentPickerDialog studentPickerDialog;
    private List<StudentContact> contactList;
    private List<String> idList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing_students);

        studentPickerDialog = new StudentPickerDialog();

        contactList = new AndroidUtil(this).queryContacts();
        studentPickerDialog.setStudentContactList(contactList);


        studentRVAdapter = new StudentRVAdapter(this, getCredential(), studentRVInfoVector, contactList, new OnStudentRVItemClickListener() {
            @Override
            public void onClick(StudentRVInfo studentRVInfo) {
                // *Acessing student inspection activity:
                if(teacherAnswersStr != null || teacherConfigs != null) {
                    Intent examManagingStudentInspectActivityIntent = new Intent(getApplicationContext(), ExamManagingStudentInspectActivity.class);

                    examManagingStudentInspectActivityIntent.putExtra("gradeFileId", studentRVInfo.getStudentGradeFileId());
                    examManagingStudentInspectActivityIntent.putExtra("studentAnswerFileId", studentRVInfo.getStudentAnswerFileId());
                    examManagingStudentInspectActivityIntent.putExtra("teacherAnswers", teacherAnswersStr);
                    examManagingStudentInspectActivityIntent.putExtra("studentName", studentRVInfo.getStudentName());
                    examManagingStudentInspectActivityIntent.putExtra("studentEmail", studentRVInfo.getStudentEmail());
                    examManagingStudentInspectActivityIntent.putExtra("studentImageUri", studentRVInfo.getStudentImageUri());

                    startActivity(examManagingStudentInspectActivityIntent);
                }
            }
        });

        studentRecyclerView = (RecyclerView) findViewById(R.id.studentRecyclerView);
        studentRecyclerView.setAdapter(studentRVAdapter);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            teacherConfigs = new TeacherConfigs(getIntent().getExtras().getString("teacherConfigs"));

            if(teacherAnswersStr == null) {
                new DriveIOHandler(getCredential()).readFile(teacherConfigs.getCorrectAnswersFileId(), new FileReadCallback() {
                    @Override
                    public void onSuccess(String content) {
                        teacherAnswersStr = content;
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // TODO error
                        exception.printStackTrace();
                    }
                });
            }

            loadInfo();

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }



    private void loadInfo(){
        studentRVInfoVector.clear();
        idList.clear();
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        final Activity activity = this;

        driveIOHandler.readMultipleFiles(teacherConfigs.getStudentConfigsFileIdArray(), new MultipleFilesReadCallback() {
            @Override
            public void onSuccess(String[] contentArray) {
                for(String content : contentArray){
                    try{
                        StudentConfigs studentConfigs = new StudentConfigs(content);
                        studentRVInfoVector.add(new StudentRVInfo(
                                studentConfigs.getGradeFileId(),
                                studentConfigs.getAnswersFileId(),
                                studentConfigs.getStudent()
                        ));

                        idList.add(studentConfigs.getStudent());

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        studentRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                new AndroidUtil(activity).showToast("Error", Toast.LENGTH_SHORT);
            }
        });
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
    public void addStudentBtn_onClick(View view){
        if(!isConnected()){
            return;
        }

        studentPickerDialog.setDialogCallback(this);
        studentPickerDialog.setIdList(idList);

        studentPickerDialog.show(getFragmentManager(), "student_picker_dialog");
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * ActionBar methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_managing_students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
