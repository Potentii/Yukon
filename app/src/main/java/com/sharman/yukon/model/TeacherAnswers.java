package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 12/10/2015.
 */
public class TeacherAnswers extends JSONObject{
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum TeacherAnswersJSONKeys{
        WEIGHT_TYPE_ANSWER_STRUCT_ARRAY("weightTypeAnswerStructArray");

        private String key;
        private TeacherAnswersJSONKeys(String key){
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
    public TeacherAnswers(WeightTypeAnswerStruct[] weightTypeAnswerStructArray){
        super();
        try {
            this.setWeightTypeAnswerStructArray(weightTypeAnswerStructArray);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public TeacherAnswers(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *WeightTypeAnswerStructArray:
    public WeightTypeAnswerStruct[] getWeightTypeAnswerStructArray(){
        JSONArray weightTypeAnswerStructJSONArray = super.optJSONArray(TeacherAnswersJSONKeys.WEIGHT_TYPE_ANSWER_STRUCT_ARRAY.getKey());
        WeightTypeAnswerStruct[] weightTypeAnswerStructArray = new WeightTypeAnswerStruct[weightTypeAnswerStructJSONArray.length()];
        for(int i=0; i<weightTypeAnswerStructJSONArray.length(); i++){
            try {
                weightTypeAnswerStructArray[i] = new WeightTypeAnswerStruct(weightTypeAnswerStructJSONArray.getJSONObject(i).toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return weightTypeAnswerStructArray;
    }
    public void setWeightTypeAnswerStructArray(WeightTypeAnswerStruct[] weightTypeAnswerStructArray) throws JSONException{
        super.putOpt(TeacherAnswersJSONKeys.WEIGHT_TYPE_ANSWER_STRUCT_ARRAY.getKey(), new JSONArray(weightTypeAnswerStructArray));
    }
}
