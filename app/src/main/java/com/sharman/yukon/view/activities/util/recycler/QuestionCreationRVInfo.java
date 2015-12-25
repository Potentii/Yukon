package com.sharman.yukon.view.activities.util.recycler;

import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.WeightTypeAnswerStruct;

import javax.annotation.Nonnull;

/**
 * Created by poten on 07/12/2015.
 */

public class QuestionCreationRVInfo {
    private int index;
    @Nonnull
    private Question question;
    @Nonnull
    private WeightTypeAnswerStruct weightTypeAnswerStruct;



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public QuestionCreationRVInfo(int index, @Nonnull Question question, @Nonnull WeightTypeAnswerStruct weightTypeAnswerStruct) {
        this.index = index;
        this.question = question;
        this.weightTypeAnswerStruct = weightTypeAnswerStruct;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    @Nonnull
    public Question getQuestion() {
        return question;
    }
    public void setQuestion(@Nonnull Question question) {
        this.question = question;
    }

    @Nonnull
    public WeightTypeAnswerStruct getWeightTypeAnswerStruct() {
        return weightTypeAnswerStruct;
    }
    public void setWeightTypeAnswerStruct(@Nonnull WeightTypeAnswerStruct weightTypeAnswerStruct) {
        this.weightTypeAnswerStruct = weightTypeAnswerStruct;
    }
}
