package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 15/11/2015.
 */
public interface GetResourceCacheCallback<T> {
    public void onFound(T resource);
    public void onNotFound(final RegisterResourceCacheCallback<T> registerResourceCacheCallback);
}
