package com.sharman.yukon.model;

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
}
