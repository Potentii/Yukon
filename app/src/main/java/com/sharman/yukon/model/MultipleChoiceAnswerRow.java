package com.sharman.yukon.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by poten on 05/10/2015.
 */
public class MultipleChoiceAnswerRow extends AnswerRow {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum MultipleChoiceAnswerRowJSONKeys{
        TITLE("title");

        private String key;
        private MultipleChoiceAnswerRowJSONKeys(String key){
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
    public MultipleChoiceAnswerRow(String title, int i){
        super();
        try {
            this.setTitle(title);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public MultipleChoiceAnswerRow(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Title:
    public String getTitle(){
        return super.optString(MultipleChoiceAnswerRowJSONKeys.TITLE.getKey());
    }
    public void setTitle(String title) throws JSONException{
        super.putOpt(MultipleChoiceAnswerRowJSONKeys.TITLE.getKey(), title);
    }
}
