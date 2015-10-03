package com.sharman.yukon;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by poten on 03/10/2015.
 */
public class DriveFileOffline {
    private final GoogleApiClient googleApiClient;

    public DriveFileOffline(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void upload(final DriveFolder folder, final String title, final String content, final String mimeType, final UploadCallback uploadCallback){

        Drive.DriveApi.newDriveContents(this.googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>(){
            @Override
            public void onResult(DriveApi.DriveContentsResult driveContentsResult){
                if(!driveContentsResult.getStatus().isSuccess()){
                    // TODO drive request error
                    uploadCallback.onComplete(null, false);
                    return;
                }

                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                try{
                    writer.write(content);
                    writer.close();
                } catch(IOException e){
                    //TODO erro ao escrever no arquivo
                    uploadCallback.onComplete(null, false);
                    e.printStackTrace();
                }

                MetadataChangeSet metadataChangeSet = new MetadataChangeSet
                        .Builder()
                        .setTitle(title)
                        .setMimeType(mimeType)
                        .build();

                folder.createFile(googleApiClient, metadataChangeSet, driveContents).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFileResult result) {
                        if (!result.getStatus().isSuccess()) {
                            // TODO drive file creation error
                            uploadCallback.onComplete(null, false);
                            return;
                        }
                        DriveId driveId = result.getDriveFile().getDriveId();
                        uploadCallback.onComplete(driveId, true);

                    }
                });

                try{
                    driveContents.commit(googleApiClient, metadataChangeSet);
                } catch(IllegalStateException e){}
            }
        });
    }




}
