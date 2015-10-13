package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 12/10/2015.
 */
public class StudentConfigs extends JSONObject {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum StudentConfigsJSONKeys{
        GRADE_FILE_ID("gradeFileId"),
        ANSWERS_FILE_ID("answersFileId"),
        EXAM_FILE_ID("examFileId");

        private String key;
        private StudentConfigsJSONKeys(String key){
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
    public StudentConfigs(String gradeFileId, String answersFileId, String examFileId){
        super();
        try {
            this.setGradeFileId(gradeFileId);
            this.setAnswersFileId(answersFileId);
            this.setExamFileId(examFileId);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public StudentConfigs(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *GradeFileId:
    public String getGradeFileId(){
        return super.optString(StudentConfigsJSONKeys.GRADE_FILE_ID.getKey());
    }
    public void setGradeFileId(String gradeFileId) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.GRADE_FILE_ID.getKey(), gradeFileId);
    }

    // *AnswersFileId:
    public String getAnswersFileId(){
        return super.optString(StudentConfigsJSONKeys.ANSWERS_FILE_ID.getKey());
    }
    public void setAnswersFileId(String answersFileId) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.ANSWERS_FILE_ID.getKey(), answersFileId);
    }

    // *ExamFileId:
    public String getExamFileId(){
        return super.optString(StudentConfigsJSONKeys.EXAM_FILE_ID.getKey());
    }
    public void setExamFileId(String examFileId) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.EXAM_FILE_ID.getKey(), examFileId);
    }
}
