package com.sharman.yukon.view.activities.util.recycler;

import com.sharman.yukon.model.Answer;
import com.sharman.yukon.model.WeightTypeAnswerStruct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by poten on 28/12/2015.
 */
public class QuestionCorrectionRVInfo {
    private int questionIndex;
    @Nonnull
    private Answer studentAnswer;
    @Nonnull
    private WeightTypeAnswerStruct teacherAnswer;

    @Nullable
    private Boolean correct;



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public QuestionCorrectionRVInfo(int questionIndex, @Nonnull Answer studentAnswer, @Nonnull WeightTypeAnswerStruct teacherAnswer) {
        this.questionIndex = questionIndex;
        this.studentAnswer = studentAnswer;
        this.teacherAnswer = teacherAnswer;
        this.correct = null;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public int getQuestionIndex() {
        return questionIndex;
    }
    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    @Nonnull
    public Answer getStudentAnswer() {
        return studentAnswer;
    }
    public void setStudentAnswer(@Nonnull Answer studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    @Nonnull
    public WeightTypeAnswerStruct getTeacherAnswer() {
        return teacherAnswer;
    }
    public void setTeacherAnswer(@Nonnull WeightTypeAnswerStruct teacherAnswer) {
        this.teacherAnswer = teacherAnswer;
    }

    @Nullable
    public Boolean getCorrect() {
        return correct;
    }
    public void setCorrect(@Nullable Boolean correct) {
        this.correct = correct;
    }
}
