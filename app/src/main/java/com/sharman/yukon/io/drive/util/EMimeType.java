package com.sharman.yukon.io.drive.util;

/**
 * Created by poten on 03/10/2015.
 */
public enum EMimeType {
    JSON("application/json"),
    TEXT("text/plain"),
    FOLDER("application/vnd.google-apps.folder"),
    STUDENT_CONFIG("application/vnd.yukon.config.student"),
    TEACHER_CONFIG("application/vnd.yukon.config.teacher");


    private String mimeType;

    private EMimeType(String mimeType){
        this.mimeType = mimeType;
    }

    public String getMimeType(){
        return this.mimeType;
    }
}
