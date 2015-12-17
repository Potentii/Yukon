package com.sharman.yukon.io.plus.callback;

import android.graphics.Bitmap;

/**
 * Created by poten on 12/10/2015.
 */
public interface PersonImgReadCallback {
    public void onSuccess(Bitmap bitmap, String bitmapURL);
    public void onFailure(String errorMessage);
}
