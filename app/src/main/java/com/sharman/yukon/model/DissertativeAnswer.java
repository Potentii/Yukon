package com.sharman.yukon.model;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 20/10/2015.
 */
public class DissertativeAnswer extends Answer{


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public DissertativeAnswer(String answer){
        super();
        try {
            this.setAnswer(answer);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    @Override
    public String getFormattedAnswerString(){
        return getAnswer();
    }


    @Override
    @Nullable
    public Boolean compareAnswerTo(Answer otherAnswer){
        return null;
        /*
        try{
            DissertativeAnswer otherAnswerCasted = (DissertativeAnswer) otherAnswer;

            if(!getAnswer().equals(otherAnswerCasted.getAnswer())){
                return false;
            }

            return true;
        } catch (ClassCastException e){
            return false;
        }
        */
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Answer:
    public String getAnswer(){
        return super.optString(AnswerJSONKeys.ANSWER.getKey());
    }
    public void setAnswer(String answer) throws JSONException{
        super.putOpt(AnswerJSONKeys.ANSWER.getKey(), answer);
    }
}
