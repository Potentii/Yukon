package com.sharman.yukon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

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

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by poten on 02/10/2015.
 */
public class DriveFileOnline {

    public void download(GoogleApiClient googleApiClient, DriveId driveId, final DownloadCallback downloadCallback){
    }



    public void share(GoogleAccountCredential credential, DriveId driveId){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        com.google.api.services.drive.Drive service = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential).build();


        String fileId = driveId.encodeToString();

        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onSuccess(Permission permission, HttpHeaders responseHeaders) {
                System.out.println("Success!*****");
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                System.out.println("Error Message: " + e.getMessage());
            }
        };

        BatchRequest batch = service.batch();
        try {
            service.permissions().insert(fileId, insertPermission(service, fileId, "tony@acmecorp.com", "user", "writer")).queue(batch, callback);
            batch.execute();
        } catch(IOException e){
            e.printStackTrace();
        }

    }





    private static Permission insertPermission(com.google.api.services.drive.Drive service, String fileId, String value, String type, String role) {
        Permission newPermission = new Permission();

        newPermission.setValue(value);
        newPermission.setType(type);
        newPermission.setRole(role);
        try {
            return service.permissions().insert(fileId, newPermission).execute();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
        return null;
    }

}
