package com.sharman.yukon.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 11/11/2015.
 */
public class YukonAccount extends JSONObject {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum YukonAccountJSONKeys{
        DISPLAY_NAME("displayName"),
        EMAIL("email"),
        USER_ID("userId");
        // TODO photo
        // TODO cover

        private String key;
        private YukonAccountJSONKeys(String key){
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
    public YukonAccount(String displayName, String email, String userId){
        super();
        try {
            this.setDisplayName(displayName);
            this.setEmail(email);
            this.setUserId(userId);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public YukonAccount(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *DisplayName:
    public String getDisplayName(){
        return super.optString(YukonAccountJSONKeys.DISPLAY_NAME.getKey());
    }
    public void setDisplayName(String displayName) throws JSONException{
        super.putOpt(YukonAccountJSONKeys.DISPLAY_NAME.getKey(), displayName);
    }

    // *Email:
    public String getEmail(){
        return super.optString(YukonAccountJSONKeys.EMAIL.getKey());
    }
    public void setEmail(String email) throws JSONException{
        super.putOpt(YukonAccountJSONKeys.EMAIL.getKey(), email);
    }

    // *UserId:
    public String getUserId(){
        return super.optString(YukonAccountJSONKeys.USER_ID.getKey());
    }
    public void setUserId(String userId) throws JSONException{
        super.putOpt(YukonAccountJSONKeys.USER_ID.getKey(), userId);
    }
}
