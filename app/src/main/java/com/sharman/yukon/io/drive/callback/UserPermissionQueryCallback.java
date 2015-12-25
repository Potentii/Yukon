package com.sharman.yukon.io.drive.callback;

import com.google.api.services.drive.model.Permission;

/**
 * Created by poten on 21/12/2015.
 */
public interface UserPermissionQueryCallback {
    public void onSuccess(Permission[] permissionArray);
    public void onFailure(Exception e);
}
