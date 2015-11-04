package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        EXAM_FILE_ID("examFileId"),
        STUDENT("student"),

        EXAM_TITLE_CACHE("examTitleCache"),
        EXAM_DELIVERY_DATE_CACHE("examDeliveryDateCache"),
        EXAM_SUBJECT_CACHE("examSubjectCache"),
        TEACHER_ID_CACHE("teacherIdCache");

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
    public StudentConfigs(String gradeFileId, String answersFileId, String examFileId, String student, String examTitleCache, Date examDeliveryDateCache, String examSubjectCache, String teacherIdCache){
        super();
        try {
            this.setGradeFileId(gradeFileId);
            this.setAnswersFileId(answersFileId);
            this.setExamFileId(examFileId);
            this.setStudent(student);

            this.setExamTitleCache(examTitleCache);
            this.setExamDeliveryDateCache(examDeliveryDateCache);
            this.setExamSubjectCache(examSubjectCache);
            this.setTeacherIdCache(teacherIdCache);
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

    // *Student
    public String getStudent(){
        return super.optString(StudentConfigsJSONKeys.STUDENT.getKey());
    }
    public void setStudent(String student) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.STUDENT.getKey(), student);
    }


    // *ExamTitleCache
    public String getExamTitleCache(){
        return super.optString(StudentConfigsJSONKeys.EXAM_TITLE_CACHE.getKey());
    }
    public void setExamTitleCache(String examTitleCache) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.EXAM_TITLE_CACHE.getKey(), examTitleCache);
    }

    // *ExamDeliveryDateCache
    public Date getExamDeliveryDateCache(){
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(super.optString(StudentConfigsJSONKeys.EXAM_DELIVERY_DATE_CACHE.getKey()));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setExamDeliveryDateCache(Date examDeliveryDateCache) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.EXAM_DELIVERY_DATE_CACHE.getKey(), new SimpleDateFormat("dd/MM/yyyy").format(examDeliveryDateCache));
    }

    // *ExamSubjectCache
    public String getExamSubjectCache(){
        return super.optString(StudentConfigsJSONKeys.EXAM_SUBJECT_CACHE.getKey());
    }
    public void setExamSubjectCache(String examSubjectCache) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.EXAM_SUBJECT_CACHE.getKey(), examSubjectCache);
    }

    // *TeacherIdCache
    public String getTeacherIdCache(){
        return super.optString(StudentConfigsJSONKeys.TEACHER_ID_CACHE.getKey());
    }
    public void setTeacherIdCache(String teacherIdCache) throws JSONException{
        super.putOpt(StudentConfigsJSONKeys.TEACHER_ID_CACHE.getKey(), teacherIdCache);
    }
}
