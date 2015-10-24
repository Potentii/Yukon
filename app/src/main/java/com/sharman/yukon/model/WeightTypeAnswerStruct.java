package com.sharman.yukon.model;

import com.sharman.yukon.model.util.EMultipleAnswerType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 20/10/2015.
 */
public class WeightTypeAnswerStruct extends JSONObject {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum WeightTypeAnswerStructJSONKeys{
        WEIGHT("weight"),
        EMULTIPLE_ANSWER_TYPE("eMultipleAnswerType"),
        ANSWER("answer");

        private String key;
        private WeightTypeAnswerStructJSONKeys(String key){
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
    public WeightTypeAnswerStruct(double weight, EMultipleAnswerType eMultipleAnswerType, Answer answer){
        super();
        try {
            this.setWeight(weight);
            this.setEMultipleAnswerType(eMultipleAnswerType);
            this.setAnswer(answer);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public WeightTypeAnswerStruct(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Weight:
    public double getWeight(){
        return super.optDouble(WeightTypeAnswerStructJSONKeys.WEIGHT.getKey());
    }
    public void setWeight(double weight) throws JSONException{
        super.putOpt(WeightTypeAnswerStructJSONKeys.WEIGHT.getKey(), weight);
    }

    // *EMultipleAnswerType:
    public EMultipleAnswerType getEMultipleAnswerType(){
        return EMultipleAnswerType.getEMultipleAnswerType(super.optString(WeightTypeAnswerStructJSONKeys.EMULTIPLE_ANSWER_TYPE.getKey()));
    }
    public void setEMultipleAnswerType(EMultipleAnswerType eMultipleAnswerType) throws JSONException{
        try {
            super.putOpt(WeightTypeAnswerStructJSONKeys.EMULTIPLE_ANSWER_TYPE.getKey(), eMultipleAnswerType.getName());
        } catch(NullPointerException e){
            super.putOpt(WeightTypeAnswerStructJSONKeys.EMULTIPLE_ANSWER_TYPE.getKey(), "");
        }
    }

    // *Answer:
    public Answer getAnswer(){
        try {
            return new Answer(super.optJSONObject(WeightTypeAnswerStructJSONKeys.ANSWER.getKey()).toString());
        }catch (JSONException e){
            return null;
        }
    }
    public void setAnswer(Answer answer) throws JSONException{
        super.putOpt(WeightTypeAnswerStructJSONKeys.ANSWER.getKey(), answer);
    }
}
