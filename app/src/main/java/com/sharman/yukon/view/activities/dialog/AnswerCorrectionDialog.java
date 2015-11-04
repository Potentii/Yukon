package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.util.DialogCallback;

/**
 * Created by poten on 02/11/2015.
 */
public class AnswerCorrectionDialog extends DialogFragment {
    private Answer studentAnswer;
    private Answer teacherAnswer;
    private EMultipleAnswerType eMultipleAnswerType;
    private boolean corrected;
    private int color;
    private int index;

    private DialogCallback dialogCallback;

    private LayoutInflater layoutInflater;


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
        layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_answer_correction, null);
        View teacherAnswerContainer = view.findViewById(R.id.teacherAnswerContainer);
        TextView studentAnswerOut = (TextView) view.findViewById(R.id.studentAnswerOut);
        TextView teacherAnswerOut = (TextView) view.findViewById(R.id.teacherAnswerOut);

        studentAnswerOut.setText(studentAnswer.getFormattedAnswerString());
        studentAnswerOut.setTextColor(color);

        teacherAnswerOut.setText(teacherAnswer.getFormattedAnswerString());

        builder.setView(view);

        if(eMultipleAnswerType == null){
            teacherAnswerContainer.setVisibility(View.GONE);

            if(corrected){
                builder.setNeutralButton(R.string.dialog_ok, neutralCallback);
            } else {
                builder.setPositiveButton(R.string.dialog_answerCorrection_dissertative_correct, positiveCallback);
                builder.setNegativeButton(R.string.dialog_answerCorrection_dissertative_incorrect, negativeCallback);
                builder.setNeutralButton(R.string.dialog_cancel, neutralCallback);
            }
        } else{
            teacherAnswerContainer.setVisibility(View.VISIBLE);

            builder.setNeutralButton(R.string.dialog_ok, neutralCallback);
        }

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

    public boolean isCorrected() {
        return corrected;
    }
    public void setCorrected(boolean corrected) {
        this.corrected = corrected;
    }

    public EMultipleAnswerType geteMultipleAnswerType() {
        return eMultipleAnswerType;
    }
    public void seteMultipleAnswerType(EMultipleAnswerType eMultipleAnswerType) {
        this.eMultipleAnswerType = eMultipleAnswerType;
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
