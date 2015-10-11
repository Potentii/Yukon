package com.sharman.yukon.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sharman.yukon.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.addQuestionsBtn);
    }

    public void callActivityExam(View view) {
        Intent iCallActivityExam = new Intent(this, ExamCreateActivity.class);
        startActivity(iCallActivityExam);
        finish();

        Button btn = (Button)findViewById(R.id.newExamBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Hello world", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ExamCreateConfirmActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
