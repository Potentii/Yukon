package com.sharman.yukon.io.drive.callback;

import com.google.api.services.drive.model.File;

import java.util.List;

/**
 * Created by poten on 11/10/2015.
 */
public interface FileQueryCallback {
    public void onResult(List<File> driveFileList);
    public void onFailure(Exception e);
}
