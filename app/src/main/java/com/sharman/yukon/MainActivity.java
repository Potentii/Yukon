package com.sharman.yukon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sharman.yukon.model.Exam;

public class MainActivity extends GoogleConnectActivity {

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
    }
}
