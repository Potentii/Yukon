package com.sharman.yukon.model;

/**
 * Created by poten on 04/10/2015.
 */
public enum EMultipleAnswerType {
    MULTIPLE_CHOICE("multipleChoice"),
    TRUE_OR_FALSE("trueOrFalse");


    private String name;
    private EMultipleAnswerType(String name){
        this.name = name;
    }
    public static EMultipleAnswerType getEMultipleAnswerType(String str){
        for (EMultipleAnswerType eMultipleAnswerType : EMultipleAnswerType.values()) {
            if(eMultipleAnswerType.getName().equals(str)){
                return eMultipleAnswerType;
            }
        }
        return null;
    }
    public String getName(){
        return this.name;
    }
}
