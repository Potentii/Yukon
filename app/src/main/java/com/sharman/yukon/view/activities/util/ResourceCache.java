package com.sharman.yukon.view.activities.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Created by poten on 15/11/2015.
 */
public class ResourceCache {
    private static final String GPLUS_PROFILE_PHOTO_IDENTIFIER = "GPPHOTO";
    private static final String GPLUS_PROFILE_COVER_IDENTIFIER = "GPCOVER";
    private Context context;

    public ResourceCache(Context context){
        this.context = context;
    }

    public String getCacheFolder(){
        String state = Environment.getExternalStorageState();
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(state)
                        ? context.getExternalCacheDir().getPath()
                        : context.getCacheDir().getPath();

        return cachePath + File.separator;
    }


    public void getResource_GPlusProfilePhoto(@Nonnull final String userId, @Nonnull final GetResourceCacheCallback<Bitmap> getResourceCacheCallback){
        getResource_Bitmap(userId + GPLUS_PROFILE_PHOTO_IDENTIFIER, getResourceCacheCallback);
    }
    public void getResource_GPlusProfileCover(@Nonnull final String userId, @Nonnull final GetResourceCacheCallback<Bitmap> getResourceCacheCallback){
        getResource_Bitmap(userId + GPLUS_PROFILE_COVER_IDENTIFIER, getResourceCacheCallback);
    }


    public void getResource_Bitmap(final String resourceName, final GetResourceCacheCallback<Bitmap> getResourceCacheCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resourcePath = getCacheFolder() + resourceName;

                try {
                    File resourceFile = new File(resourcePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(resourceFile));
                    getResourceCacheCallback.onFound(bitmap);

                } catch (FileNotFoundException | NullPointerException e) {
                    System.err.println(">> ERROR: Resource not found, or couldn't be read [" + resourcePath + "].");
                    getResourceCacheCallback.onNotFound(new RegisterResourceCacheCallback<Bitmap>() {
                        @Override
                        public void register(final Bitmap resource) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    FileOutputStream fileOutputStream = null;

                                    try{
                                        fileOutputStream = new FileOutputStream(resourcePath);
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    } finally{
                                        try {
                                            if (fileOutputStream != null) {
                                                fileOutputStream.close();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();

                        }
                    });

                }
            }
        }).start();
    }
}
