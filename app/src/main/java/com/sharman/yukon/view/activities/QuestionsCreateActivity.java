package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
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

        // Criando pacote de dados da Name Exam "pegando da intent
        String examStr = getIntent().getExtras().getString("exam");
        try{
            exam = new Exam(examStr);
        }catch (JSONException e){
        }


    }

    public void callMyself(View view){

        Intent iCallsMyself = new Intent(this, QuestionsCreateActivity.class);
        updateVectorQuestion();

        iCallsMyself.putExtra("exam", exam.toString());
        startActivity(iCallsMyself);
        finish();
    }
    public void callExamCreateConfirmActivity(View view){

        Intent iCallsMyself = new Intent(this, ExamCreateConfirmActivity.class);
        updateVectorQuestion();

        iCallsMyself.putExtra("exam", exam.toString());
        startActivity(iCallsMyself);
        finish();
    }

    public void updateVectorQuestion(){

        final EditText edtquestionQuestionInput = (EditText) findViewById(R.id.questionQuestionsInput);
        String questionQuestion = edtquestionQuestionInput.getText().toString();
        final EditText edtweightQuestionsInput = (EditText) findViewById(R.id.weightQuestionsInput);
        //Lembrando o Peso é Bundle ou float.
        String weightQuestionsInput = edtweightQuestionsInput.getText().toString();
        //instanciei a classe AnswerBox para poder usa-la.
        AnswerBox answerBox = new AnswerBox();
        Question question = new Question(questionQuestion, Double.parseDouble(weightQuestionsInput), answerBox);

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
}
