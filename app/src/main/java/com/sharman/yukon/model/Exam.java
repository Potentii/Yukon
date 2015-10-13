package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by poten on 04/10/2015.
 */
public class Exam extends JSONObject{
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum ExamJSONKeys{
        TITLE("title"),
        DELIVER_DATE("deliverDate"),
        TEACHER_ID("teacherId"),
        SUBJECT("subject"),
        QUESTION_ARRAY("questionArray");

        private String key;
        private ExamJSONKeys(String key){
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
    public Exam(String title, Date deliverDate, String teacherId, String subject, Question[] questionArray){
        super();
        try {
            this.setTitle(title);
            this.setDeliverDate(deliverDate);
            this.setTeacherId(teacherId);
            this.setSubject(subject);
            this.setQuestionArray(questionArray);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public Exam(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Title:
    public String getTitle(){
        return super.optString(ExamJSONKeys.TITLE.getKey());
    }
    public void setTitle(String title) throws JSONException{
        super.putOpt(ExamJSONKeys.TITLE.getKey(), title);
    }

    // *DeliverDate:
    public Date getDeliverDate(){
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(super.optString(ExamJSONKeys.DELIVER_DATE.getKey()));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setDeliverDate(Date deliverDate) throws JSONException{
        super.putOpt(ExamJSONKeys.DELIVER_DATE.getKey(), new SimpleDateFormat("dd/MM/yyyy").format(deliverDate));
    }

    // *TeacherId:
    public String getTeacherId(){
        return super.optString(ExamJSONKeys.TEACHER_ID.getKey());
    }
    public void setTeacherId(String teacherId) throws JSONException{
        super.putOpt(ExamJSONKeys.TEACHER_ID.getKey(), teacherId);
    }

    // *Subject:
    public String getSubject(){
        return super.optString(ExamJSONKeys.SUBJECT.getKey());
    }
    public void setSubject(String subject) throws JSONException{
        super.putOpt(ExamJSONKeys.SUBJECT.getKey(), subject);
    }

    // *QuestionArray:
    public Question[] getQuestionArray(){
        JSONArray questionJSONArray = super.optJSONArray(ExamJSONKeys.QUESTION_ARRAY.getKey());
        Question[] questionArray = new Question[questionJSONArray.length()];
        for(int i=0; i<questionJSONArray.length(); i++){
            try {
                questionArray[i] = new Question(questionJSONArray.getJSONObject(i).toString());
            }catch (JSONException e){}
        }
        return questionArray;
    }
    public void setQuestionArray(Question[] questionArray) throws JSONException{
        super.putOpt(ExamJSONKeys.QUESTION_ARRAY.getKey(), new JSONArray(questionArray));
    }
}
