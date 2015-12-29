package com.sharman.yukon.view.activities.util.recycler;


import com.sharman.yukon.model.Grade;
import com.sharman.yukon.view.activities.util.StudentContact;

import javax.annotation.Nonnull;

/**
 * Created by poten on 24/10/2015.
 */
public class StudentRVInfo{
    @Nonnull
    private String studentGradeFileId;
    @Nonnull
    private String studentAnswerFileId;

    @Nonnull
    private Grade grade;
    @Nonnull
    private StudentContact studentContact;



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public StudentRVInfo(@Nonnull String studentGradeFileId, @Nonnull String studentAnswerFileId, @Nonnull Grade grade, @Nonnull StudentContact studentContact) {
        this.studentGradeFileId = studentGradeFileId;
        this.studentAnswerFileId = studentAnswerFileId;
        this.grade = grade;
        this.studentContact = studentContact;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Nonnull
    public String getStudentGradeFileId() {
        return studentGradeFileId;
    }
    public void setStudentGradeFileId(@Nonnull String studentGradeFileId) {
        this.studentGradeFileId = studentGradeFileId;
    }

    @Nonnull
    public String getStudentAnswerFileId() {
        return studentAnswerFileId;
    }
    public void setStudentAnswerFileId(@Nonnull String studentAnswerFileId) {
        this.studentAnswerFileId = studentAnswerFileId;
    }

    @Nonnull
    public Grade getGrade() {
        return grade;
    }
    public void setGrade(@Nonnull Grade grade) {
        this.grade = grade;
    }

    @Nonnull
    public StudentContact getStudentContact() {
        return studentContact;
    }
    public void setStudentContact(@Nonnull StudentContact studentContact) {
        this.studentContact = studentContact;
    }
}