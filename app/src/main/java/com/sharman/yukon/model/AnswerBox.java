package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 05/10/2015.
 */
public class AnswerBox extends JSONObject {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum AnswerBoxJSONKeys{
        TITLE_ARRAY("titleArray"),
        EANSWER_TYPE("eAnswerType");

        private String key;
        private AnswerBoxJSONKeys(String key){
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
    public AnswerBox(EAnswerType eAnswerType){
        this(new String[0], eAnswerType);
    }
    public AnswerBox(String[] titleArray, EAnswerType eAnswerType){
        super();
        try{
            this.setTitleArray(titleArray);
            this.setEAnswerType(eAnswerType);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public AnswerBox(String jsonStr) throws JSONException {
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *TitleArray:
    public String[] getTitleArray(){
        JSONArray titleJSONArray = super.optJSONArray(AnswerBoxJSONKeys.TITLE_ARRAY.getKey());
        String[] titleArray = new String[titleJSONArray.length()];
        for(int i=0; i<titleJSONArray.length(); i++){
            try {
                titleArray[i] = titleJSONArray.getString(i);
            }catch (JSONException e){}
        }
        return titleArray;
    }
    public void setTitleArray(String[] titleArray) throws JSONException{
        super.putOpt(AnswerBoxJSONKeys.TITLE_ARRAY.getKey(), new JSONArray(titleArray));
    }

    // *EAnswerType:
    public EAnswerType getEAnswerType(){
        return EAnswerType.getEAnswerType(super.optString(AnswerBoxJSONKeys.EANSWER_TYPE.getKey()));
    }
    public void setEAnswerType(EAnswerType eAnswerType) throws JSONException{
        super.putOpt(AnswerBoxJSONKeys.EANSWER_TYPE.getKey(), eAnswerType.getName());
    }
}
