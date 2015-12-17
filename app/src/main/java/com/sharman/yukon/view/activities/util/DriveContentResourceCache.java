package com.sharman.yukon.view.activities.util;

import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.callback.LastModifiedDateCallback;

import javax.annotation.Nonnull;

/**
 * Created by poten on 17/12/2015.
 */
public abstract class DriveContentResourceCache extends ResourceCache {
    private GoogleAccountCredential credential;

    public DriveContentResourceCache(@Nonnull Activity activity, @Nonnull  GoogleAccountCredential credential) {
        super(activity);
        this.credential = credential;
    }

    public void getResource(@Nonnull final String fileId){
        getResource_DriveFileContent(fileId, new GetResourceCacheCallback<String, Long>() {
            @Override
            public void onFound(String cachedResource) {

            }

            @Override
            public void onNotFound(final RegisterResourceCacheCallback<String, Long> registerResourceCacheCallback) {
                new DriveIOHandler(credential).readFile(fileId, new FileReadCallback() {
                    @Override
                    public void onSuccess(String content, Long lastModifiedDate) {
                        registerResourceCacheCallback.register(content, lastModifiedDate);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        DriveContentResourceCache.this.onFailure(e);
                    }
                });
            }

            @Override
            public void onValidationRequested(final ValidateResourceCacheCallback<Long> validateResourceCacheCallback) {
                new DriveIOHandler(credential).getLastModifiedDate(fileId, new LastModifiedDateCallback() {
                    @Override
                    public void onSuccess(Long lastModifiedDate) {
                        validateResourceCacheCallback.validate(lastModifiedDate);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        DriveContentResourceCache.this.onFailure(e);
                    }
                });
            }

            @Override
            public void onValidatedCache(String validatedResource) {
                onSuccess(validatedResource);
            }
        });
    }



    public abstract void onSuccess(String validatedResource);
    public abstract void onFailure(Exception e);

}
