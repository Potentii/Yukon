package com.sharman.yukon.view.activities.creation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.AnswerBox;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.AlternativeAnswerDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.AnswerAlternativePair;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.FormValidator;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class QuestionsCreateActivity extends GoogleRestConnectActivity {
    public static final String QUESTION_INTENT_KEY = "question";
    public static final String WTA_STRUCT_INTENT_KEY = "weightTypeAnswerStruct";
    public static final String QUESTION_INDEX_INTENT_KEY = "questionIndex";

    private Question question;
    private WeightTypeAnswerStruct weightTypeAnswerStruct;
    private EMultipleAnswerType eMultipleAnswerType;
    private List<AnswerAlternativePair> answerAlternativePairList = new ArrayList<>();
    private int questionIndex;

    private AlternativeAnswerDialog alternativeAnswerDialog;

    private View alternativesField;
    private EditText questionTitleIn;
    private EditText questionWeightIn;
    private Spinner answerTypeSpinner;
    private EditText questionAlternativesIn;

    private TextView questionTitleIn_errorOut;
    private TextView questionWeightIn_errorOut;
    private TextView questionAlternativesIn_errorOut;

    private FormValidator formValidator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_create);



        // *Fields:
        alternativesField = findViewById(R.id.alternativesField);
        questionTitleIn = (EditText) findViewById(R.id.questionTitleIn);
        questionWeightIn = (EditText) findViewById(R.id.questionWeightIn);
        answerTypeSpinner = (Spinner) findViewById(R.id.answerTypeSpinner);
        questionAlternativesIn = (EditText) findViewById(R.id.questionAlternativesIn);

        questionTitleIn_errorOut = (TextView) findViewById(R.id.questionTitleIn_errorOut);
        questionWeightIn_errorOut = (TextView) findViewById(R.id.questionWeightIn_errorOut);
        questionAlternativesIn_errorOut = (TextView) findViewById(R.id.questionAlternativesIn_errorOut);



        // *Validator:
        formValidator = new FormValidator(this)
                .addField(questionTitleIn, questionTitleIn_errorOut, EnumSet.of(FormValidator.EValidation.REQUIRED))
                .addField(questionWeightIn, questionWeightIn_errorOut, EnumSet.of(FormValidator.EValidation.REQUIRED, FormValidator.EValidation.FLOAT));



        // *Dialogs:
        alternativeAnswerDialog = new AlternativeAnswerDialog();
        alternativeAnswerDialog.setContext(this);
        alternativeAnswerDialog.setAnswerAlternativePairList(answerAlternativePairList);
        alternativeAnswerDialog.setDialogCallback(new DialogCallback() {
            @Override
            public void onPositive() {
                // Showing the number of registered alternatives:
                answerAlternativePairList = alternativeAnswerDialog.getAnswerAlternativePairList();
                questionAlternativesIn_formatTxt();
            }

            @Override
            public void onNegative() {}

            @Override
            public void onNeutral() {}
        });



        // *Intent data:
        int requestCode = getIntent().getExtras().getInt(ExamCreateActivity.REQUEST_CODE_INTENT_KEY);
        questionIndex = getIntent().getExtras().getInt(ExamCreateActivity.QUESTION_INDEX_INTENT_KEY);

        if(requestCode == ExamCreateActivity.ADD_QUESTION_REQUEST){
            question = new Question("", 0, new AnswerBox());
            weightTypeAnswerStruct = new WeightTypeAnswerStruct(0, null, new Answer(new String[0]));
        } else if(requestCode == ExamCreateActivity.EDIT_QUESTION_REQUEST){
            String questionStr = getIntent().getExtras().getString(ExamCreateActivity.QUESTION_INTENT_KEY);
            String wtaStructStr = getIntent().getExtras().getString(ExamCreateActivity.WTA_STRUCT_INTENT_KEY);
            try {
                question = new Question(questionStr);
                weightTypeAnswerStruct = new WeightTypeAnswerStruct(wtaStructStr);

                eMultipleAnswerType = weightTypeAnswerStruct.getEMultipleAnswerType();

                // *Setting new answerAlternativePairList based on given Question:
                String[] titleArray = question.getAnswerBox().getTitleArray();
                String[] answerArray = weightTypeAnswerStruct.getAnswer().getAnswerArray();
                int[] correctAlternativesArray = Answer.convertAlphabetArray_IntArray(answerArray);


                if(titleArray.length >= correctAlternativesArray.length){
                    answerAlternativePairList.clear();
                    for (int i = 0; i<titleArray.length; i++) {
                        boolean correct = false;
                        for (int j = 0; j < correctAlternativesArray.length; j++) {
                            if(correctAlternativesArray[j] == i){
                                correct = true;
                                break;
                            }
                        }
                        answerAlternativePairList.add(new AnswerAlternativePair(correct, titleArray[i]));
                    }
                }


                alternativeAnswerDialog.setAnswerAlternativePairList(answerAlternativePairList);


                questionTitleIn.setText(question.getTitle());
                questionWeightIn.setText(String.valueOf(weightTypeAnswerStruct.getWeight()));
                if(eMultipleAnswerType == null){
                    answerTypeSpinner.setSelection(0);
                } else if(eMultipleAnswerType == EMultipleAnswerType.SINGLE_CHOICE){
                    answerTypeSpinner.setSelection(1);
                } else{
                    answerTypeSpinner.setSelection(2);
                }
                questionAlternativesIn_formatTxt();

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
                question = new Question("", 0, new AnswerBox());
                weightTypeAnswerStruct = new WeightTypeAnswerStruct(0, null, new Answer(new String[0]));
                answerAlternativePairList.clear();
            }
        } else{
            question = new Question("", 0, new AnswerBox());
            weightTypeAnswerStruct = new WeightTypeAnswerStruct(0, null, new Answer(new String[0]));
        }


        eMultipleAnswerType = weightTypeAnswerStruct.getEMultipleAnswerType();


        try {
            getActionToolbar().setTitle(getResources().getString(R.string.activityTitle_questionCreate) + " " + (questionIndex+1));
        } catch (NullPointerException e){}


        // *Spinner listener:
        answerTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EMultipleAnswerType lastEMultipleAnswerType = eMultipleAnswerType;

                // FIXME fix this logic:
                // *Setting the AnswerType:
                if(i==0){
                    eMultipleAnswerType = null;
                } else if(i==1){
                    eMultipleAnswerType = EMultipleAnswerType.SINGLE_CHOICE;
                } else if(i==2){
                    eMultipleAnswerType = EMultipleAnswerType.MULTIPLE_CHOICE;
                } else{
                    eMultipleAnswerType = null;
                }


                // *Cleaning the selected answers:
                if(lastEMultipleAnswerType != eMultipleAnswerType) {
                    alternativeAnswerDialog.cleanAnswer();
                }


                // *Hidding the alternatives field:
                if(eMultipleAnswerType == null){
                    alternativesField.setVisibility(View.INVISIBLE);
                    // *Remove from validation process:
                    formValidator.removeComplexField(alternativeAnswerDialog);
                } else{
                    alternativesField.setVisibility(View.VISIBLE);
                    // *Add to validation process:
                    formValidator.addComplexField(alternativeAnswerDialog, questionAlternativesIn, questionAlternativesIn_errorOut);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listeners methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void confirmFAB_onClick(View view){
        formValidator.doVisualValidation();
        if(!formValidator.isValid()){
            new AndroidUtil(this).showToast(R.string.toast_invalidFields, Toast.LENGTH_LONG);
            return;
        }

        List<AnswerAlternativePair> answerAlternativePairList = alternativeAnswerDialog.getAnswerAlternativePairList();


        // *Setting the answerBox:
        AnswerBox answerBox;
        if(eMultipleAnswerType == null) {
            answerBox = new AnswerBox();
        } else{
            String[] alternativeArray = new String[answerAlternativePairList.size()];

            for(int i=0; i<alternativeArray.length; i++){
                alternativeArray[i] = answerAlternativePairList.get(i).getAlternative();
            }
            answerBox = new AnswerBox(alternativeArray, eMultipleAnswerType);
        }


        // *Setting the answer:
        Answer answer;
        if(eMultipleAnswerType == null){
            answer = new Answer(new String[]{});
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
                    answer = new Answer(Answer.convertIntArray_AlphabetArray(new int[]{indexCorrect}));
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
                    answer = new Answer(Answer.convertIntArray_AlphabetArray(indexCorrectArray));
                    break;
            }
        }


        // *Creating the WeightTypeAnswerStruct object:
        weightTypeAnswerStruct = new WeightTypeAnswerStruct(
                Double.parseDouble(questionWeightIn.getText().toString()),
                eMultipleAnswerType,
                answer);


        // *Creating the Question object:
        question = new Question(
                questionTitleIn.getText().toString(),
                Double.parseDouble(questionWeightIn.getText().toString()),
                answerBox);


        // *Sending the OK result intent:
        Intent resultIntent = new Intent();
        resultIntent.putExtra(QUESTION_INTENT_KEY, question.toString());
        resultIntent.putExtra(WTA_STRUCT_INTENT_KEY, weightTypeAnswerStruct.toString());
        resultIntent.putExtra(QUESTION_INDEX_INTENT_KEY, questionIndex);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, resultIntent);
        } else {
            getParent().setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }


    public void questionAlternativesIn_onCLick(View view){
        alternativeAnswerDialog.setAnswerAlternativePairList(answerAlternativePairList);
        alternativeAnswerDialog.seteMultipleAnswerType(eMultipleAnswerType);
        alternativeAnswerDialog.show(getFragmentManager(), "alternative_answer_dialog");
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void questionAlternativesIn_formatTxt(){
        if(answerAlternativePairList.size() == 0){
            questionAlternativesIn.setText("");
        } else if(answerAlternativePairList.size() == 1){
            questionAlternativesIn.setText(answerAlternativePairList.size() + " " + getResources().getString(R.string.input_questionCreate_alternativesSelected_singular));
        } else{
            questionAlternativesIn.setText(answerAlternativePairList.size() + " " + getResources().getString(R.string.input_questionCreate_alternativesSelected_plural));
        }
    }
}
