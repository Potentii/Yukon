package com.sharman.yukon.view.activities;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;

/**
 * Created by poten on 07/10/2015.
 */
public class StudentConfigFilePair {
    private String userId;
    private DriveId configFile;

    public StudentConfigFilePair(String userId, DriveId configFile) {
        this.userId = userId;
        this.configFile = configFile;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public DriveId getConfigFile() {
        return configFile;
    }
    public void setConfigFile(DriveId configFile) {
        this.configFile = configFile;
    }
}
