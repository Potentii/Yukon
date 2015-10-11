package com.sharman.yukon.view.activities;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;

/**
 * Created by poten on 07/10/2015.
 */
public class StudentConfigFilePair {
    private String userId;
    private String configFileId;

    public StudentConfigFilePair(String userId, String configFileId) {
        this.userId = userId;
        this.configFileId = configFileId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getConfigFileId() {
        return configFileId;
    }
    public void setConfigFileId(String configFileId) {
        this.configFileId = configFileId;
    }
}
