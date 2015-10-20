package com.sharman.yukon.model;

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
