package com.sharman.yukon.view.activities.creation;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.DeliveryDateDialog;
import com.sharman.yukon.view.activities.util.DialogCallback;

import java.text.SimpleDateFormat;

public class ExamCreateActivity extends GoogleRestConnectActivity implements DialogCallback {

    private DeliveryDateDialog deliveryDateDialog;


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create);





        deliveryDateDialog = new DeliveryDateDialog();
        deliveryDateDialog.setDialogCallback(this);


        try {
            /*
            View view = findViewById(R.id.action_info_photo);
            TextView primary = (TextView) view.findViewById(R.id.primaryInfoOut);
            primary.setText("Teste maroto");
            */

            View view = getLayoutInflater().inflate(R.layout.action_info_photo, null);
            TextView primary = (TextView) view.findViewById(R.id.primaryInfoOut);
            primary.setText("Teste maroto");


            /*
            getActionBar().setCustomView(view);
            getActionBar().setDisplayShowCustomEnabled(true);
            */

            /*
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(view);
            actionBar.setLogo(R.drawable.ic_cast_dark);
            actionBar.setDisplayShowTitleEnabled(true);


            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.setHomeButtonEnabled(false);
*/
            /*
            //android.support.v7.app.ActionBar.LayoutParams layout = new android.support.v7.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.FILL_PARENT, android.support.v7.app.ActionBar.LayoutParams.FILL_PARENT);
            getSupportActionBar().setCustomView(view);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().set
            */
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void examDeliveryDateIn_onCLick(View view){
        deliveryDateDialog.show(getFragmentManager(), "delivery_date_dialog");
    }


    public void examCreateNextActionButton_onClick(){
        EditText examTitleIn = (EditText) findViewById(R.id.examTitleIn);
        EditText examSubjectIn = (EditText) findViewById(R.id.examSubjectIn);
        EditText examDeliveryDateIn = (EditText) findViewById(R.id.examDeliveryDateIn);

        Exam exam = new Exam(
                examTitleIn.getText().toString(),
                deliveryDateDialog.getDate(),
                "",
                examSubjectIn.getText().toString(),
                new Question[]{});

        Intent questionCreateIntent = new Intent(this, QuestionsCreateActivity.class);
        questionCreateIntent.putExtra("exam", exam.toString());
        startActivity(questionCreateIntent);
        //finish();
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
        ((EditText) findViewById(R.id.examDeliveryDateIn)).setText(new SimpleDateFormat("dd/MM/yyyy").format(deliveryDateDialog.getDate()));
    }

    @Override
    public void onNegative() {}

    @Override
    public void onNeutral() {}
}
