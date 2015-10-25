package com.sharman.yukon.view.activities.util.recycler;


import com.sharman.yukon.model.Grade;

/**
 * Created by poten on 24/10/2015.
 */
public class StudentRVInfo{
    private String studentGradeFileId;
    private String studentAnswerFileId;
    private String studentEmail;

    private String studentGrade;
    private String studentName;

    public StudentRVInfo(String studentGradeFileId, String studentAnswerFileId, String studentEmail) {
        this.studentGradeFileId = studentGradeFileId;
        this.studentAnswerFileId = studentAnswerFileId;
        this.studentEmail = studentEmail;
        this.studentGrade = new Grade(-1).toString();
        this.studentName = "";
    }

    public String getStudentGradeFileId() {
        return studentGradeFileId;
    }

    public void setStudentGradeFileId(String studentGradeFileId) {
        this.studentGradeFileId = studentGradeFileId;
    }

    public String getStudentAnswerFileId() {
        return studentAnswerFileId;
    }

    public void setStudentAnswerFileId(String studentAnswerFileId) {
        this.studentAnswerFileId = studentAnswerFileId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentGrade() {
        return studentGrade;
    }

    public void setStudentGrade(String studentGrade) {
        this.studentGrade = studentGrade;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}