package com.sharman.yukon.view.activities.managing;

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

import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;

import org.json.JSONException;

import java.text.SimpleDateFormat;

public class ExamManagingActivity extends GoogleRestConnectActivity {
    private TeacherConfigs teacherConfigs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing);
    }



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            // *Gets the teacherConfigs from intent:
            teacherConfigs = new TeacherConfigs(getIntent().getExtras().getString("teacherConfigs"));

            loadInfo();

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }


    private void loadInfo(){
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        final Activity activity = this;

        driveIOHandler.readFile(teacherConfigs.getExamFileId(), new FileReadCallback() {
            @Override
            public void onSuccess(String content) {
                try {
                    final Exam exam = new Exam(content);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView descriptionOut = (TextView) findViewById(R.id.descriptionOut);
                            descriptionOut.setText(exam.getDescription());

                            // *Building the toolbar:
                            View infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
                            new AndroidUtil(activity).fillInfoPhotoToolbar_GPlusImage(
                                    infoPhotoHeader,
                                    getCredential(),
                                    exam.getTeacherId(),
                                    exam.getTitle(),
                                    exam.getSubject(),
                                    new SimpleDateFormat("dd/MM/yyyy").format(exam.getDeliverDate())
                            );
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                //TODO error
                exception.printStackTrace();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listeners methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void studentsBtn_onClick(View view){
        Intent examManagingStudentsIntent = new Intent(this, ExamManagingStudentsActivity.class);
        examManagingStudentsIntent.putExtra("teacherConfigs", teacherConfigs.toString());
        /*
        examManagingStudentsIntent.putExtra("studentConfigsFileIdArray", teacherConfigs.getStudentConfigsFileIdArray());
        examManagingStudentsIntent.putExtra("teacherAnswerFileId", teacherConfigs.getCorrectAnswersFileId());
        */
        startActivity(examManagingStudentsIntent);
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * ActionBar methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_managing, menu);
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
