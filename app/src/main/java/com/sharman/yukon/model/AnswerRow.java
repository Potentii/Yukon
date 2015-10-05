package com.sharman.yukon.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 04/10/2015.
 */
public class AnswerRow extends JSONObject{
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public AnswerRow(){}
    public AnswerRow(String jsonStr) throws JSONException{
        super(jsonStr);
    }
}
