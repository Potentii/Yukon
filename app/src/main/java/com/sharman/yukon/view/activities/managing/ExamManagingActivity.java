package com.sharman.yukon.view.activities.managing;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import android.support.v7.app.ActionBar.LayoutParams;

import org.json.JSONException;

import java.text.SimpleDateFormat;

public class ExamManagingActivity extends GoogleRestConnectActivity {
    private TeacherConfigs teacherConfigs;

    private View actionBarView;
    private ImageView actionBarTeacherImg;
    private TextView actionBarExamTitle;
    private TextView actionBarExamSubject;
    private TextView actionBarExamDeliveryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing);

        actionBarView = getLayoutInflater().inflate(R.layout.action_info_photo, null);
        actionBarTeacherImg         = (ImageView) actionBarView.findViewById(R.id.infoImg);
        actionBarExamTitle          = (TextView) actionBarView.findViewById(R.id.primaryInfoOut);
        actionBarExamSubject        = (TextView) actionBarView.findViewById(R.id.secondaryInfoOut);
        actionBarExamDeliveryDate   = (TextView) actionBarView.findViewById(R.id.tertiaryInfoOut);

        try{
            LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            getSupportActionBar().setCustomView(actionBarView, layout);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            // *Gets the teacherConfigs from intent:
            teacherConfigs = new TeacherConfigs(getIntent().getExtras().getString("teacherConfigs"));


            actionBarExamTitle.setText(teacherConfigs.getExamTitleCache());
            actionBarExamSubject.setText(teacherConfigs.getExamSubjectCache());
            actionBarExamDeliveryDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(teacherConfigs.getExamDeliveryDateCache()));


            final PlusIOHandler plusIOHandler = new PlusIOHandler(getCredential());
            plusIOHandler.ReadPerson(teacherConfigs.getTeacherIdCache(), new PersonReadCallback() {
                @Override
                public void onSuccess(Person person) {
                    plusIOHandler.ReadPersonImg(person, new PersonImgReadCallback() {
                        @Override
                        public void onSuccess(final Bitmap bitmap) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    actionBarTeacherImg.setImageBitmap(bitmap);
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



        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }











    public void studentsBtn_onClick(View view){
        Intent examManagingStudentsIntent = new Intent(this, ExamManagingStudentsActivity.class);
        examManagingStudentsIntent.putExtra("studentConfigsFileIdArray", teacherConfigs.getStudentConfigsFileIdArray());
        examManagingStudentsIntent.putExtra("teacherAnswerFileId", teacherConfigs.getCorrectAnswersFileId());
        startActivity(examManagingStudentsIntent);
    }



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
