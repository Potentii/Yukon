package com.sharman.yukon;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sharman.yukon.model.AnswerBox;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Question;

import java.util.Date;

public class QuestionsCreateActivity extends GoogleConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_create);

        /*
        Exam exam = new Exam();
        */

        // Criando pacote de dados da Name Exam "pegando da intent
        Bundle nameData = getIntent().getExtras();
        if(nameData == null) {
            return;
        }


    }

    public void callsMyself(View view){

        Intent iCallsMyself = new Intent(this, QuestionsCreateActivity.class);

        final EditText edtquestionQuestionsInput = (EditText) findViewById(R.id.questionQuestionsInput);
        String questionQuestions = edtquestionQuestionsInput.getText().toString();
        final EditText edtweightQuestionsInput = (EditText) findViewById(R.id.weightQuestionsInput);
        //Lembrando o Peso é Bundle ou float.
        String weightQuestionsInput = edtweightQuestionsInput.getText().toString();
        //instanciei a classe AnswerBox para poder usa-la.
        AnswerBox answerBox = new AnswerBox();
        Question question = new Question(questionQuestions, Double.parseDouble(weightQuestionsInput), answerBox);

        iCallsMyself.putExtra("question", question.toString());
        startActivity(iCallsMyself);
        finish();
    }
}
