package com.sharman.yukon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.model.Permission;
import com.sharman.yukon.io.drive.util.PermissionStruct;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by poten on 02/10/2015.
 */
@Deprecated
public class DriveFileOnline {

    public void download(GoogleApiClient googleApiClient, DriveId driveId, final DownloadCallback downloadCallback){
    }



    public void share(final GoogleAccountCredential credential, final DriveId driveId, final PermissionStruct[] permissionStruct){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential).build();

                String fileId = driveId.encodeToString();

                JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
                    @Override
                    public void onSuccess(Permission permission, HttpHeaders responseHeaders) {
                        System.out.println("Permission added to file");
                    }

                    @Override
                    public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                        System.out.println("Error Message: " + e.getMessage());
                    }
                };

                BatchRequest batch = service.batch();

                for(int i=0; i<permissionStruct.length; i++) {
                    Permission permission = new Permission();
                    permission.setValue(permissionStruct[i].getValue());
                    permission.setType(permissionStruct[i].getType());
                    permission.setRole(permissionStruct[i].getRole());
                    try {
                        service.permissions().insert(fileId, permission).queue(batch, callback);
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }

                try {
                    batch.execute();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).run();
    }







}
