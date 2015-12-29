package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import javax.annotation.Nonnull;

/**
 * Created by poten on 12/10/2015.
 */
public class StudentAnswers extends JSONObject{
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum StudentAnswersJSONKeys{
        ANSWER_ARRAY("answerArray");

        private String key;
        private StudentAnswersJSONKeys(String key){
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
    public StudentAnswers(Answer[] answerArray){
        super();
        try {
            this.setAnswerArray(answerArray);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public StudentAnswers(String jsonStr) throws JSONException{
        super(jsonStr);
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Class methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void syncWithTeacherAnswer(@Nonnull TeacherAnswers teacherAnswers){
        Answer[] studentAnswerArray_OLD = getAnswerArray();

        try {
            WeightTypeAnswerStruct[] teacherAnswerArray = teacherAnswers.getWeightTypeAnswerStructArray();
            Answer[] studentAnswerArray_NEW = new Answer[teacherAnswerArray.length];


            for (int i = 0; i < teacherAnswerArray.length; i++) {
                Answer teacherAnswer = teacherAnswerArray[i].getAnswer();
                long QID = teacherAnswer.getQID();
                Answer foundStudentAnswerWithQID = null;

                // *Trying to search an answer with the QID:
                for (Answer studentAnswer : studentAnswerArray_OLD) {
                    if (studentAnswer.getQID() == QID) {
                        foundStudentAnswerWithQID = studentAnswer;
                        break;
                    }
                }


                if (foundStudentAnswerWithQID != null) {
                    // *If found an answer with the QID:
                    studentAnswerArray_NEW[i] = foundStudentAnswerWithQID;
                } else {
                    // *If not:
                    studentAnswerArray_NEW[i] = new Answer(QID, new String[0]);
                }


            }

            setAnswerArray(studentAnswerArray_NEW);
        } catch (Exception e){
            e.printStackTrace();
            try {
                setAnswerArray(studentAnswerArray_OLD);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *AnswerArray:
    public Answer[] getAnswerArray(){
        JSONArray answerJSONArray = super.optJSONArray(StudentAnswersJSONKeys.ANSWER_ARRAY.getKey());
        Answer[] answerArray = new Answer[answerJSONArray.length()];
        for(int i=0; i<answerJSONArray.length(); i++){
            try {
                answerArray[i] = new Answer(answerJSONArray.getJSONObject(i).toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return answerArray;
    }
    public void setAnswerArray(Answer[] answerArray) throws JSONException{
        super.putOpt(StudentAnswersJSONKeys.ANSWER_ARRAY.getKey(), new JSONArray(answerArray));
    }
}
