package com.sharman.yukon.model;

import android.support.annotation.Nullable;

import org.json.JSONException;

/**
 * Created by poten on 20/10/2015.
 */
public class SingleChoiceAnswer extends Answer{


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public SingleChoiceAnswer(int answer){
        super();
        try {
            this.setAnswer(answer);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    @Override
    public String getFormattedAnswerString(){
        int answer = getAnswer();
        String answerStr = convertIntIndexToStringIndex(answer);
        return answerStr;
    }

    @Override
    public Boolean compareAnswerTo(Answer otherAnswer){
        try{
            SingleChoiceAnswer otherAnswerCasted = (SingleChoiceAnswer) otherAnswer;
            
            if(getAnswer() != otherAnswerCasted.getAnswer()){
                return false;
            }

            return true;
        } catch (ClassCastException e){
            return false;
        }
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Answer:
    public int getAnswer(){
        return super.optInt(AnswerJSONKeys.ANSWER.getKey());
    }
    public void setAnswer(int answer) throws JSONException{
        super.putOpt(AnswerJSONKeys.ANSWER.getKey(), answer);
    }
}
