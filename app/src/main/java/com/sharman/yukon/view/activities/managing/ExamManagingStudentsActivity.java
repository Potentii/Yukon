package com.sharman.yukon.view.activities.managing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.StudentPickerDialog;
import com.sharman.yukon.view.activities.util.DialogCallback;
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


    private String[] studentConfigsFileIdArray;
    private String teacherAnswerFileId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing_students);

        studentPickerDialog = new StudentPickerDialog();

        studentRVAdapter = new StudentRVAdapter(this, getCredential(), studentRVInfoVector, new OnStudentRVItemClickListener() {
            @Override
            public void onClick(StudentRVInfo studentRVInfo) {
                // *Acessing student inspection activity:
                Intent examManagingStudentInspectActivityIntent = new Intent(getApplicationContext(), ExamManagingStudentInspectActivity.class);
                examManagingStudentInspectActivityIntent.putExtra("gradeFileId", studentRVInfo.getStudentGradeFileId());
                examManagingStudentInspectActivityIntent.putExtra("grade", studentRVInfo.getStudentGrade());
                examManagingStudentInspectActivityIntent.putExtra("studentAnswerFileId", studentRVInfo.getStudentAnswerFileId());
                examManagingStudentInspectActivityIntent.putExtra("teacherAnswerFileId", teacherAnswerFileId);
                examManagingStudentInspectActivityIntent.putExtra("studentName", studentRVInfo.getStudentName());
                examManagingStudentInspectActivityIntent.putExtra("studentEmail", studentRVInfo.getStudentEmail());
                startActivity(examManagingStudentInspectActivityIntent);
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
            studentConfigsFileIdArray = getIntent().getExtras().getStringArray("studentConfigsFileIdArray");
            teacherAnswerFileId = getIntent().getExtras().getString("teacherAnswerFileId");

            //loadInfo();


        } catch (NullPointerException e){
            // TODO error
            e.printStackTrace();
        }


        /*
        new PlusIOHandler(getCredential()).listPersonContacts("me", new PersonContactsListCallback() {
            @Override
            public void onResult(List<Person> personList) {

            }
        });*/
    }





    private void loadInfo(){
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());

        for(int i=0; i<studentConfigsFileIdArray.length; i++) {
            driveIOHandler.readFile(studentConfigsFileIdArray[i], new FileReadCallback() {
                @Override
                public void onSuccess(final String content) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                StudentConfigs studentConfigs = new StudentConfigs(content);
                                System.out.println(">> STUDENT: " + studentConfigs.getStudent());
                                studentRVInfoVector.add(new StudentRVInfo(studentConfigs.getGradeFileId(), studentConfigs.getAnswersFileId(), studentConfigs.getStudent()));
                                studentRecyclerView.getAdapter().notifyDataSetChanged();
                            } catch (JSONException e){
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    // TODO error
                    exception.printStackTrace();
                }
            });
        }

    }





    private StudentPickerDialog studentPickerDialog;
    private List<String> idList = new ArrayList<>();

    @Override
    public void onPositive() {
        idList = studentPickerDialog.getIdList();

    }
    @Override
    public void onNegative() {
        studentPickerDialog.setIdList(idList);
    }
    @Override
    public void onNeutral() {}




    public void addStudentBtn_onClick(View view){
        if(!isConnected()){
            return;
        }

        studentPickerDialog.setDialogCallback(this);
        studentPickerDialog.setIdList(idList);

        studentPickerDialog.show(getFragmentManager(), "student_picker_dialog");
    }



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
