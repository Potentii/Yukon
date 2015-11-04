package com.sharman.yukon.model;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 20/10/2015.
 */
public class Answer extends JSONObject {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum AnswerJSONKeys{
        ANSWER("answer");

        private String key;
        private AnswerJSONKeys(String key){
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
    public Answer(){
        super();
    }
    public Answer(String jsonStr) throws JSONException{
        super(jsonStr);
    }

    public String getFormattedAnswerString(){
        return "";
    }
    @Nullable
    public Boolean compareAnswerTo(Answer otherAnswer){
        return false;
    }

    public static String convertIntIndexToStringIndex(int index){
        String stringIndex = "";
        if(index>=0){
            int loops = ((int) Math.floor(index / 26)) + 1;
            int alphabeticalPos = index - ((loops - 1) * 26);

            for (int i = 0; i < loops; i++) {
                char letter = (char) (alphabeticalPos + 1 + 64);
                stringIndex += letter;
            }
        }

        return stringIndex;
    }
}
