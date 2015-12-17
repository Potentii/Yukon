package com.sharman.yukon.io.drive.callback;

/**
 * Created by poten on 10/10/2015.
 */
public interface FileReadCallback {
    public void onSuccess(String content, Long lastModifiedDate);
    public void onFailure(Exception exception);
}
