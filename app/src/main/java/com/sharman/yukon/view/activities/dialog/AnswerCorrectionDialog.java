package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.view.activities.util.DialogCallback;

/**
 * Created by poten on 02/11/2015.
 */
public class AnswerCorrectionDialog extends DialogFragment {
    private Answer studentAnswer;
    private Answer teacherAnswer;
    @Nullable private Boolean correct;
    private int color;
    private int index;

    private DialogCallback dialogCallback;

    private DialogInterface.OnClickListener positiveCallback = new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int id) {
            try {
                dialogCallback.onPositive();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };
    private DialogInterface.OnClickListener negativeCallback = new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int id) {
            try {
                dialogCallback.onNegative();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };
    private DialogInterface.OnClickListener neutralCallback = new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int id) {
            try {
                dialogCallback.onNeutral();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();


        View view = layoutInflater.inflate(R.layout.dialog_answer_correction, null);
        View teacherAnswerContainer = view.findViewById(R.id.teacherAnswerContainer);
        TextView studentAnswerOut = (TextView) view.findViewById(R.id.studentAnswerOut);
        TextView teacherAnswerOut = (TextView) view.findViewById(R.id.teacherAnswerOut);


        studentAnswerOut.setText(studentAnswer.getFormattedAnswerString());
        studentAnswerOut.setTextColor(color);


        String teacherAnswerStr = teacherAnswer.getFormattedAnswerString();
        if(teacherAnswerStr.isEmpty()){
            teacherAnswerContainer.setVisibility(View.GONE);
        } else{
            teacherAnswerContainer.setVisibility(View.VISIBLE);
            teacherAnswerOut.setText(teacherAnswerStr);
        }


        if(correct == null){
            builder.setPositiveButton(R.string.dialog_answerCorrection_needsCorrection_correct, positiveCallback);
            builder.setNegativeButton(R.string.dialog_answerCorrection_needsCorrection_incorrect, negativeCallback);
            builder.setNeutralButton(R.string.dialog_cancel, neutralCallback);
        } else{
            builder.setNeutralButton(R.string.dialog_ok, neutralCallback);
        }


        builder.setView(view);

        return builder.create();
    }




    public Answer getStudentAnswer() {
        return studentAnswer;
    }
    public void setStudentAnswer(Answer studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public Answer getTeacherAnswer() {
        return teacherAnswer;
    }
    public void setTeacherAnswer(Answer teacherAnswer) {
        this.teacherAnswer = teacherAnswer;
    }

    @Nullable
    public Boolean getCorrect() {
        return correct;
    }
    public void setCorrect(@Nullable Boolean correct) {
        this.correct = correct;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    public DialogCallback getDialogCallback() {
        return dialogCallback;
    }
    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
