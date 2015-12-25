package com.sharman.yukon.io.drive.util;

/**
 * Created by poten on 21/12/2015.
 */
public enum ERole {
    READER("reader"),
    WRITER("writer"),
    OWNER("owner"),
    COMMENTER("commenter");


    private String value;

    private ERole(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public static ERole getERole(String str){
        for (ERole eRole : ERole.values()) {
            if(eRole.getValue().equals(str)){
                return eRole;
            }
        }
        return null;
    }
}
