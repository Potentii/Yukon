package com.sharman.yukon.io.drive.callback;

/**
 * Created by poten on 15/12/2015.
 */
public interface LastModifiedDateCallback {
    public void onSuccess(Long lastModifiedDate);
    public void onFailure(Exception e);
}
