package com.sharman.yukon.view.activities.answering;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.AnswerBox;
import com.sharman.yukon.model.DissertativeAnswer;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.MultipleChoiceAnswer;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.SingleChoiceAnswer;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnsweringActivity extends GoogleRestConnectActivity {
    private String studentAnswerFileId;

    private Exam exam;
    private StudentAnswers studentAnswers;

    private int questionIndex;
    private Question question;
    private List<View> answerViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answering);

        answerViewList = new ArrayList<>();

        try {
            questionIndex = getIntent().getExtras().getInt("questionIndex");
            exam = new Exam(getIntent().getExtras().getString("exam"));
            studentAnswerFileId = getIntent().getExtras().getString("studentAnswerFileId");

            try {
                studentAnswers = new StudentAnswers(getIntent().getExtras().getString("studentAnswers"));
            } catch (NullPointerException | JSONException e){
                studentAnswers = new StudentAnswers(new Answer[]{});
            }

            question = exam.getQuestionArray()[questionIndex];


            try {
                // *Setting the title of the actionBar:
                getSupportActionBar().setTitle(getResources().getString(R.string.activityTitle_questionAnswering) + " " + (questionIndex+1) + "/" + exam.getQuestionArray().length);
            } catch (NullPointerException e){
                e.printStackTrace();
            }

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

        answerViewList.clear();
        container.removeAllViews();

        if(answerBox.getEMultipleAnswerType() != null){
            CompoundButton row;

            String[] titleArray = answerBox.getTitleArray();
            for(int i=0; i<titleArray.length; i++){

                switch (answerBox.getEMultipleAnswerType()){
                    case SINGLE_CHOICE:
                        row = (RadioButton) getLayoutInflater().inflate(R.layout.row_single_choice_answer_answering, null);
                        break;
                    case MULTIPLE_CHOICE:
                        row = (CheckBox) getLayoutInflater().inflate(R.layout.row_multiple_choice_answer_answering, null);
                        break;
                    default:
                        row = (RadioButton) getLayoutInflater().inflate(R.layout.row_single_choice_answer_answering, null);
                        break;
                }

                container.addView(row);
                answerViewList.add(row);
                row.setId(i);
                row.setText(titleArray[i]);
            }
        } else{
            EditText row;
            row = (EditText) getLayoutInflater().inflate(R.layout.row_dissertative_answer_answering, null);
            container.addView(row);
            answerViewList.add(row);
        }
    }



    private void updateStudentAnswers(){
        EMultipleAnswerType eMultipleAnswerType = question.getAnswerBox().getEMultipleAnswerType();
        Answer answer;

        if(eMultipleAnswerType == null){
            answer = new DissertativeAnswer(((EditText) answerViewList.get(0)).getText().toString());
        } else {
            switch (eMultipleAnswerType) {
                default:
                case SINGLE_CHOICE:

                    int indexSelected = -1;
                    for(int i=0; i<answerViewList.size(); i++){
                        CompoundButton compoundButton = (RadioButton) answerViewList.get(i);
                        if(compoundButton.isChecked()){
                            indexSelected = i;
                            break;
                        }
                    }

                    answer = new SingleChoiceAnswer(indexSelected);
                    break;

                case MULTIPLE_CHOICE:
                    int indexSelectedCount = 0;
                    for(int i=0; i<answerViewList.size(); i++){
                        CompoundButton compoundButton = (CheckBox) answerViewList.get(i);
                        if(compoundButton.isChecked()){
                            indexSelectedCount++;
                        }
                    }

                    int indexSelectedArray[] = new int[indexSelectedCount];
                    int index = 0;
                    for(int i=0; i<answerViewList.size(); i++){
                        CompoundButton compoundButton = (CheckBox) answerViewList.get(i);
                        if(compoundButton.isChecked()){
                            indexSelectedArray[index] = i;
                            index++;
                        }
                    }

                    answer = new MultipleChoiceAnswer(indexSelectedArray);
                    break;
            }
        }


        Answer[] answerArrayOLD = studentAnswers.getAnswerArray();
        Answer[] answerArrayNEW = new Answer[answerArrayOLD.length+1];

        for(int i=0; i<answerArrayOLD.length; i++){
            answerArrayNEW[i] = answerArrayOLD[i];
        }

        answerArrayNEW[answerArrayNEW.length-1] = answer;

        try {
            studentAnswers.setAnswerArray(answerArrayNEW);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }



    // *Action of the "Next" button:
    public void questionAnsweringNextButton_onClick(View view){
        // *If it was the last question:
        if(exam.getQuestionArray().length == questionIndex+1){
            updateStudentAnswers();
            Intent examAnsweringIntent = new Intent(this, ExamAnsweringConfirmActivity.class);
            examAnsweringIntent.putExtra("studentAnswers", studentAnswers.toString());
            examAnsweringIntent.putExtra("studentAnswerFileId", studentAnswerFileId);
            examAnsweringIntent.putExtra("exam", exam.toString());
            startActivity(examAnsweringIntent);
        } else {
            updateStudentAnswers();
            Intent questionAnsweringIntent = new Intent(this, QuestionAnsweringActivity.class);
            questionAnsweringIntent.putExtra("questionIndex", questionIndex+1);
            questionAnsweringIntent.putExtra("studentAnswers", studentAnswers.toString());
            questionAnsweringIntent.putExtra("studentAnswerFileId", studentAnswerFileId);
            questionAnsweringIntent.putExtra("exam", exam.toString());
            startActivity(questionAnsweringIntent);
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
