package com.sharman.yukon.model;

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
