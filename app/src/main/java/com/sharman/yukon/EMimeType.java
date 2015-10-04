package com.sharman.yukon;

/**
 * Created by poten on 03/10/2015.
 */
public enum EMimeType {
    JSON("application/json");


    private String mimeType;

    private EMimeType(String mimeType){
        this.mimeType = mimeType;
    }

    public String getMimeType(){
        return this.mimeType;
    }
}
