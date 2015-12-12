package com.sharman.yukon.view.activities.util;

import java.util.Set;

/**
 * Created by poten on 16/11/2015.
 */
public interface FinishStepByStepEventCallback {
    public void onSuccess();
    public void onFailure(Set<String> failedSteps);
}
