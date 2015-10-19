package com.sharman.yukon.view.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Exam;

import org.json.JSONException;

public class ExamAnsweringActivity extends GoogleRestConnectActivity {
    private Exam exam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answering);


        try {
            // TODO CHANGE: intent should pass the fileId of exam file
            // *Gets the exam from intent:
            exam = new Exam(getIntent().getExtras().getString("exam"));

            // *Sets the exam's title to the actionBar title:
            getSupportActionBar().setTitle(exam.getTitle());

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }


    // *Action of the "Start" button:
    private void examAnsweringStartActionButton_onClick(){
        Intent questionAnsweringIntent = new Intent(this, QuestionAnsweringActivity.class);
        questionAnsweringIntent.putExtra("questionIndex", 0);
        questionAnsweringIntent.putExtra("exam", exam.toString());
        startActivity(questionAnsweringIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_answering, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.examAnsweringStartActionButton:
                examAnsweringStartActionButton_onClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
