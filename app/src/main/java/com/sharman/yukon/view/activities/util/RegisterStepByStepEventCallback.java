package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 20/11/2015.
 */
public interface RegisterStepByStepEventCallback {
    public void onRegisterSuccess(String succeededStep);
    public void onRegisterFailure(String failedStep);
}
