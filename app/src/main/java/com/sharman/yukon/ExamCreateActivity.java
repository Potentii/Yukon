package com.sharman.yukon;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Question;

import java.util.Date;

public class ExamCreateActivity extends GoogleConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_create);
        Button btn = (Button) findViewById(R.id.addQuestionsBtn);
    }
    public void callQuestionsCreateActivity(View view){

        Intent iCallQuestionsCreateActivity = new Intent(this, QuestionsCreateActivity.class);

        final EditText edtNameExamInput = (EditText) findViewById(R.id.nameExamInput);
        String nameExam = edtNameExamInput.getText().toString();
        final EditText edtSubjectExam = (EditText) findViewById(R.id.subjectExamInput);
        String subjectExam = edtSubjectExam.getText().toString();
        final EditText edtDateExam = (EditText) findViewById(R.id.dateExamInput);
        String dateExam = edtDateExam.getText().toString();

        Exam exam = new Exam(nameExam, new Date(), "", subjectExam, new Question[]{});

        iCallQuestionsCreateActivity.putExtra("exam", exam.toString());
        startActivity(iCallQuestionsCreateActivity);
        finish();
    }

}
