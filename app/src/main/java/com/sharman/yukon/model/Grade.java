package com.sharman.yukon.model;

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
        GRADE("grade");

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
    public Grade(double grade){
        super();
        try {
            this.setGrade(grade);
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
}
