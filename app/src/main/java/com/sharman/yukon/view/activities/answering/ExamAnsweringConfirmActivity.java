package com.sharman.yukon.view.activities.answering;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.FileShareEditCallback;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.DissertativeAnswer;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.MultipleChoiceAnswer;
import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.SingleChoiceAnswer;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;

import org.json.JSONException;

public class ExamAnsweringConfirmActivity extends GoogleRestConnectActivity {
    private String studentAnswerFileId;
    private Exam exam; // TODO remover exam daqui
    private StudentAnswers studentAnswers;

    private boolean onAnsweringSuccess_called;
    private boolean onAnsweringFailure_called;

    private LayoutInflater layoutInflater;
    private LinearLayout rowContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answering_confirm);

        layoutInflater = getLayoutInflater();
        rowContainer = (LinearLayout) findViewById(R.id.rowContainer);

        try {
            exam = new Exam(getIntent().getExtras().getString("exam"));
            studentAnswers = new StudentAnswers(getIntent().getExtras().getString("studentAnswers"));
            studentAnswerFileId = getIntent().getExtras().getString("studentAnswerFileId");

            buildAnswerList();

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }



    private void buildAnswerList(){
        Answer[] answerArray = studentAnswers.getAnswerArray();
        Question[] questionArray = exam.getQuestionArray();

        if(questionArray.length != answerArray.length){
            System.out.println("ERRO");
            return;
        }

        rowContainer.removeAllViews();

        for(int i=0; i<answerArray.length; i++){
            EMultipleAnswerType eMultipleAnswerType = questionArray[i].getAnswerBox().getEMultipleAnswerType();
            Answer answer = null;

            try {
                if(eMultipleAnswerType == null){
                    answer = (DissertativeAnswer) answerArray[i];
                } else{
                    switch (eMultipleAnswerType){
                        case SINGLE_CHOICE:
                            answer = (SingleChoiceAnswer) answerArray[i];
                            break;
                        case MULTIPLE_CHOICE:
                            answer = (MultipleChoiceAnswer) answerArray[i];
                            break;
                    }
                }
            } catch (ClassCastException e){
                e.printStackTrace();
            }

            addRow(i, answer);
        }
    }


    private void addRow(final int index, final Answer answer){
        final View row = layoutInflater.inflate(R.layout.row_answer_managing, null);

        final TextView answerNumberOut = (TextView) row.findViewById(R.id.answerNumberOut);
        final TextView answerOut = (TextView) row.findViewById(R.id.answerOut);

        answerNumberOut.setText((index + 1) + "-)");
        System.out.println("ANS: " + answer.getFormattedAnswerString());
        answerOut.setText(answer.getFormattedAnswerString());

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO voltar para a questao
            }
        });

        rowContainer.addView(row);
    }






    private synchronized void onAnsweringSuccess(){
        if(!onAnsweringSuccess_called) {
            onAnsweringSuccess_called = true;
            new AndroidUtil(this).showToast("Answer sent, good luck", Toast.LENGTH_SHORT);
        }
    }

    private synchronized void onAnsweringFailure(String errorMessage){
        if(!onAnsweringFailure_called) {
            onAnsweringFailure_called = true;
            new AndroidUtil(this).showToast("Something went wrong, try again", Toast.LENGTH_SHORT);
        }
    }




    // *Action of the "Send" button:
    private void examAnsweringConfirmSendActionButton_onClick(){
        if(!isConnected()){
            return;
        }
        onAnsweringSuccess_called = false;
        onAnsweringFailure_called = false;

        new AndroidUtil(this).showToast("Working...", Toast.LENGTH_LONG);


        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());

        driveIOHandler.editFile(studentAnswerFileId, null, null, studentAnswers.toString(), new FileEditCallback() {
            @Override
            public void onSuccess() {

                driveIOHandler.editFileShare(studentAnswerFileId, getCredential().getSelectedAccountName(), "reader", new FileShareEditCallback() {
                    @Override
                    public void onSuccess() {
                        onAnsweringSuccess();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        onAnsweringFailure(errorMessage);
                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {
                onAnsweringFailure(errorMessage);
            }
        });
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_answering_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.examAnsweringConfirmSendActionButton:
                examAnsweringConfirmSendActionButton_onClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
