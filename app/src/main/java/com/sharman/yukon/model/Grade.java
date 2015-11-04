package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by poten on 12/10/2015.
 */
public class Grade extends JSONObject {
    /*
    *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
    *  * Keys enum:
    *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
    */
    protected enum GradeJSONKeys{
        GRADE("grade"),
        CORRECTION_ARRAY("correctionArray");

        private String key;
        private GradeJSONKeys(String key){
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
    public Grade(double grade, Boolean[] correctionArray){
        super();
        try {
            this.setGrade(grade);
            this.setCorrectionArray(correctionArray);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public Grade(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Grade:
    public double getGrade(){
        return super.optDouble(GradeJSONKeys.GRADE.getKey());
    }
    public void setGrade(double grade) throws JSONException{
        super.putOpt(GradeJSONKeys.GRADE.getKey(), grade);
    }

    // *CorrectionArray:
    public Boolean[] getCorrectionArray(){
        JSONArray correctionJSONArray = super.optJSONArray(GradeJSONKeys.CORRECTION_ARRAY.getKey());
        Boolean[] correctionArray = new Boolean[correctionJSONArray.length()];
        for(int i=0; i<correctionJSONArray.length(); i++){
            try {
                correctionArray[i] = correctionJSONArray.getBoolean(i);
            }catch (JSONException e){
                correctionArray[i] = null;
            }
        }
        return correctionArray;
    }
    public void setCorrectionArray(Boolean[] correctionArray) throws JSONException{
        super.putOpt(GradeJSONKeys.CORRECTION_ARRAY.getKey(), new JSONArray(correctionArray));
    }
}
