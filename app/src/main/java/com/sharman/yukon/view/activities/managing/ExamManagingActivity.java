package com.sharman.yukon.view.activities.managing;

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
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;

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
            // TODO alterar para ler Exam e parar de usar cache nos arquivos
            teacherConfigs = new TeacherConfigs(getIntent().getExtras().getString("teacherConfigs"));


            final View infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
            final ImageView infoImg           = (ImageView) infoPhotoHeader.findViewById(R.id.infoImg);
            final TextView primaryInfoOut     = (TextView) infoPhotoHeader.findViewById(R.id.primaryInfoOut);
            final TextView secondaryInfoOut   = (TextView) infoPhotoHeader.findViewById(R.id.secondaryInfoOut);
            final TextView tertiaryInfoOut    = (TextView) infoPhotoHeader.findViewById(R.id.tertiaryInfoOut);

            primaryInfoOut.setText(teacherConfigs.getExamTitleCache());
            secondaryInfoOut.setText(teacherConfigs.getExamSubjectCache());
            tertiaryInfoOut.setText(new SimpleDateFormat("dd/MM/yyyy").format(teacherConfigs.getExamDeliveryDateCache()));

            final PlusIOHandler plusIOHandler = new PlusIOHandler(getCredential());
            plusIOHandler.readPerson(teacherConfigs.getTeacherIdCache(), new PersonReadCallback() {
                @Override
                public void onSuccess(Person person) {
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


            final TextView descriptionOut = (TextView) findViewById(R.id.descriptionOut);
            descriptionOut.setText("");

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
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
