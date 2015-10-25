package com.sharman.yukon.view.activities.managing;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;

import org.json.JSONException;

public class ExamManagingStudentInspectActivity extends GoogleRestConnectActivity {
    private String gradeFileId;
    private Grade grade;
    private String studentAnswerFileId;
    private String teacherAnswerFileId;
    private String studentEmail;

    private TeacherAnswers teacherAnswers;
    private StudentAnswers studentAnswers;

    private TextView gradeOut;
    private Button acceptGradeBtn;

    private View actionBarView;
    private ImageView actionBarStudentImg;
    private TextView actionBarStudentName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing_student_inspect);

        actionBarView = getLayoutInflater().inflate(R.layout.action_info_photo, null);
        actionBarStudentImg     = (ImageView) actionBarView.findViewById(R.id.infoImg);
        actionBarStudentName    = (TextView) actionBarView.findViewById(R.id.primaryInfoOut);

        gradeOut = (TextView) findViewById(R.id.gradeOut);
        acceptGradeBtn = (Button) findViewById(R.id.acceptGradeBtn);

        try{
            getSupportActionBar().setCustomView(actionBarView);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }




    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            teacherAnswerFileId = getIntent().getExtras().getString("teacherAnswerFileId");
            gradeFileId = getIntent().getExtras().getString("gradeFileId");
            grade = new Grade(getIntent().getExtras().getString("grade"));
            studentAnswerFileId = getIntent().getExtras().getString("studentAnswerFileId");
            studentEmail = getIntent().getExtras().getString("studentEmail");

            actionBarStudentName.setText(getIntent().getExtras().getString("studentName"));

            double gradeGrade = grade.getGrade();
            gradeOut.setText(gradeGrade<0 ? "Not set" : Double.toString(gradeGrade));   // TODO alterar para xml de Strings
            acceptGradeBtn.setEnabled(!(gradeGrade<0));

            loadInfo();

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }




    // *Load all the info from Drive and Plus to display on screen:
    private void loadInfo(){
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        final PlusIOHandler plusIOHandler = new PlusIOHandler(getCredential());



        plusIOHandler.ReadPerson(studentEmail, new PersonReadCallback() {
            @Override
            public void onSuccess(final Person person) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionBarStudentName.setText(person.getDisplayName());
                    }
                });

                plusIOHandler.ReadPersonImg(person, new PersonImgReadCallback() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actionBarStudentImg.setImageBitmap(bitmap);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO error
                    }
                });


            }

            @Override
            public void onFailure(Exception exception) {
                // TODO error
            }
        });



        driveIOHandler.readFile(gradeFileId, new FileReadCallback() {
            @Override
            public void onSuccess(final String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            grade = new Grade(content);
                            double gradeGrade = grade.getGrade();
                            gradeOut.setText(gradeGrade<0 ? "Not set" : Double.toString(gradeGrade));   // TODO alterar para xml de Strings
                            acceptGradeBtn.setEnabled(!(gradeGrade < 0));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO error
            }
        });



        driveIOHandler.readFile(teacherAnswerFileId, new FileReadCallback() {
            @Override
            public void onSuccess(String content) {
                try {

                    teacherAnswers = new TeacherAnswers(content);

                    driveIOHandler.readFile(studentAnswerFileId, new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {

                                studentAnswers = new StudentAnswers(content);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        buildAnswersList();
                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            // TODO error
                        }
                    });
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO error
            }
        });
    }




    private void buildAnswersList(){
        //TODO build the answers list

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_managing_student_inspect, menu);
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
