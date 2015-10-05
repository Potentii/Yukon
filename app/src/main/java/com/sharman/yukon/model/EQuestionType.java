package com.sharman.yukon.model;

/**
 * Created by poten on 04/10/2015.
 */
public enum EQuestionType {
    MULTIPLE_CHOICE("multipleChoice"),
    DISSERTATIVE("dissertative"),
    TRUE_OR_FALSE("trueOrFalse");


    private String name;
    private EQuestionType(String name){
        this.name = name;
    }
    public static EQuestionType getEQuestionType(String str){
        for (EQuestionType eQuestionType : EQuestionType.values()) {
            if(eQuestionType.getName().equals(str)){
                return eQuestionType;
            }
        }
        return null;
    }
    public String getName(){
        return this.name;
    }
}
