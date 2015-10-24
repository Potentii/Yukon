package com.sharman.yukon.view.activities.util;

import com.sharman.yukon.model.Exam;

import java.util.Date;

/**
 * Created by poten on 11/10/2015.
 */
public class ExamRVInfo {
    private String teacherId;
    private String examTitle;
    private String examSubject;
    private Date examDeliveryDate;

    private String examFileId;
    private String studentAnswerFileId;
    private String gradeFileId;

    private String correctAnswersFileId;



    public ExamRVInfo(String teacherId, String examTitle, String examSubject, Date examDeliveryDate, String examFileId, String studentAnswerFileId, String gradeFileId) {
        this.teacherId = teacherId;
        this.examTitle = examTitle;
        this.examSubject = examSubject;
        this.examDeliveryDate = examDeliveryDate;
        this.examFileId = examFileId;
        this.studentAnswerFileId = studentAnswerFileId;
        this.gradeFileId = gradeFileId;
    }

    public ExamRVInfo(String teacherId, String examTitle, String examSubject, Date examDeliveryDate, String examFileId, String correctAnswersFileId) {
        this.teacherId = teacherId;
        this.examTitle = examTitle;
        this.examSubject = examSubject;
        this.examDeliveryDate = examDeliveryDate;
        this.examFileId = examFileId;
        this.correctAnswersFileId = correctAnswersFileId;
    }



    public String getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    public String getExamTitle() {
        return examTitle;
    }
    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }
    public String getExamSubject() {
        return examSubject;
    }
    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }
    public Date getExamDeliveryDate() {
        return examDeliveryDate;
    }
    public void setExamDeliveryDate(Date examDeliveryDate) {
        this.examDeliveryDate = examDeliveryDate;
    }
    public String getExamFileId() {
        return examFileId;
    }
    public void setExamFileId(String examFileId) {
        this.examFileId = examFileId;
    }
    public String getStudentAnswerFileId() {
        return studentAnswerFileId;
    }
    public void setStudentAnswerFileId(String studentAnswerFileId) {
        this.studentAnswerFileId = studentAnswerFileId;
    }
    public String getGradeFileId() {
        return gradeFileId;
    }
    public void setGradeFileId(String gradeFileId) {
        this.gradeFileId = gradeFileId;
    }
    public String getCorrectAnswersFileId() {
        return correctAnswersFileId;
    }
    public void setCorrectAnswersFileId(String correctAnswersFileId) {
        this.correctAnswersFileId = correctAnswersFileId;
    }
}
