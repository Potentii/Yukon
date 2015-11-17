package com.sharman.yukon.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poten on 11/11/2015.
 */
public class YukonAccountKeeper extends JSONObject {
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Keys enum:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected enum YukonAccountKeeperJSONKeys{
        MAIN_ACCOUNT("mainAccount"),
        SECONDARY_ACCOUNT_ARRAY("secondaryAccountArray");

        private String key;
        private YukonAccountKeeperJSONKeys(String key){
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
    public YukonAccountKeeper(YukonAccount mainAccount, YukonAccount[] secondaryAccountArray){
        super();
        try {
            this.setMainAccount(mainAccount);
            this.setSecondaryAccountArray(secondaryAccountArray);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public YukonAccountKeeper(String jsonStr) throws JSONException{
        super(jsonStr);
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *MainAccount:
    public YukonAccount getMainAccount(){
        try {
            return new YukonAccount(super.getJSONObject(YukonAccountKeeperJSONKeys.MAIN_ACCOUNT.getKey()).toString());
        } catch (JSONException e){
            return null;
        }
    }
    public void setMainAccount(YukonAccount mainAccount) throws JSONException{
        super.put(YukonAccountKeeperJSONKeys.MAIN_ACCOUNT.getKey(), mainAccount);
    }

    // *SecondaryAccountArray:
    public YukonAccount[] getSecondaryAccountArray(){
        JSONArray secondaryAccountJSONArray = super.optJSONArray(YukonAccountKeeperJSONKeys.SECONDARY_ACCOUNT_ARRAY.getKey());
        YukonAccount[] secondaryAccountArray = new YukonAccount[secondaryAccountJSONArray.length()];
        for(int i=0; i<secondaryAccountJSONArray.length(); i++){
            try {
                secondaryAccountArray[i] = new YukonAccount(secondaryAccountJSONArray.getJSONObject(i).toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return secondaryAccountArray;
    }
    public void setSecondaryAccountArray(YukonAccount[] secondaryAccountArray) throws JSONException{
        super.put(YukonAccountKeeperJSONKeys.SECONDARY_ACCOUNT_ARRAY.getKey(), new JSONArray(secondaryAccountArray));
    }
}
