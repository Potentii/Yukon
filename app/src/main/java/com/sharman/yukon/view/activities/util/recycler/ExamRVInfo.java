package com.sharman.yukon.view.activities.util.recycler;

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
    private String configs;


    public ExamRVInfo(String teacherId, String examTitle, String examSubject, Date examDeliveryDate, String configs) {
        this.teacherId = teacherId;
        this.examTitle = examTitle;
        this.examSubject = examSubject;
        this.examDeliveryDate = examDeliveryDate;
        this.configs = configs;
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
    public String getConfigs() {
        return configs;
    }
    public void setConfigs(String configs) {
        this.configs = configs;
    }
}
