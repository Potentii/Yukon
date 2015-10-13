package com.sharman.yukon.io.drive.callback;

/**
 * Created by poten on 10/10/2015.
 */
public interface FileReadCallback {
    public void onSuccess(String content);
    public void onFailure(String errorMessage);
}
