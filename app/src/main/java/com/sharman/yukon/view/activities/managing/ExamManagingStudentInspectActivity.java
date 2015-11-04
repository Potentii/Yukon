package com.sharman.yukon.view.activities.managing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Grade;
import com.sharman.yukon.model.StudentAnswers;
import com.sharman.yukon.model.TeacherAnswers;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.dialog.AnswerCorrectionDialog;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;

import org.json.JSONException;

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
            final ImageView infoImg           = (ImageView) infoPhotoHeader.findViewById(R.id.infoImg);
            final TextView primaryInfoOut     = (TextView) infoPhotoHeader.findViewById(R.id.primaryInfoOut);
            final TextView secondaryInfoOut   = (TextView) infoPhotoHeader.findViewById(R.id.secondaryInfoOut);

            new AndroidUtil(this).formatContactImageView(infoImg, getIntent().getExtras().getString("studentImageUri"));

            primaryInfoOut.setText(getIntent().getExtras().getString("studentName"));
            secondaryInfoOut.setText(getIntent().getExtras().getString("studentEmail"));

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
        final TextView gradeOut = (TextView) findViewById(R.id.gradeOut);


        driveIOHandler.readFile(gradeFileId, new FileReadCallback() {
            @Override
            public void onSuccess(final String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            grade = new Grade(content);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO error
                exception.printStackTrace();
                gradeOut.setText(getResources().getString(R.string.output_grade_notSet_text));
                acceptGradeBtn.setEnabled(false);
            }
        });
    }




    private DialogCallback dissertativeDialogCallback = new DialogCallback() {
        @Override
        public void onPositive() {
            // *The answer was CORRECT:
            correctionArray[answerCorrectionDialog.getIndex()] = true;
            verifyIfGradeIsReadyToSet();
        }
        @Override
        public void onNegative() {
            // *The answer was INCORRECT:
            correctionArray[answerCorrectionDialog.getIndex()] = false;
            verifyIfGradeIsReadyToSet();
        }
        @Override
        public void onNeutral() {}


    };

    private DialogCallback multipleDialogCallback = new DialogCallback() {
        @Override
        public void onPositive() {}
        @Override
        public void onNegative() {}
        @Override
        public void onNeutral() {}
    };


    private void verifyIfGradeIsReadyToSet(){
        for(int i=0; i<correctionArray.length; i++){
            if(correctionArray[i] == null){
                thereIsNullAnswers = true;
                acceptGradeBtn.setEnabled(false);
                return;
            }
        }
        thereIsNullAnswers = false;

        acceptGradeBtn.setEnabled(!alreadyCorrected);
    }


    private boolean thereIsNullAnswers = false;
    private Boolean[] correctionArray;
    private boolean alreadyCorrected;


    private void buildAnswersList(){
        correctionArray = grade.getCorrectionArray();
        WeightTypeAnswerStruct[] teacherAnswerArray = teacherAnswers.getWeightTypeAnswerStructArray();
        Answer[] studentAnswerArray = studentAnswers.getAnswerArray();

        rowContainer.removeAllViews();

        if(correctionArray.length > 0 || grade.getGrade()>=0){
            alreadyCorrected = true;
        } else{
            alreadyCorrected = false;
            correctionArray = new Boolean[studentAnswerArray.length];
        }


        int studentAnswerLength = studentAnswerArray.length;
        int teacherAnswerLength = teacherAnswerArray.length;
        int correctionLength = correctionArray.length;


        if(studentAnswerLength != teacherAnswerLength || studentAnswerLength != correctionLength){
            //TODO error: numero de respostas está incoerente ou aluno ainda nao respondeu

            final View row = layoutInflater.inflate(R.layout.row_answer_managing, null);
            TextView answerOut = (TextView) row.findViewById(R.id.answerOut);
            answerOut.setText(getResources().getString(R.string.output_answerCorrection_studentDidntAnswered));
            rowContainer.addView(row);
            return;
        }


        for(int i=0; i<studentAnswerArray.length; i++){
            Answer studentAnswer = studentAnswerArray[i];
            Answer teacherAnswer = teacherAnswerArray[i].getAnswer();
            if(!alreadyCorrected){
                correctionArray[i] = studentAnswer.compareAnswerTo(teacherAnswer);
                System.out.println(studentAnswer.getFormattedAnswerString() + " == " + teacherAnswer.getFormattedAnswerString() + " : " + correctionArray[i]);
            }

            thereIsNullAnswers = correctionArray[i] == null?true:thereIsNullAnswers;
            addRow(i, teacherAnswerArray[i].getEMultipleAnswerType(), correctionArray[i], alreadyCorrected, studentAnswer, teacherAnswer);
        }


        verifyIfGradeIsReadyToSet();
    }



    private void addRow(final int index, @Nullable final EMultipleAnswerType eMultipleAnswerType, final Boolean correct, final boolean corrected, final Answer studentAnswer, final Answer teacherAnswer){
        final int color;

        final View row = layoutInflater.inflate(R.layout.row_answer_managing, null);
        TextView answerNumberOut = (TextView) row.findViewById(R.id.answerNumberOut);
        TextView answerOut = (TextView) row.findViewById(R.id.answerOut);

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
                answerCorrectionDialog.seteMultipleAnswerType(eMultipleAnswerType);
                answerCorrectionDialog.setStudentAnswer(studentAnswer);
                answerCorrectionDialog.setTeacherAnswer(teacherAnswer);
                answerCorrectionDialog.setColor(color);
                answerCorrectionDialog.setIndex(index);
                answerCorrectionDialog.setCorrected(corrected);
                if(eMultipleAnswerType == null) {
                    answerCorrectionDialog.setDialogCallback(dissertativeDialogCallback);
                } else{
                    answerCorrectionDialog.setDialogCallback(multipleDialogCallback);
                }

                answerCorrectionDialog.show(getFragmentManager(), "answer_correction_dialog");

            }
        });


        rowContainer.addView(row);
    }



    public void acceptGradeBtn_onClick(View view){
        if(grade == null){
            return;
        }


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
