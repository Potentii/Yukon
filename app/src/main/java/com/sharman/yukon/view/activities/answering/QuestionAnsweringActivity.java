package com.sharman.yukon.view.activities.answering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.CompoundButtonController;
import com.sharman.yukon.view.activities.util.FormValidator;
import com.sharman.yukon.view.activities.util.Validatable;

import org.json.JSONException;

import java.text.DecimalFormat;

public class QuestionAnsweringActivity extends GoogleRestConnectActivity implements Validatable {
    public final static int ANSWER_QUESTION_REQUEST = 55;

    public final static String REQUEST_CODE_INTENT_KEY = "requestCode";
    public final static String EXAM_TITLE_INTENT_KEY = "examTitle";
    public final static String QUESTION_INDEX_INTENT_KEY = "questionIndex";
    public final static String QUESTION_INTENT_KEY = "question";
    public final static String ANSWER_INTENT_KEY = "answer";


    private static int ROW_REMOVE_BTN_ID = R.id.removeBtn;
    private static int ROW_COMPOUND_BTN_ID = R.id.compoundBtn;
    private static int ROW_SINGLE_CHOICE_LAYOUT = R.layout.row_alternative_single_creating;
    private static int ROW_MULTIPLE_CHOICE_LAYOUT = R.layout.row_alternative_multiple_creating;


    private String examTitle;
    private int index;
    private Question question;
    private Answer answer;
    private EMultipleAnswerType eMultipleAnswerType;


    private TextView questionTextOut;
    private TextView questionNumberOut;
    private EditText textAnswerIn;
    private LinearLayout rowContainer;
    private TextView answerIn_errorOut;


    private LayoutInflater layoutInflater;
    private CompoundButtonController compoundButtonController;
    private FormValidator formValidator;


    private String invalidText = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answering);

        // *Initializing objects:
        layoutInflater = getLayoutInflater();
        compoundButtonController = new CompoundButtonController();
        formValidator = new FormValidator(this);


        // *Getting Views from XML:
        questionTextOut = (TextView) findViewById(R.id.questionTextOut);
        questionNumberOut = (TextView) findViewById(R.id.questionNumberOut);
        textAnswerIn = (EditText) findViewById(R.id.textAnswerIn);
        rowContainer = (LinearLayout) findViewById(R.id.rowContainer);
        answerIn_errorOut = (TextView) findViewById(R.id.answerIn_errorOut);


        // *Setting the formValidator fields:
        formValidator.addComplexField(this, null, answerIn_errorOut);


        // *Intent data:
        int requestCode = getIntent().getExtras().getInt(REQUEST_CODE_INTENT_KEY);

        if(requestCode == ANSWER_QUESTION_REQUEST){
            try {
                examTitle = getIntent().getExtras().getString(EXAM_TITLE_INTENT_KEY);
                index = getIntent().getExtras().getInt(QUESTION_INDEX_INTENT_KEY);
                question = new Question(getIntent().getExtras().getString(QUESTION_INTENT_KEY));
                try{
                    answer = new Answer(getIntent().getExtras().getString(ANSWER_INTENT_KEY));
                }catch (JSONException | NullPointerException e) {
                    answer = null;
                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
                // ERROR: back to previous activity
                new AndroidUtil(this).showToast(R.string.toast_somethingWentWrong, Toast.LENGTH_SHORT);
                backToPreviousActivity();
                return;
            }
        } else{
            // ERROR: back to previous activity
            new AndroidUtil(this).showToast(R.string.toast_somethingWentWrong, Toast.LENGTH_SHORT);
            backToPreviousActivity();
            return;
        }


        // *Displaying information:
        try {
            getActionToolbar().setTitle(examTitle);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        questionNumberOut.setText(String.valueOf(index+1));
        questionTextOut.setText("[" + new DecimalFormat("#.00").format(question.getWeight()) + "] " + question.getTitle());
        textAnswerIn.setVisibility(View.GONE);


        // *Building answer box:
        eMultipleAnswerType = question.getAnswerBox().getEMultipleAnswerType();
        String[] alternativeTitleArray = question.getAnswerBox().getTitleArray();

        if(eMultipleAnswerType == null){
            // *Text answer:
            textAnswerIn.setVisibility(View.VISIBLE);
            if(answer != null && answer.getAnswerArray().length != 0){
                // *Already answered
                textAnswerIn.setText(answer.getAnswerArray()[0]);
            }
        } else{
            // *Choice answer:
            int[] answerIndexArray = new int[0];

            if(answer != null && answer.getAnswerArray().length != 0){
                // *Already answered
                String[] answerAlphabetArray = answer.getAnswerArray();
                if(answerAlphabetArray.length <= alternativeTitleArray.length){
                    answerIndexArray = Answer.convertAlphabetArray_IntArray(answerAlphabetArray);
                }
            }

            for (int i = 0; i < alternativeTitleArray.length; i++) {
                boolean checked = false;

                for (int index : answerIndexArray) {
                    if(index == i){
                        checked = true;
                    }
                }

                addRow(eMultipleAnswerType, alternativeTitleArray[i], checked);
            }
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void addRow(EMultipleAnswerType eMultipleAnswerType, String title, boolean answer){
        final View row;

        // *Setting the row xml:
        if(eMultipleAnswerType == EMultipleAnswerType.SINGLE_CHOICE){
            row = layoutInflater.inflate(ROW_SINGLE_CHOICE_LAYOUT, null);
        } else {
            row = layoutInflater.inflate(ROW_MULTIPLE_CHOICE_LAYOUT, null);
        }

        // *Hiding removeBtn:
        ImageButton removeBtn = (ImageButton) row.findViewById(ROW_REMOVE_BTN_ID);
        removeBtn.setVisibility(View.GONE);

        // *RadioButton listener:
        final CompoundButton compoundButton = (CompoundButton) row.findViewById(ROW_COMPOUND_BTN_ID);
        compoundButtonController.addCompoundButton(compoundButton);

        // *Fill the text and check state of the row:
        compoundButton.setText(title);
        compoundButton.setChecked(answer);

        rowContainer.addView(row);
    }


    private void backToPreviousActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_CANCELED, intent);
                } else {
                    getParent().setResult(Activity.RESULT_CANCELED, intent);
                }
                finish();
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listener methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void confirmFAB_onClick(View view){
        formValidator.doVisualValidation();
        if(!formValidator.isValid()){
            new AndroidUtil(this).showToast(R.string.toast_invalidFields, Toast.LENGTH_LONG);
            return;
        }


        // *Building the new Answer object:
        String[] answerArray;
        if(eMultipleAnswerType == null){
            answerArray = new String[]{ textAnswerIn.getText().toString() };
        } else{
            answerArray = Answer.convertIntArray_AlphabetArray(compoundButtonController.getCheckedIndexes());
        }
        answer = new Answer(answerArray);


        // *Sending the OK result intent:
        Intent resultIntent = new Intent()
                .putExtra(QUESTION_INDEX_INTENT_KEY, index)
                .putExtra(ANSWER_INTENT_KEY, answer.toString());
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, resultIntent);
        } else {
            getParent().setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Validatable methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean isValid() {
        if((eMultipleAnswerType == null && textAnswerIn.getText().toString().trim().isEmpty()) || (eMultipleAnswerType != null && !compoundButtonController.isChecked())){
            try {
                invalidText = getResources().getString(R.string.output_invalidField_questionAnswering_notAnswered);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public String getInvalidText() {
        return invalidText;
    }
}
