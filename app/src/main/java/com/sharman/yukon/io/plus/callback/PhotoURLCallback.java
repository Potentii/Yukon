package com.sharman.yukon.io.plus.callback;

/**
 * Created by poten on 16/12/2015.
 */
public interface PhotoURLCallback {
    public void onSuccess(String photoURL);
    public void onFailure(Exception e);
}
