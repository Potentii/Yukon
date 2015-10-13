package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by poten on 12/10/2015.
 */
public class TeacherConfigs extends JSONObject{
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum TeacherConfigsJSONKeys{
        STUDENT_CONFIGS_FILE_ID_ARRAY("studentConfigsFileIdArray"),
        CORRECT_ANSWERS_FILE_ID("correctAnswersFileId"),
        EXAM_FILE_ID("examFileId");

        private String key;
        private TeacherConfigsJSONKeys(String key){
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
    public TeacherConfigs(String[] studentConfigsFileIdArray, String correctAnswersFileId, String examFileId){
        super();
        try {
            this.setStudentConfigsFileIdArray(studentConfigsFileIdArray);
            this.setCorrectAnswersFileId(correctAnswersFileId);
            this.setExamFileId(examFileId);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public TeacherConfigs(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *StudentConfigsFileIdArray:
    public String[] getStudentConfigsFileIdArray(){
        JSONArray studentConfigsFileIdJSONArray = super.optJSONArray(TeacherConfigsJSONKeys.STUDENT_CONFIGS_FILE_ID_ARRAY.getKey());
        String[] studentConfigsFileIdArray = new String[studentConfigsFileIdJSONArray.length()];
        for(int i=0; i<studentConfigsFileIdJSONArray.length(); i++){
            try {
                studentConfigsFileIdArray[i] = studentConfigsFileIdJSONArray.getString(i);
            }catch (JSONException e){}
        }
        return studentConfigsFileIdArray;
    }
    public void setStudentConfigsFileIdArray(String[] studentConfigsFileIdArray) throws JSONException{
        super.putOpt(TeacherConfigsJSONKeys.STUDENT_CONFIGS_FILE_ID_ARRAY.getKey(), new JSONArray(studentConfigsFileIdArray));
    }

    // *CorrectAnswersFileId:
    public String getCorrectAnswersFileId(){
        return super.optString(TeacherConfigsJSONKeys.CORRECT_ANSWERS_FILE_ID.getKey());
    }
    public void setCorrectAnswersFileId(String correctAnswersFileId) throws JSONException{
        super.putOpt(TeacherConfigsJSONKeys.CORRECT_ANSWERS_FILE_ID.getKey(), correctAnswersFileId);
    }

    // *ExamFileId:
    public String getExamFileId(){
        return super.optString(TeacherConfigsJSONKeys.EXAM_FILE_ID.getKey());
    }
    public void setExamFileId(String examFileId) throws JSONException{
        super.putOpt(TeacherConfigsJSONKeys.EXAM_FILE_ID.getKey(), examFileId);
    }
}
