package com.sharman.yukon;

/**
 * Created by poten on 02/10/2015.
 */
public interface DownloadCallback {
    public void onResult(String fileName, String fileContent, boolean success);
}
