package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 15/11/2015.
 */
public interface GetResourceCacheCallback<T, U> {
    public void onFound(T cachedResource);
    public void onNotFound(final RegisterResourceCacheCallback<T, U> registerResourceCacheCallback);
    public void onValidationRequested(final ValidateResourceCacheCallback<U> validateResourceCacheCallback);
    public void onValidatedCache(T validatedResource);
}
