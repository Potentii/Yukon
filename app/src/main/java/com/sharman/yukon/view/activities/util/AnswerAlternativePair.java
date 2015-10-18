package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 17/10/2015.
 */
public class AnswerAlternativePair {
    private boolean correct;
    private String question;

    public AnswerAlternativePair(boolean correct, String question) {
        this.correct = correct;
        this.question = question;
    }

    public boolean isCorrect() {
        return correct;
    }
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
}
