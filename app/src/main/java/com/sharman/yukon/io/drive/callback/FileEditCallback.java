package com.sharman.yukon.io.drive.callback;

/**
 * Created by poten on 10/10/2015.
 */
public interface FileEditCallback {
    public void onSuccess();
    public void onFailure(String errorMessage);
}
