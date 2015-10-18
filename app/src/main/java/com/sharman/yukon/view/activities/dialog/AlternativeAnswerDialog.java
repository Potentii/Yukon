package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.sharman.yukon.R;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.util.AnswerAlternativePair;
import com.sharman.yukon.view.activities.util.DialogCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by poten on 17/10/2015.
 */
public class AlternativeAnswerDialog extends DialogFragment {
    private List<View> answerRowList = new ArrayList<>();
    private LinearLayout answerDiv;
    private LayoutInflater layoutInflater;
    private DialogCallback dialogCallback;

    private EMultipleAnswerType eMultipleAnswerType;
    private List<AnswerAlternativePair> answerAlternativePairList;

    public AlternativeAnswerDialog(){
        answerAlternativePairList = new ArrayList<>();
        eMultipleAnswerType = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_alternative_answer, null);
        answerDiv = (LinearLayout) view.findViewById(R.id.alternativeAnswerDiv);

        builder.setView(view);


        removeAllAnswerRow();


        for(int i=0; i< answerAlternativePairList.size(); i++) {
            addAnswerRow(answerAlternativePairList.get(i).isCorrect(), answerAlternativePairList.get(i).getQuestion(), false);
        }
        addAnswerRow(false, "", true);


        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                answerAlternativePairList.clear();

                for (int i = 0; i < answerRowList.size()-1; i++) {
                    EditText alternativeIn = (EditText) answerRowList.get(i).findViewById(R.id.alternativeIn);
                    CompoundButton answerIn = (CompoundButton) answerRowList.get(i).findViewById(R.id.answerIn);

                    answerAlternativePairList.add(new AnswerAlternativePair(answerIn.isChecked(), alternativeIn.getText().toString()));
                }

                try {
                    dialogCallback.onPositive();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogCallback.onNegative();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        return builder.create();
    }


    private void addAnswerRow(boolean isSelected, String text, final boolean lastRow){
        final View answerRow;

        switch (eMultipleAnswerType){
        case MULTIPLE_CHOICE:
            answerRow = layoutInflater.inflate(R.layout.row_multiple_choice_answer, null);
            break;
        case SINGLE_CHOICE:
            answerRow = layoutInflater.inflate(R.layout.row_single_choice_answer, null);
            break;
        default:
            answerRow = layoutInflater.inflate(R.layout.row_single_choice_answer, null);
            break;
        }

        final CompoundButton answerIn = (CompoundButton) answerRow.findViewById(R.id.answerIn);
        answerIn.setChecked(isSelected);
        if(lastRow){
            answerIn.setChecked(false);
            answerIn.setVisibility(View.INVISIBLE);
        }



        EditText alternativeIn = (EditText) answerRow.findViewById(R.id.alternativeIn);
        alternativeIn.setText(text);
        alternativeIn.addTextChangedListener(new TextWatcher() {
            private boolean hasCreatedView = !lastRow;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (!hasCreatedView && charSequence.length() > 0) {
                    addAnswerRow(false, "", true);
                    answerIn.setVisibility(View.VISIBLE);
                    hasCreatedView = true;
                } else if (hasCreatedView && charSequence.length() == 0) {
                    hasCreatedView = false;
                    answerIn.setChecked(false);
                    answerIn.setVisibility(View.INVISIBLE);
                    removeAnswerRow(answerRow);
                }
            }
        });
        answerDiv.addView(answerRow);
        answerRowList.add(answerRow);
    }


    private void removeAnswerRow(View answerRow){
        answerDiv.removeView(answerRow);
        answerRowList.remove(answerRow);
    }
    private void removeAllAnswerRow(){
        answerDiv.removeAllViews();
        answerRowList.clear();
    }



    public void radioButton_onClick(View view){
        for(int i=0; i<answerRowList.size(); i++){
            RadioButton radioButton = (RadioButton) answerRowList.get(i).findViewById(R.id.answerIn);
            if(!radioButton.equals((RadioButton)view)){
                radioButton.setChecked(false);
            }
        }
    }



    public EMultipleAnswerType getEMultipleAnswerType() {
        return eMultipleAnswerType;
    }
    public void setEMultipleAnswerType(EMultipleAnswerType eMultipleAnswerType) {
        if(this.eMultipleAnswerType != null && !this.eMultipleAnswerType.equals(eMultipleAnswerType)){

            for(int i=0; i< answerAlternativePairList.size(); i++){
                answerAlternativePairList.get(i).setCorrect(false);
            }
        }

        this.eMultipleAnswerType = eMultipleAnswerType;
    }

    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public List<AnswerAlternativePair> getAnswerAlternativePairList() {
        return answerAlternativePairList;
    }
}
