package com.sharman.yukon.io.drive;

import com.google.android.gms.drive.DriveFolder;

/**
 * Created by poten on 06/10/2015.
 */
public interface CreateFolderCallback {
    public void onComplete(DriveFolder driveFolder);
    public void onFail(String errorMessage);
}
