package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.sharman.yukon.R;
import com.sharman.yukon.model.AnswerBox;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Question;

import org.json.JSONException;


public class QuestionsCreateActivity extends GoogleRestConnectActivity {
    private Exam exam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_create);

        String examStr = getIntent().getExtras().getString("exam");
        try{
            exam = new Exam(examStr);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    public void addQuestionBtn_onClick(View view){
        updateExamQuestionArray();

        Intent addQuestionIntent = new Intent(this, QuestionsCreateActivity.class);
        addQuestionIntent.putExtra("exam", exam.toString());
        startActivity(addQuestionIntent);
        finish();
    }


    public void examCreateConfirmBtn_onClick(View view){
        updateExamQuestionArray();

        Intent examCreateConfirmIntent = new Intent(this, ExamCreateConfirmActivity.class);
        examCreateConfirmIntent.putExtra("exam", exam.toString());
        startActivity(examCreateConfirmIntent);
        finish();
    }


    /*
     * *Updates the exam object, adding the question to its question array:
     */
    public void updateExamQuestionArray(){
        EditText questionTitleIn = (EditText) findViewById(R.id.questionTitleIn);
        EditText questionWeightIn = (EditText) findViewById(R.id.questionWeightIn);
        AnswerBox answerBox = new AnswerBox();

        Question question = new Question(
                questionTitleIn.getText().toString(),
                Double.parseDouble(questionWeightIn.getText().toString()),
                answerBox);

        Question[] questionArrayOLD = exam.getQuestionArray();
        Question[] questionArrayNEW = new Question[questionArrayOLD.length + 1];

        for(int i=0; i<questionArrayOLD.length; i++){
            questionArrayNEW[i] = questionArrayOLD[i];
        }

        questionArrayNEW[questionArrayNEW.length - 1] = question;

        try {
            exam.setQuestionArray(questionArrayNEW);
        } catch (JSONException e){}
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.questionCreateShareActionButton:
                System.out.println("fdsfdfsdfs");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
