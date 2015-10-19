package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.AnswerBox;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.Question;

import org.json.JSONException;

public class QuestionAnsweringActivity extends GoogleRestConnectActivity {
    private Exam exam;
    private int questionIndex;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answering);


        try {
            questionIndex = getIntent().getExtras().getInt("questionIndex");
            exam = new Exam(getIntent().getExtras().getString("exam"));
            question = exam.getQuestionArray()[questionIndex];

            // *Setting the title of the actionBar:
            getSupportActionBar().setTitle(getResources().getString(R.string.activityTitle_questionAnswering) + questionIndex + "/" + exam.getQuestionArray().length);

            //TODO change the button icon if it is the last question

            // *Setting the weight, question title, and alternatives:
            TextView weightOut = (TextView) findViewById(R.id.weightOut);
            TextView titleOut = (TextView) findViewById(R.id.titleOut);

            weightOut.setText("(" + question.getWeight() + ")");
            titleOut.setText(question.getTitle());
            buildAnswerDiv();

        } catch (NullPointerException | JSONException | ArrayIndexOutOfBoundsException e){
            // TODO error
            e.printStackTrace();
        }
    }


    private void buildAnswerDiv(){
        AnswerBox answerBox = question.getAnswerBox();
        RadioGroup container = (RadioGroup) findViewById(R.id.answerDiv);
        View row;

        if(answerBox.getEMultipleAnswerType() != null){

            String[] titleArray = answerBox.getTitleArray();
            for(int i=0; i<titleArray.length; i++){

                switch (answerBox.getEMultipleAnswerType()){
                    case SINGLE_CHOICE:
                        row = getLayoutInflater().inflate(R.layout.row_single_choice_answer_answering, null);
                        break;
                    case MULTIPLE_CHOICE:
                        row = getLayoutInflater().inflate(R.layout.row_multiple_choice_answer_answering, null);
                        break;
                    default:
                        row = getLayoutInflater().inflate(R.layout.row_single_choice_answer_answering, null);
                        break;
                }

                container.addView(row);
                CompoundButton alternativeOut = (CompoundButton) row.findViewById(R.id.alternativeOut);
                alternativeOut.setText(titleArray[i]);
            }
        } else{
            row = getLayoutInflater().inflate(R.layout.row_dissertative_answer_answering, null);
            container.addView(row);
        }


    }



    // *Action of the "Next" button:
    public void questionAnsweringNextButton_onClick(View view){
        // *If it was the last question:
        if(exam.getQuestionArray().length == questionIndex+1){
            Intent examAnsweringIntent = new Intent(this, ExamAnsweringConfirmActivity.class);
            examAnsweringIntent.putExtra("exam", exam.toString());
            startActivity(examAnsweringIntent);
            finish();
        } else {
            Intent questionAnsweringIntent = new Intent(this, QuestionAnsweringActivity.class);
            questionAnsweringIntent.putExtra("questionIndex", questionIndex+1);
            questionAnsweringIntent.putExtra("exam", exam.toString());
            startActivity(questionAnsweringIntent);
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question_answering, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
