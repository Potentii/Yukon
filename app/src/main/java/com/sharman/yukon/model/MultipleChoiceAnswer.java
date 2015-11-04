package com.sharman.yukon.model;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by poten on 20/10/2015.
 */
public class MultipleChoiceAnswer extends Answer{


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public MultipleChoiceAnswer(int[] answer){
        super();
        try {
            this.setAnswer(answer);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    @Override
    public String getFormattedAnswerString(){
        String answerStr = "";
        int[] answer = getAnswer();

        for(int i=0; i<answer.length; i++){
            answerStr += convertIntIndexToStringIndex(answer[i]) + (i<answer.length-1?", ":"");
        }

        return answerStr;
    }

    @Override
    public Boolean compareAnswerTo(Answer otherAnswer){
        try{
            MultipleChoiceAnswer otherAnswerCasted = (MultipleChoiceAnswer) otherAnswer;

            int[] thisAnswerArray = getAnswer();
            int[] otherAnswerArray = otherAnswerCasted.getAnswer();

            if(thisAnswerArray.length != otherAnswerArray.length){
                return false;
            }

            for(int i=0; i<thisAnswerArray.length; i++){
                if(thisAnswerArray[i] != otherAnswerArray[i]){
                    return false;
                }
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
    public int[] getAnswer(){
        JSONArray answerJSONArray = super.optJSONArray(AnswerJSONKeys.ANSWER.getKey());
        int[] answerArray = new int[answerJSONArray.length()];
        for(int i=0; i<answerJSONArray.length(); i++){
            try {
                answerArray[i] = answerJSONArray.getInt(i);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return answerArray;
    }
    public void setAnswer(int[] answer) throws JSONException{
        super.putOpt(AnswerJSONKeys.ANSWER.getKey(), new JSONArray(answer));
    }
}
