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
        EQUESTION_TYPE("eQuestionType"),
        WEIGHT("weight"),
        ANSWER_ROW_ARRAY("answerRowArray");

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
    public Question(String title, EQuestionType eQuestionType, double weight, AnswerRow[] answerRowArray){
        super();
        try {
            this.setTitle(title);
            this.setEQuestionType(eQuestionType);
            this.setWeight(weight);
            this.setAnswerRowArray(answerRowArray);
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

    // *EQuestionType:
    public EQuestionType getEQuestionType(){
        return EQuestionType.getEQuestionType(super.optString(QuestionJSONKeys.EQUESTION_TYPE.getKey()));
    }
    public void setEQuestionType(EQuestionType eQuestionType) throws JSONException{
        super.putOpt(QuestionJSONKeys.EQUESTION_TYPE.getKey(), eQuestionType.getName());
    }

    // *Weight:
    public double getWeight(){
        return super.optDouble(QuestionJSONKeys.WEIGHT.getKey());
    }
    public void setWeight(double weight) throws JSONException{
        super.putOpt(QuestionJSONKeys.WEIGHT.getKey(), weight);
    }

    // *AnswerRowArray:
    public AnswerRow[] getAnswerRowArray(){
        JSONArray answerRowJSONArray = super.optJSONArray(QuestionJSONKeys.ANSWER_ROW_ARRAY.getKey());
        AnswerRow[] answerRowArray = new AnswerRow[answerRowJSONArray.length()];
        for(int i=0; i<answerRowJSONArray.length(); i++){
            try {
                answerRowArray[i] = new AnswerRow(answerRowJSONArray.getJSONObject(i).toString());
            }catch (JSONException e){}
        }
        return answerRowArray;
    }
    public void setAnswerRowArray(AnswerRow[] answerRowArray) throws JSONException{
        super.putOpt(QuestionJSONKeys.ANSWER_ROW_ARRAY.getKey(), new JSONArray(answerRowArray));
    }
}
