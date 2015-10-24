package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by poten on 12/10/2015.
 */
public class StudentAnswers extends JSONObject{
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum StudentAnswersJSONKeys{
        ANSWER_ARRAY("answerArray");

        private String key;
        private StudentAnswersJSONKeys(String key){
            this.key = key;
        }
        public String getKey(){
            return this.key;
        }
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public StudentAnswers(Answer[] answerArray){
        super();
        try {
            this.setAnswerArray(answerArray);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public StudentAnswers(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *AnswerArray:
    public Answer[] getAnswerArray(){
        JSONArray answerJSONArray = super.optJSONArray(StudentAnswersJSONKeys.ANSWER_ARRAY.getKey());
        Answer[] answerArray = new Answer[answerJSONArray.length()];
        for(int i=0; i<answerJSONArray.length(); i++){
            try {
                answerArray[i] = new Answer(answerJSONArray.getJSONObject(i).toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return answerArray;
    }
    public void setAnswerArray(Answer[] answerArray) throws JSONException{
        super.putOpt(StudentAnswersJSONKeys.ANSWER_ARRAY.getKey(), new JSONArray(answerArray));
    }
}
