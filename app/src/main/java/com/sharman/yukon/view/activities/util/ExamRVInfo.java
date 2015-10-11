package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 11/10/2015.
 */
public class ExamRVInfo {
    private int teacherImgId;
    private String examTitle;
    private String fileId;

    public ExamRVInfo(int teacherImgId, String examTitle, String fileId) {
        this.teacherImgId = teacherImgId;
        this.examTitle = examTitle;
        this.fileId = fileId;
    }

    public int getTeacherImgId() {
        return teacherImgId;
    }
    public void setTeacherImgId(int teacherImgId) {
        this.teacherImgId = teacherImgId;
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
