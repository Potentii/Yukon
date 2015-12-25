package com.sharman.yukon.view.activities.util.recycler;

import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.Question;

import javax.annotation.Nonnull;

/**
 * Created by poten on 17/12/2015.
 */
public class QuestionAnsweringRVInfo {
    private int index;
    @Nonnull
    private Question question;
    @Nonnull
    private Answer answer;



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public QuestionAnsweringRVInfo(int index, @Nonnull Question question, @Nonnull Answer answer) {
        this.index = index;
        this.question = question;
        this.answer = answer;
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
    public Answer getAnswer() {
        return answer;
    }
    public void setAnswer(@Nonnull Answer answer) {
        this.answer = answer;
    }
}
