package com.sharman.yukon.view.activities.answering;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.FileShareEditCallback;
import com.sharman.yukon.model.Exam;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;

import org.json.JSONException;

public class ExamAnsweringConfirmActivity extends GoogleRestConnectActivity {
    private String studentAnswerFileId;
    private Exam exam;
    private StudentAnswers studentAnswers;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answering_confirm);

        try {
            exam = new Exam(getIntent().getExtras().getString("exam"));
            studentAnswers = new StudentAnswers(getIntent().getExtras().getString("studentAnswers"));
            studentAnswerFileId = getIntent().getExtras().getString("studentAnswerFileId");

            System.out.println(">> studentAnswers: " + studentAnswers.toString());
        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }


    private boolean onAnsweringSuccess_called;
    private boolean onAnsweringFailure_called;


    private synchronized void onAnsweringSuccess(){
        if(!onAnsweringSuccess_called) {
            onAnsweringSuccess_called = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private synchronized void onAnsweringFailure(String errorMessage){
        if(!onAnsweringFailure_called) {
            onAnsweringFailure_called = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    // *Action of the "Send" button:
    private void examAnsweringConfirmSendActionButton_onClick(){
        if(!isConnected()){
            return;
        }

        onAnsweringSuccess_called = false;
        onAnsweringFailure_called = false;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Working...", Toast.LENGTH_LONG).show();
            }
        });


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
