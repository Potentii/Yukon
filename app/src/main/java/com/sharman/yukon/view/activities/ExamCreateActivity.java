package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.view.activities.dialog.DeliveryDateDialog;
import com.sharman.yukon.view.activities.util.DialogCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExamCreateActivity extends GoogleRestConnectActivity implements DialogCallback {

    private DeliveryDateDialog deliveryDateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create);
        deliveryDateDialog = new DeliveryDateDialog();
        deliveryDateDialog.setDialogCallback(this);
    }


    public void examDeliverDateIn_onCLick(View view){
        deliveryDateDialog.show(getSupportFragmentManager(), "delivery_date_dialog");
    }


    public void examCreateNextActionButton_onClick(){
        EditText examTitleIn = (EditText) findViewById(R.id.examTitleIn);
        EditText examSubjectIn = (EditText) findViewById(R.id.examSubjectIn);
        EditText examDeliverDateIn = (EditText) findViewById(R.id.examDeliverDateIn);

        Exam exam = new Exam(
                examTitleIn.getText().toString(),
                deliveryDateDialog.getDate(),
                "",
                examSubjectIn.getText().toString(),
                new Question[]{});

        Intent questionCreateIntent = new Intent(this, QuestionsCreateActivity.class);
        questionCreateIntent.putExtra("exam", exam.toString());
        startActivity(questionCreateIntent);
        finish();
    }


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



    @Override
    public void onPositive() {
        ((EditText) findViewById(R.id.examDeliverDateIn)).setText(new SimpleDateFormat("dd/MM/yyyy").format(deliveryDateDialog.getDate()));
    }

    @Override
    public void onNegative() {}

    @Override
    public void onNeutral() {}
}
