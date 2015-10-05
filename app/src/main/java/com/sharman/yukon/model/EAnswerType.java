package com.sharman.yukon.model;

/**
 * Created by poten on 04/10/2015.
 */
public enum EAnswerType {
    MULTIPLE_CHOICE("multipleChoice"),
    DISSERTATIVE("dissertative"),
    TRUE_OR_FALSE("trueOrFalse");


    private String name;
    private EAnswerType(String name){
        this.name = name;
    }
    public static EAnswerType getEAnswerType(String str){
        for (EAnswerType eAnswerType : EAnswerType.values()) {
            if(eAnswerType.getName().equals(str)){
                return eAnswerType;
            }
        }
        return null;
    }
    public String getName(){
        return this.name;
    }
}
