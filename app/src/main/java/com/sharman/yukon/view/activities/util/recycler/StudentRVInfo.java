package com.sharman.yukon.view.activities.util.recycler;


import com.sharman.yukon.model.Grade;

/**
 * Created by poten on 24/10/2015.
 */
public class StudentRVInfo{
    private String studentGradeFileId;
    private String studentAnswerFileId;

    private String studentGradeStr;

    private String studentEmail;
    private String studentName;
    private String studentImageUri;



    public StudentRVInfo(String studentGradeFileId, String studentAnswerFileId, String studentEmail) {
        this.studentGradeFileId = studentGradeFileId;
        this.studentAnswerFileId = studentAnswerFileId;
        this.studentEmail = studentEmail;

        // *If it's not set yet:
        this.studentGradeStr = null;
        this.studentName = null;
        this.studentImageUri = null;
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

    public String getStudentGradeStr() {
        return studentGradeStr;
    }
    public void setStudentGradeStr(String studentGradeStr) {
        this.studentGradeStr = studentGradeStr;
    }

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentImageUri() {
        return studentImageUri;
    }
    public void setStudentImageUri(String studentImageUri) {
        this.studentImageUri = studentImageUri;
    }
}