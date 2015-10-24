package com.sharman.yukon.model;

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
