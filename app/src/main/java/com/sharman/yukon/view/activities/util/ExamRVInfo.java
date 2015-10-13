package com.sharman.yukon.view.activities.util;

import com.sharman.yukon.model.Exam;

/**
 * Created by poten on 11/10/2015.
 */
public class ExamRVInfo {
    private String teacherId;
    private String examTitle;
    private String fileId;
    private Exam exam;

    public ExamRVInfo(String teacherId, String examTitle, String fileId) {
        this.teacherId = teacherId;
        this.examTitle = examTitle;
        this.fileId = fileId;
    }

    public ExamRVInfo(Exam exam){
        this.exam = exam;
    }

    public Exam getExam() {
        return exam;
    }
    public void setExam(Exam exam) {
        this.exam = exam;
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
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
