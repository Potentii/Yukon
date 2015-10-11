package com.sharman.yukon.io.drive.callback;

/**
 * Created by poten on 10/10/2015.
 */
public interface FolderCreateCallback {
    public void onSuccess(String folderId);
    public void onFailure(String errorMessage);
}
