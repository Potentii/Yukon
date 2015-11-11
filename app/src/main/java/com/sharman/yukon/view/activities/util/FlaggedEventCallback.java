package com.sharman.yukon.view.activities.util;

import java.util.EnumSet;

/**
 * Created by poten on 10/11/2015.
 */
public interface FlaggedEventCallback<T extends java.lang.Enum<T>> {
    public void onSuccess();
    public void onFailure(EnumSet<T> failedEvents);
}
