package com.sharman.yukon.view.activities.util.recycler;

import com.sharman.yukon.model.Question;
import com.sharman.yukon.model.WeightTypeAnswerStruct;
import com.sharman.yukon.model.util.EMultipleAnswerType;

/**
 * Created by poten on 07/12/2015.
 */
public class QuestionsCreatingRVInfo {
    private int questionIndex;

    private Question question;
    private WeightTypeAnswerStruct weightTypeAnswerStruct;



    public QuestionsCreatingRVInfo(int questionIndex, Question question, WeightTypeAnswerStruct weightTypeAnswerStruct) {
        this.questionIndex = questionIndex;
        this.question = question;
        this.weightTypeAnswerStruct = weightTypeAnswerStruct;
    }



    public int getQuestionIndex() {
        return questionIndex;
    }
    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }

    public WeightTypeAnswerStruct getWeightTypeAnswerStruct() {
        return weightTypeAnswerStruct;
    }
    public void setWeightTypeAnswerStruct(WeightTypeAnswerStruct weightTypeAnswerStruct) {
        this.weightTypeAnswerStruct = weightTypeAnswerStruct;
    }
}
