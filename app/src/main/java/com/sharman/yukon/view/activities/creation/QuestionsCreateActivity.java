package com.sharman.yukon.view.activities.creation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.AnswerBox;
import com.sharman.yukon.model.DissertativeAnswer;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.MultipleChoiceAnswer;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.SingleChoiceAnswer;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.AlternativeAnswerDialog;
import com.sharman.yukon.view.activities.util.AnswerAlternativePair;
import com.sharman.yukon.view.activities.util.DialogCallback;

import org.json.JSONException;

import java.util.List;


public class QuestionsCreateActivity extends GoogleRestConnectActivity implements DialogCallback {
    private Exam exam;
    private TeacherAnswers teacherAnswers;

    private AlternativeAnswerDialog alternativeAnswerDialog;

    private EMultipleAnswerType eMultipleAnswerType;
    private View alternativesRow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_create);

        alternativeAnswerDialog = new AlternativeAnswerDialog();
        alternativeAnswerDialog.setDialogCallback(this);

        alternativesRow = findViewById(R.id.alternativesRow);

        Spinner answerTypeSpinner = (Spinner) findViewById(R.id.answerTypeSpinner);
        answerTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO fix this logic:
                if(i==0){
                    eMultipleAnswerType = null;
                } else if(i==1){
                    eMultipleAnswerType = EMultipleAnswerType.SINGLE_CHOICE;
                } else if(i==2){
                    eMultipleAnswerType = EMultipleAnswerType.MULTIPLE_CHOICE;
                } else{
                    eMultipleAnswerType = null;
                }

                if(eMultipleAnswerType == null){
                    alternativesRow.setVisibility(View.INVISIBLE);
                } else{
                    alternativesRow.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        try {
            exam = new Exam(getIntent().getExtras().getString("exam"));
        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }

        try {
            teacherAnswers = new TeacherAnswers(getIntent().getExtras().getString("teacherAnswers"));
        } catch (NullPointerException | JSONException e){
            teacherAnswers = new TeacherAnswers(new WeightTypeAnswerStruct[]{});
        }

        //TODO System.out.println();
        System.out.println(">> EXAM: " + exam.toString());
        System.out.println(">> TEACHER_ANSWERS: " + teacherAnswers.toString());
    }





    @Override
    public void onPositive() {
        // TODO mostrar um preview das alternativas
    }

    @Override
    public void onNegative() {}

    @Override
    public void onNeutral() {}





    public void radioButton_onClick(View view){
        alternativeAnswerDialog.radioButton_onClick(view);
    }

    public void questionAlternativesIn_onCLick(View view){
        alternativeAnswerDialog.setEMultipleAnswerType(eMultipleAnswerType);
        alternativeAnswerDialog.show(getFragmentManager(), "alternatives_dialog");
    }





    /*
     * *Updates the exam object, adding the question to its question array:
     */
    public void updateExam(){
        EditText questionTitleIn = (EditText) findViewById(R.id.questionTitleIn);
        EditText questionWeightIn = (EditText) findViewById(R.id.questionWeightIn);
        AnswerBox answerBox;

        if(eMultipleAnswerType == null) {
            answerBox = new AnswerBox();
        } else{
            List<AnswerAlternativePair> answerAlternativePairList = alternativeAnswerDialog.getAnswerAlternativePairList();
            String[] alternativeArray = new String[answerAlternativePairList.size()];

            for(int i=0; i<alternativeArray.length; i++){
                alternativeArray[i] = answerAlternativePairList.get(i).getQuestion();
            }

            answerBox = new AnswerBox(alternativeArray, eMultipleAnswerType);
        }

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
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    /*
     * *Updates the teacherAnswers object, adding the answer to its answer array:
     */
    public void updateTeacherAnswers(){
        EditText questionWeightIn = (EditText) findViewById(R.id.questionWeightIn);
        List<AnswerAlternativePair> answerAlternativePairList = alternativeAnswerDialog.getAnswerAlternativePairList();
        Answer answer;

        if(eMultipleAnswerType == null){

            answer = new DissertativeAnswer("");

        } else {

            switch (eMultipleAnswerType) {
                default:
                case SINGLE_CHOICE:
                    int indexCorrect = -1;
                    for (int i = 0; i < answerAlternativePairList.size(); i++) {
                        if (answerAlternativePairList.get(i).isCorrect()) {
                            indexCorrect = i;
                        }
                    }
                    answer = new SingleChoiceAnswer(indexCorrect);
                    break;

                case MULTIPLE_CHOICE:
                    int indexCorrectCount = 0;
                    for (int i = 0; i < answerAlternativePairList.size(); i++) {
                        if (answerAlternativePairList.get(i).isCorrect()) {
                            indexCorrectCount++;
                        }
                    }

                    int[] indexCorrectArray = new int[indexCorrectCount];
                    int index = 0;
                    for (int i = 0; i < answerAlternativePairList.size(); i++) {
                        if (answerAlternativePairList.get(i).isCorrect()) {
                            indexCorrectArray[index] = i;
                            index++;
                        }
                    }

                    answer = new MultipleChoiceAnswer(indexCorrectArray);
                    break;
            }

        }

        WeightTypeAnswerStruct weightTypeAnswerStruct = new WeightTypeAnswerStruct(
                Double.parseDouble(questionWeightIn.getText().toString()),
                eMultipleAnswerType,
                answer);

        WeightTypeAnswerStruct[] weightTypeAnswerStructArrayOLD = teacherAnswers.getWeightTypeAnswerStructArray();
        WeightTypeAnswerStruct[] weightTypeAnswerStructArrayNEW = new WeightTypeAnswerStruct[weightTypeAnswerStructArrayOLD.length+1];

        for(int i=0; i<weightTypeAnswerStructArrayOLD.length; i++){
            weightTypeAnswerStructArrayNEW[i] = weightTypeAnswerStructArrayOLD[i];
        }

        weightTypeAnswerStructArrayNEW[weightTypeAnswerStructArrayNEW.length-1] = weightTypeAnswerStruct;

        try {
            teacherAnswers.setWeightTypeAnswerStructArray(weightTypeAnswerStructArrayNEW);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }




    public void addQuestionBtn_onClick(View view){
        updateExam();
        updateTeacherAnswers();

        Intent addQuestionIntent = new Intent(this, QuestionsCreateActivity.class);
        addQuestionIntent.putExtra("exam", exam.toString());
        addQuestionIntent.putExtra("teacherAnswers", teacherAnswers.toString());
        startActivity(addQuestionIntent);
        //finish();
    }


    public void questionCreateShareActionButton_onClick() {
        updateExam();
        updateTeacherAnswers();

        Intent examCreateConfirmIntent = new Intent(this, ExamCreateConfirmActivity.class);
        examCreateConfirmIntent.putExtra("exam", exam.toString());
        examCreateConfirmIntent.putExtra("teacherAnswers", teacherAnswers.toString());
        startActivity(examCreateConfirmIntent);
        //finish();
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
                questionCreateShareActionButton_onClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
