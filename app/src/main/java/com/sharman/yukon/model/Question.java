package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 04/10/2015.
 */
public class Question extends JSONObject{
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum QuestionJSONKeys{
        TITLE("title"),
        WEIGHT("weight"),
        ANSWER_BOX("answerBox");

        private String key;
        private QuestionJSONKeys(String key){
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
    public Question(String title, double weight, AnswerBox answerBox){
        super();
        try {
            this.setTitle(title);
            this.setWeight(weight);
            this.setAnswerBox(answerBox);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public Question(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Title:
    public String getTitle(){
        return super.optString(QuestionJSONKeys.TITLE.getKey());
    }
    public void setTitle(String title) throws JSONException{
        super.putOpt(QuestionJSONKeys.TITLE.getKey(), title);
    }

    // *Weight:
    public double getWeight(){
        return super.optDouble(QuestionJSONKeys.WEIGHT.getKey());
    }
    public void setWeight(double weight) throws JSONException{
        super.putOpt(QuestionJSONKeys.WEIGHT.getKey(), weight);
    }

    // *AnswerBox:
    public AnswerBox getAnswerBox(){
        try {
            return new AnswerBox(super.getJSONObject(QuestionJSONKeys.ANSWER_BOX.getKey()).toString());
        } catch (JSONException e){
            return null;
        }
    }
    public void setAnswerBox(AnswerBox answerBox) throws JSONException{
        super.putOpt(QuestionJSONKeys.ANSWER_BOX.getKey(), answerBox);
    }
}
