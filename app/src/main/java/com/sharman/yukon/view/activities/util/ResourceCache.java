package com.sharman.yukon.view.activities.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Nonnull;

/**
 * Created by poten on 15/11/2015.
 */
public class ResourceCache {
    private static final String GPLUS_PROFILE_PHOTO_IDENTIFIER = "GPPHOTO";
    private static final String GPLUS_PROFILE_COVER_IDENTIFIER = "GPCOVER";
    private static final String DRIVE_FILE_CONTENT_IDENTIFIER = "DRIVECONTENT";


    private static final String LAST_BITMAP_URL_SHARED_FILE = "lastBitmapURL";
    private static final String LAST_MODIFIED_DATE_SHARED_FILE = "lastModifiedDate";

    private Activity activity;


    
    public ResourceCache(Activity activity){
        this.activity = activity;
    }


    public String getCacheFolder(){
        String state = Environment.getExternalStorageState();
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(state)
                        ? activity.getExternalCacheDir().getPath()
                        : activity.getCacheDir().getPath();

        return cachePath + File.separator;
    }



    public void getResource_GPlusProfilePhoto(@Nonnull final String userId, @Nonnull final GetResourceCacheCallback<Bitmap, String> getResourceCacheCallback){
        getResource_bitmap(userId + GPLUS_PROFILE_PHOTO_IDENTIFIER, getResourceCacheCallback);
    }
    public void getResource_GPlusProfileCover(@Nonnull final String userId, @Nonnull final GetResourceCacheCallback<Bitmap, String> getResourceCacheCallback){
        getResource_bitmap(userId + GPLUS_PROFILE_COVER_IDENTIFIER, getResourceCacheCallback);
    }


    public void getResource_bitmap(final String resourceName, final GetResourceCacheCallback<Bitmap, String> getResourceCacheCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resourcePath = getCacheFolder() + resourceName;
                FileInputStream fileInputStream = null;

                try {
                    File resourceFile = new File(resourcePath);
                    fileInputStream = new FileInputStream(resourceFile);
                    final Bitmap cachedBitmap = BitmapFactory.decodeStream(fileInputStream);


                    getResourceCacheCallback.onFound(cachedBitmap);


                    // *Start validation process:
                    getResourceCacheCallback.onValidationRequested(new ValidateResourceCacheCallback<String>() {
                        @Override
                        public void validate(final String currentBitmapURL) {
                            String lastRecordedURL = new AndroidUtil(activity).readFromSharedPreferences(LAST_BITMAP_URL_SHARED_FILE, resourceName, null);

                            if(lastRecordedURL != null && currentBitmapURL != null && currentBitmapURL.equals(lastRecordedURL)){
                                getResourceCacheCallback.onValidatedCache(cachedBitmap);
                            } else{
                                // *Cached version has to be replaced:
                                System.err.println(">> ALERT: Resource cache is outdated [" + resourcePath + "].");
                                getResourceCacheCallback.onNotFound(new RegisterResourceCacheCallback<Bitmap, String>() {
                                    @Override
                                    public void register(final Bitmap updatedResource, final String dataToCompare) {
                                        write_bitmap(updatedResource, resourcePath);
                                        new AndroidUtil(activity).writeToSharedPreferences(LAST_BITMAP_URL_SHARED_FILE, resourceName, dataToCompare);
                                        getResourceCacheCallback.onValidatedCache(updatedResource);
                                    }
                                });
                            }
                        }
                    });


                } catch (FileNotFoundException | NullPointerException e) {
                    System.err.println(">> ALERT: Resource not found, or couldn't be read [" + resourcePath + "].");
                    getResourceCacheCallback.onNotFound(new RegisterResourceCacheCallback<Bitmap, String>() {
                        @Override
                        public void register(final Bitmap updatedResource, final String dataToCompare) {
                            write_bitmap(updatedResource, resourcePath);
                            new AndroidUtil(activity).writeToSharedPreferences(LAST_BITMAP_URL_SHARED_FILE, resourceName, dataToCompare);
                            getResourceCacheCallback.onValidatedCache(updatedResource);
                        }
                    });
                } finally {
                    if(fileInputStream != null){
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }






    public void getResource_DriveFileContent(@Nonnull final String fileId, @Nonnull final GetResourceCacheCallback<String, Long> getResourceCacheCallback){
        getResource_text(fileId + DRIVE_FILE_CONTENT_IDENTIFIER, getResourceCacheCallback);
    }


    public void getResource_text(final String resourceName, final GetResourceCacheCallback<String, Long> getResourceCacheCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resourcePath = getCacheFolder() + resourceName;
                BufferedReader bufferRead = null;

                try {
                    File resourceFile = new File(resourcePath);
                    bufferRead = new BufferedReader(new FileReader(resourceFile.getAbsoluteFile()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while((line = bufferRead.readLine()) != null){
                        sb.append(line);
                    }

                    final String cachedText = sb.toString();

                    getResourceCacheCallback.onFound(cachedText);


                    // *Start validation process:
                    getResourceCacheCallback.onValidationRequested(new ValidateResourceCacheCallback<Long>() {
                        @Override
                        public void validate(Long lastModifiedDate) {
                            Long cachedVersionDate;
                            boolean cacheUpdated;

                            try {
                                cachedVersionDate = Long.parseLong(new AndroidUtil(activity).readFromSharedPreferences(LAST_MODIFIED_DATE_SHARED_FILE, resourceName, null));
                            } catch (NumberFormatException e){
                                cachedVersionDate = null;
                            }


                            try{
                                cacheUpdated = new Date(lastModifiedDate).compareTo(new Date(cachedVersionDate)) <= 0;
                            } catch (NullPointerException e){
                                cacheUpdated = false;
                            }


                            // *Check if the cached version is up to date:
                            if(cachedVersionDate != null && lastModifiedDate != null && cacheUpdated){
                                getResourceCacheCallback.onValidatedCache(cachedText);
                            } else{
                                // *Cached version has to be replaced:
                                System.err.println(">> ALERT: Resource cache is outdated [" + resourcePath + "].");
                                getResourceCacheCallback.onNotFound(new RegisterResourceCacheCallback<String, Long>() {
                                    @Override
                                    public void register(final String updatedResource, final Long dataToCompare) {
                                        write_text(updatedResource, resourcePath);
                                        new AndroidUtil(activity).writeToSharedPreferences(LAST_MODIFIED_DATE_SHARED_FILE, resourceName, Long.toString(dataToCompare));
                                        getResourceCacheCallback.onValidatedCache(updatedResource);
                                    }
                                });
                            }
                        }
                    });


                } catch (IOException | NullPointerException e) {
                    System.err.println(">> ALERT: Resource not found, or couldn't be read [" + resourcePath + "].");
                    getResourceCacheCallback.onNotFound(new RegisterResourceCacheCallback<String, Long>() {
                        @Override
                        public void register(final String updatedResource, final Long dataToCompare) {
                            write_text(updatedResource, resourcePath);
                            new AndroidUtil(activity).writeToSharedPreferences(LAST_MODIFIED_DATE_SHARED_FILE, resourceName, Long.toString(dataToCompare));
                            getResourceCacheCallback.onValidatedCache(updatedResource);
                        }
                    });

                } finally {
                    if(bufferRead != null){
                        try {
                            bufferRead.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }











    private void write_text(final String content, final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bufferWrite = null;

                try{
                    bufferWrite = new BufferedWriter(new FileWriter(path));
                    bufferWrite.write(content);
                } catch(Exception e) {
                    e.printStackTrace();
                } finally{
                    if (bufferWrite != null) {
                        try {
                            bufferWrite.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }


    private void write_bitmap(final Bitmap content, final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fileOutputStream = null;

                try{
                    fileOutputStream = new FileOutputStream(path);
                    content.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                } catch(Exception e) {
                    e.printStackTrace();
                } finally{
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

}
