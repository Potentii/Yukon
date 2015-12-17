package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 15/11/2015.
 */
public interface RegisterResourceCacheCallback<T, U> {
    public void register(final T updatedResource, final U dataToCompare);
}
