package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.sharman.yukon.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileCreateCallback;
import com.sharman.yukon.io.drive.callback.FileShareCallback;
import com.sharman.yukon.io.drive.callback.FolderCreateCallback;
import com.sharman.yukon.io.drive.util.PermissionStruct;

import java.io.IOException;
import java.util.Arrays;

public class TestActivity extends GoogleRestConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(getCredential().getSelectedAccountName() != null) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // *Verifies if the user has logged in:
        if(getCredential().getSelectedAccountName() != null) {
            /*
            new DriveIOHandler(getCredential()).createFolder("", "folder loka", "desc", new FolderCreateCallback() {
                @Override
                public void onSuccess(String folderId) {
                    System.out.println("SUCCESS FOLDER: " + folderId);
                    new DriveIOHandler(getCredential()).createFile(folderId, "File inside folder test", "descr", EMimeType.TEXT.getMimeType(), "conteudo", new FileCreateCallback() {
                        @Override
                        public void onSuccess(String fileId) {
                            System.out.println("SUCCESS FILE: " + fileId);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            System.out.println("ERROR FILE: " + errorMessage);
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    System.out.println("ERROR FOLDER: " + errorMessage);
                }
            });
            */

            /*
            new DriveIOHandler(getCredential()).createFile("", "Share test", "desc", EMimeType.TEXT.getMimeType(), "conteudo", new FileCreateCallback() {
                @Override
                public void onSuccess(String fileId) {
                    System.out.println("SUCCESS: " + fileId);

                    new DriveIOHandler(getCredential()).shareFile(fileId, new PermissionStruct("luciana.reginaldocps@gmail.com", "user", "reader"), new FileShareCallback() {
                        @Override
                        public void onSuccess() {
                            System.out.println("SUCCESS: File shared");
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            System.out.println("sERROR: " + errorMessage);
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    System.out.println("fERROR: " + errorMessage);
                }
            });
            */
        }
    }


    /*
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.drive.Drive service = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            service = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    //.setApplicationName("Yukon")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                List<String> fileInfo = new ArrayList<String>();
                FileList result = service.files().list()
                        .setMaxResults(10)
                        .execute();
                List<File> files = result.getItems();
                if (files != null) {
                    for (File file : files) {
                        fileInfo.add(file.getTitle());
                    }
                }
                return fileInfo;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        // After execute
        @Override
        protected void onPostExecute(List<String> output) {
            for (String fileName : output) {
                System.out.println(fileName);
            }
        }

    }*/
}
