package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 17/10/2015.
 */
public class AnswerAlternativePair {
    private boolean correct;
    private String alternative;

    public AnswerAlternativePair(boolean correct, String alternative) {
        this.correct = correct;
        this.alternative = alternative;
    }

    public boolean isCorrect() {
        return correct;
    }
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
    public String getAlternative() {
        return alternative;
    }
    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }
}
