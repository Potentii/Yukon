package com.sharman.yukon.view.activities.creation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.DeliveryDateDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;

import java.text.SimpleDateFormat;

public class ExamCreateActivity extends GoogleRestConnectActivity implements DialogCallback {
    private Exam exam;
    private DeliveryDateDialog deliveryDateDialog;

    private EditText examTitleIn;
    private EditText examDescriptionIn;
    private EditText examSubjectIn;
    private EditText examDeliveryDateIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create);


        deliveryDateDialog = new DeliveryDateDialog();
        deliveryDateDialog.setDialogCallback(this);


        examTitleIn = (EditText) findViewById(R.id.examTitleIn);
        examDescriptionIn = (EditText) findViewById(R.id.examDescriptionIn);
        examSubjectIn = (EditText) findViewById(R.id.examSubjectIn);
        examDeliveryDateIn = (EditText) findViewById(R.id.examDeliveryDateIn);
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * DialogCallback methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public void onPositive() {
        examDeliveryDateIn.setText(new SimpleDateFormat("dd/MM/yyyy").format(deliveryDateDialog.getDate()));
    }

    @Override
    public void onNegative() {

    }

    @Override
    public void onNeutral() {

    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listeners methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void examCreateNextActionButton_onClick(){
        if(examTitleIn.getText().toString().trim().length()==0
                || examDescriptionIn.getText().toString().trim().length()==0
                || examSubjectIn.getText().toString().trim().length()==0
                || examDeliveryDateIn.getText().toString().trim().length()==0){
            new AndroidUtil(this).showToast("Invalid fields", Toast.LENGTH_SHORT);
            return;
        }

        exam = new Exam(
                examTitleIn.getText().toString(),
                examDescriptionIn.getText().toString(),
                deliveryDateDialog.getDate(),
                "",
                examSubjectIn.getText().toString(),
                new Question[]{});

        Intent questionCreateIntent = new Intent(this, QuestionsCreateActivity.class);
        questionCreateIntent.putExtra("exam", exam.toString());
        startActivity(questionCreateIntent);
    }

    public void examDeliveryDateIn_onCLick(View view){
        deliveryDateDialog.show(getFragmentManager(), "delivery_date_dialog");
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * ActionBar methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exam_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.examCreateNextActionButton:
                examCreateNextActionButton_onClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
