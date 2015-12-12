package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.model.util.EMultipleAnswerType;
import com.sharman.yukon.view.activities.util.AnswerAlternativePair;
import com.sharman.yukon.view.activities.util.CompoundButtonController;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.Validatable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;


/**
 * Created by poten on 17/10/2015.
 */
public class AlternativeAnswerDialog extends DialogFragment implements Validatable{
    private static int ROW_REMOVE_BTN_ID = R.id.removeBtn;
    private static int ROW_COMPOUND_BTN_ID = R.id.compoundBtn;
    private static int ROW_SINGLE_CHOICE_LAYOUT = R.layout.row_alternative_single_creating;
    private static int ROW_MULTIPLE_CHOICE_LAYOUT = R.layout.row_alternative_multiple_creating;
    private static int MAIN_LAYOUT = R.layout.dialog_alternative_answer;

    private LayoutInflater layoutInflater;
    private CompoundButtonController compoundButtonController = new CompoundButtonController();

    private LinearLayout rowContainer;
    private EditText alternativeIn;

    private List<CompoundButton> compoundButtonList = new ArrayList<>();
    private List<AnswerAlternativePair> answerAlternativePairList = new ArrayList<>();

    @Nonnull
    private EMultipleAnswerType eMultipleAnswerType;
    @Nonnull
    private DialogCallback dialogCallback;

    private Context context;

    private String invalidText = "";



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layoutInflater = getActivity().getLayoutInflater();


        // *Finding views:
        View view = layoutInflater.inflate(MAIN_LAYOUT, null);
        rowContainer = (LinearLayout) view.findViewById(R.id.rowContainer);
        alternativeIn = (EditText) view.findViewById(R.id.alternativeIn);


        // *Setting alternative input listener:
        alternativeIn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // *The user finished to edit the text:
                    addRow(new AnswerAlternativePair(false, alternativeIn.getText().toString()));
                    alternativeIn.setText("");
                    return true;
                }
                return false;
            }
        });


        // *Setting main view to fragment:
        builder.setView(view);


        // *Initial clean:
        removeAllRow();


        // *Initial auto fill:
        for (AnswerAlternativePair answerAlternativePair : answerAlternativePairList) {
            addRow(answerAlternativePair);
        }


        // *Positive listener:
        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                answerAlternativePairList.clear();


                for (CompoundButton compoundButton : compoundButtonList) {
                    answerAlternativePairList.add(new AnswerAlternativePair(compoundButton.isChecked(), compoundButton.getText().toString()));
                }


                try {
                    dialogCallback.onPositive();
                } catch (NullPointerException e) {
                    System.err.println("Forgot to set the DialogCallback");
                }
            }
        });


        // *Negative listener
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogCallback.onNegative();
                } catch (NullPointerException e) {
                    System.err.println("Forgot to set the DialogCallback");
                }
            }
        });

        return builder.create();
    }


    private void addRow(final AnswerAlternativePair answerAlternativePair){
        final View row;

        // *Setting the row xml:
        if(eMultipleAnswerType == EMultipleAnswerType.SINGLE_CHOICE){
            row = layoutInflater.inflate(ROW_SINGLE_CHOICE_LAYOUT, null);
        } else {
            row = layoutInflater.inflate(ROW_MULTIPLE_CHOICE_LAYOUT, null);
        }

        // *Remove row listener:
        ImageButton removeBtn = (ImageButton) row.findViewById(ROW_REMOVE_BTN_ID);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRow(row);
            }
        });

        // *RadioButton listener:
        final CompoundButton compoundButton = (CompoundButton) row.findViewById(ROW_COMPOUND_BTN_ID);
        compoundButtonController.addCompoundButton(compoundButton);

        // *On long click listener (makes it edit the current alternative):
        compoundButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                alternativeIn.setText(answerAlternativePair.getAlternative());
                return false;
            }
        });

        // *Fill the text and check state of the row:
        compoundButton.setText(answerAlternativePair.getAlternative());
        compoundButton.setChecked(answerAlternativePair.isCorrect());

        // *Adding to the list and layout:
        compoundButtonList.add(compoundButton);
        rowContainer.addView(row);
    }


    private void removeRow(View row) {
        compoundButtonList.remove((CompoundButton) row.findViewById(ROW_COMPOUND_BTN_ID));
        rowContainer.removeView(row);
    }

    private void removeAllRow(){
        compoundButtonList.clear();
        rowContainer.removeAllViews();
    }


    public void cleanAnswer(){
        try{
            List<AnswerAlternativePair> answerAlternativePairListClean = new ArrayList<>();
            for (CompoundButton compoundButton : compoundButtonList) {
                compoundButton.setChecked(false);
                answerAlternativePairListClean.add(new AnswerAlternativePair(compoundButton.isChecked(), compoundButton.getText().toString()));
            }
            answerAlternativePairList = new ArrayList<>(answerAlternativePairListClean);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void seteMultipleAnswerType(@Nonnull EMultipleAnswerType eMultipleAnswerType) {
        this.eMultipleAnswerType = eMultipleAnswerType;
    }

    public void setDialogCallback(@Nonnull DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public List<AnswerAlternativePair> getAnswerAlternativePairList() {
        return answerAlternativePairList;
    }

    public void setAnswerAlternativePairList(List<AnswerAlternativePair> answerAlternativePairList) {
        this.answerAlternativePairList = new ArrayList<>(answerAlternativePairList);
    }

    public void setContext(Context context) {
        this.context = context;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Validatable methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean isValid() {
        boolean hasAnswer = false;
        for (AnswerAlternativePair answerAlternativePair : answerAlternativePairList) {
            if(answerAlternativePair.isCorrect()){
                hasAnswer = true;
                break;
            }
        }

        if(answerAlternativePairList.size() == 0){
            try {
                invalidText = context.getResources().getString(R.string.output_invalidField_alternativeAnswer_empty);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            return false;
        }

        if(!hasAnswer){
            try {
                invalidText = context.getResources().getString(R.string.output_invalidField_alternativeAnswer_noAnswers);
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
