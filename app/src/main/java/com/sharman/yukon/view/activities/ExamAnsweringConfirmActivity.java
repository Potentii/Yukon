package com.sharman.yukon.view.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Exam;

import org.json.JSONException;

public class ExamAnsweringConfirmActivity extends GoogleRestConnectActivity {
    private Exam exam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answering_confirm);

        try {
            exam = new Exam(getIntent().getExtras().getString("exam"));
        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }


    // *Action of the "Send" button:
    private void examAnsweringConfirmSendActionButton_onClick(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_answering_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.examAnsweringConfirmSendActionButton:
                examAnsweringConfirmSendActionButton_onClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
