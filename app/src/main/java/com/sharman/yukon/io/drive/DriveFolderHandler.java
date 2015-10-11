package com.sharman.yukon.io.drive;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

/**
 * Created by poten on 06/10/2015.
 */
@Deprecated
public class DriveFolderHandler {
    private GoogleApiClient googleApiClient;

    public DriveFolderHandler(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void create(DriveFolder parentFolder, MetadataChangeSet metadataChangeSet, final CreateFolderCallback createFolderCallback){
        parentFolder.createFolder(googleApiClient, metadataChangeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                if(!driveFolderResult.getStatus().isSuccess()){
                    createFolderCallback.onFail("Erro ao tentar criar uma nova pasta");
                    return;
                }
                createFolderCallback.onComplete(driveFolderResult.getDriveFolder());
            }
        });
    }

}
