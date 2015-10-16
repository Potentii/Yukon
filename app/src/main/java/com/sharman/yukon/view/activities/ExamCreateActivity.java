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

import java.util.Date;

public class ExamCreateActivity extends GoogleRestConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create);
        //Button btn = (Button) findViewById(R.id.addQuestionsBtn);

    }


    public void examCreateBtn_onClick(View view){
        EditText examTitleIn = (EditText) findViewById(R.id.examTitleIn);
        EditText examSubjectIn = (EditText) findViewById(R.id.examSubjectIn);
        EditText examDeliverDateIn = (EditText) findViewById(R.id.examDeliverDateIn);

        // TODO adicionar date
        Exam exam = new Exam(
                examTitleIn.getText().toString(),
                new Date(),
                "",
                examSubjectIn.getText().toString(),
                new Question[]{});

        Intent examCreateIntent = new Intent(this, QuestionsCreateActivity.class);
        examCreateIntent.putExtra("exam", exam.toString());
        startActivity(examCreateIntent);
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
                System.out.println("fdsfdfsdfs");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
