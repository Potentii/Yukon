package com.sharman.yukon.io.drive;

import com.google.android.gms.drive.DriveId;

/**
 * Created by poten on 03/10/2015.
 */
@Deprecated
public interface UploadCallback {
    public void onComplete(DriveId driveId);
    public void onFail(String errorMessage);
}
