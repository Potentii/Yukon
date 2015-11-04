package com.sharman.yukon.io.drive.callback;

/**
 * Created by poten on 31/10/2015.
 */
public interface MultipleFilesReadCallback {
    public void onSuccess(String[] contentArray);
    public void onFailure(String errorMessage);
}
