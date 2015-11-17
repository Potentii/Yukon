package com.sharman.yukon.view.activities.managing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.AnswerCorrectionDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ExamManagingStudentInspectActivity extends GoogleRestConnectActivity {
    private String studentAnswerFileId;
    private String gradeFileId;

    private TeacherAnswers teacherAnswers;
    private StudentAnswers studentAnswers;
    private Grade grade;

    private LayoutInflater layoutInflater;
    private LinearLayout rowContainer;
    private Button acceptGradeBtn;
    private AnswerCorrectionDialog answerCorrectionDialog;

    private List<View> rowList = new ArrayList<>();
    private Boolean[] correctionArray;
    private boolean gradeSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_managing_student_inspect);

        layoutInflater = getLayoutInflater();
        rowContainer = (LinearLayout) findViewById(R.id.rowContainer);
        answerCorrectionDialog = new AnswerCorrectionDialog();
        acceptGradeBtn = (Button) findViewById(R.id.acceptGradeBtn);
    }



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        try {
            gradeFileId = getIntent().getExtras().getString("gradeFileId");
            studentAnswerFileId = getIntent().getExtras().getString("studentAnswerFileId");

            teacherAnswers = new TeacherAnswers(getIntent().getExtras().getString("teacherAnswers"));

            final View infoPhotoHeader = findViewById(R.id.infoPhotoHeader);
            new AndroidUtil(this).fillInfoPhotoToolbar_AndroidContactsImage(
                    infoPhotoHeader,
                    getIntent().getExtras().getString("studentImageUri"),
                    getIntent().getExtras().getString("studentName"),
                    getIntent().getExtras().getString("studentEmail"),
                    ""
            );

            if(grade == null) {
                loadInfo();
            }

        } catch (NullPointerException | JSONException e){
            // TODO error
            e.printStackTrace();
        }
    }



    private void loadInfo(){
        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());

        driveIOHandler.readFile(gradeFileId, new FileReadCallback() {
            @Override
            public void onSuccess(final String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            grade = new Grade(content);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // *Showing the grade of the student:
                                    TextView gradeOut = (TextView) findViewById(R.id.gradeOut);

                                    double gradeGrade = grade.getGrade();
                                    if(gradeGrade<0){
                                        gradeOut.setText(getResources().getString(R.string.output_grade_notSet_text));
                                    } else{
                                        gradeOut.setText(Double.toString(gradeGrade));
                                    }
                                }
                            });

                            driveIOHandler.readFile(studentAnswerFileId, new FileReadCallback() {
                                @Override
                                public void onSuccess(String content) {
                                    try {
                                        studentAnswers = new StudentAnswers(content);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                buildAnswersList();
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    // TODO error
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO error
                exception.printStackTrace();
            }
        });
    }



    private void buildAnswersList(){
        Boolean[] correctionArray = grade.getCorrectionArray();
        WeightTypeAnswerStruct[] teacherAnswerArray = teacherAnswers.getWeightTypeAnswerStructArray();
        Answer[] studentAnswerArray = studentAnswers.getAnswerArray();
        double gradeGrade = grade.getGrade();

        rowContainer.removeAllViews();

        if(gradeGrade<0){
            gradeSet = false;
            correctTheAnswers(studentAnswerArray, teacherAnswerArray);
        } else{
            if(correctionArray.length>0){
                // *The student already answered and have its grade set:
                // TODO listar apenas usando o correctionArray
                gradeSet = true;
                listAnswers(studentAnswerArray, teacherAnswerArray, correctionArray);
            } else{
                // *Ocorreu algum erro com o correctionArray, portanto deve gerar outro:
                gradeSet = false;
                correctTheAnswers(studentAnswerArray, teacherAnswerArray);
            }
        }
    }



    private void correctTheAnswers(Answer[] studentAnswerArray, WeightTypeAnswerStruct[] teacherAnswerArray){
        if(studentAnswerArray.length == 0){
            final View row = layoutInflater.inflate(R.layout.row_answer_managing, null);
            TextView answerOut = (TextView) row.findViewById(R.id.answerOut);
            answerOut.setText(getResources().getString(R.string.output_answerCorrection_studentDidntAnswered));
            rowContainer.addView(row);
            return;
        }

        if(studentAnswerArray.length != teacherAnswerArray.length){
            // TODO error
            System.out.println("ERRO");
            return;
        }

        Boolean[] correctionArray = new Boolean[teacherAnswerArray.length];

        for(int i=0; i<teacherAnswerArray.length; i++){
            correctionArray[i] = teacherAnswerArray[i].getAnswer().compareAnswerTo(studentAnswerArray[i]);
        }

        listAnswers(studentAnswerArray, teacherAnswerArray, correctionArray);
    }



    private void listAnswers(Answer[] studentAnswerArray, WeightTypeAnswerStruct[] teacherAnswerArray, Boolean[] correctionArray){
        if(studentAnswerArray.length != teacherAnswerArray.length || studentAnswerArray.length != correctionArray.length){
            // TODO error
            System.out.println("ERRO");
            return;
        }

        this.correctionArray = correctionArray;
        rowList.clear();
        rowContainer.removeAllViews();

        for(int i=0; i<studentAnswerArray.length; i++){
            final View row = layoutInflater.inflate(R.layout.row_answer_managing, null);
            formatRow(i, row, correctionArray[i], studentAnswerArray[i], teacherAnswerArray[i].getAnswer());
            rowList.add(row);
            rowContainer.addView(row);
        }

        tryToEnableButton();
    }



    private void formatRow(final int index, final View row, @Nullable final Boolean correct, final Answer studentAnswer, final Answer teacherAnswer){
        final TextView answerNumberOut = (TextView) row.findViewById(R.id.answerNumberOut);
        final TextView answerOut = (TextView) row.findViewById(R.id.answerOut);
        final int color;

        if(correct == null){
            color = getResources().getColor(R.color.answer_neutral);
        } else{
            if(correct){
                color = getResources().getColor(R.color.answer_correct);
            } else{
                color = getResources().getColor(R.color.answer_incorrect);
            }
        }

        answerNumberOut.setText((index+1) + "-)");

        answerOut.setText(studentAnswer.getFormattedAnswerString());
        answerOut.setTextColor(color);


        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerCorrectionDialog.setStudentAnswer(studentAnswer);
                answerCorrectionDialog.setTeacherAnswer(teacherAnswer);
                answerCorrectionDialog.setColor(color);
                answerCorrectionDialog.setIndex(index);
                answerCorrectionDialog.setCorrect(correct);
                answerCorrectionDialog.setDialogCallback(dialogCallback);

                answerCorrectionDialog.show(getFragmentManager(), "answer_correction_dialog");
            }
        });
    }


    private DialogCallback dialogCallback = new DialogCallback() {
        @Override
        public void onPositive() {
            int index = answerCorrectionDialog.getIndex();
            correctionArray[index] = true;

            formatRow(index, rowList.get(index), correctionArray[index], answerCorrectionDialog.getStudentAnswer(), answerCorrectionDialog.getTeacherAnswer());
            tryToEnableButton();
        }

        @Override
        public void onNegative() {
            int index = answerCorrectionDialog.getIndex();
            correctionArray[index] = false;

            formatRow(index, rowList.get(index), correctionArray[index], answerCorrectionDialog.getStudentAnswer(), answerCorrectionDialog.getTeacherAnswer());
            tryToEnableButton();
        }

        @Override
        public void onNeutral() {}
    };



    private void tryToEnableButton(){
        if(gradeSet){ return; }
        for(int i=0; i<correctionArray.length; i++){
            if(correctionArray[i] == null){
                return;
            }
        }


        WeightTypeAnswerStruct[] teacherAnswerArray = teacherAnswers.getWeightTypeAnswerStructArray();
        double newGrade = 0.0;

        if(teacherAnswerArray.length != correctionArray.length){
            System.out.println("ERRO");
            return;
        }

        for(int i=0; i<correctionArray.length; i++){
            newGrade += correctionArray[i] ? teacherAnswerArray[i].getWeight() : 0.0;
        }


        try {
            grade.setGrade(newGrade);
            grade.setCorrectionArray(correctionArray);

            // TODO show new grade

            TextView gradeOut = (TextView) findViewById(R.id.gradeOut);
            gradeOut.setText(Double.toString(newGrade));

            acceptGradeBtn.setEnabled(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Listener methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void acceptGradeBtn_onClick(View view){
        if(grade == null) {
            return;
        }

        new AndroidUtil(this).showToast("Working.", Toast.LENGTH_SHORT);
        final Activity activity = this;

        new DriveIOHandler(getCredential()).editFile(gradeFileId, null, null, grade.toString(), new FileEditCallback() {
            @Override
            public void onSuccess() {
                new AndroidUtil(activity).showToast("Grade set", Toast.LENGTH_SHORT);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        Intent examManagingStudentsActivityIntent = new Intent(activity, ExamManagingStudentsActivity.class);
                        startActivity(examManagingStudentsActivityIntent);
                        finish();
                        */
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                new AndroidUtil(activity).showToast("Something went wrong, try again", Toast.LENGTH_SHORT);
            }
        });


        System.out.println("E");

    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * ActionBar methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam_managing_student_inspect, menu);
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
