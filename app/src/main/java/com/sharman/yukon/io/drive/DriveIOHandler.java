package com.sharman.yukon.io.drive;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.sharman.yukon.EMimeType;
import com.sharman.yukon.io.drive.callback.FileCreateCallback;
import com.sharman.yukon.io.drive.callback.FileDeleteCallback;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.callback.FileShareCallback;
import com.sharman.yukon.io.drive.callback.FolderCreateCallback;
import com.sharman.yukon.io.drive.callback.FolderDeleteCallback;
import com.sharman.yukon.io.drive.callback.FolderShareCallback;
import com.sharman.yukon.io.drive.util.PermissionStruct;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by poten on 10/10/2015.
 */
public final class DriveIOHandler {
    private GoogleAccountCredential credential;

    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public DriveIOHandler(GoogleAccountCredential credential) {
        this.credential = credential;
    }


    private com.google.api.services.drive.Drive getDriveService(){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, this.credential)
                .setApplicationName("Yukon")
                .build();
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Creates a file on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void createFile(final String folderId, final String title, final String description, final String mimeType, final String content, final FileCreateCallback fileCreateCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // *Get the Drive service instance:
                    com.google.api.services.drive.Drive service = getDriveService();

                    // *File's metadata:
                    File driveMetadata = new File();
                    driveMetadata.setTitle(title);
                    driveMetadata.setDescription(description);
                    driveMetadata.setMimeType(mimeType);

                    // *Set the parent folder:
                    if (folderId != null && !folderId.isEmpty()) {
                        driveMetadata.setParents(Arrays.asList(new ParentReference().setId(folderId)));
                    } else{
                        driveMetadata.setParents(Arrays.asList(new ParentReference().setId(service.about().get().execute().getRootFolderId())));
                    }


                    // *Write to the file:
                    ByteArrayContent fileContent = ByteArrayContent.fromString(mimeType, content);

                    // *Insert the file to Drive:
                    File driveFile = service.files().insert(driveMetadata, fileContent).execute();

                    // *Return the file id to the callback:
                    fileCreateCallback.onSuccess(driveFile.getId());
                } catch (IOException e){
                    e.printStackTrace();
                    fileCreateCallback.onFailure(e.getMessage());
                } catch (Exception e){
                    e.printStackTrace();
                    fileCreateCallback.onFailure(e.getMessage());
                }
            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Reads a file on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void readFile(final FileReadCallback fileReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Edits a file on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void editFile(final FileEditCallback fileEditCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Deletes a file on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void deleteFile(final FileDeleteCallback fileDeleteCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Shares a file on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void shareFile(final String fileId, final String emailMessage, final PermissionStruct[] permissionStructArray, final FileShareCallback fileShareCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // *Get the Drive service instance:
                com.google.api.services.drive.Drive service = getDriveService();

                // *Set the "permission added" callback:
                JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
                    @Override
                    public void onSuccess(Permission permission, HttpHeaders responseHeaders) {
                        fileShareCallback.onSuccess();
                    }

                    @Override
                    public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                        fileShareCallback.onFailure(e.getMessage());
                    }
                };

                BatchRequest batch = service.batch();

                // *Inserting permissions:
                for(int i=0; i<permissionStructArray.length; i++) {
                    Permission permission = new Permission();
                    permission.setValue(permissionStructArray[i].getValue());
                    permission.setType(permissionStructArray[i].getType());
                    permission.setRole(permissionStructArray[i].getRole());
                    try {
                        if (emailMessage != null) {
                            service.permissions().insert(fileId, permission)
                                    .setSendNotificationEmails(true)
                                    .setEmailMessage(emailMessage)
                                    .queue(batch, callback);
                        } else {
                            service.permissions().insert(fileId, permission)
                                    .setSendNotificationEmails(false)
                                    .queue(batch, callback);
                        }
                    } catch(IOException e){
                        e.printStackTrace();
                        fileShareCallback.onFailure(e.getMessage());
                        return;
                    }
                }


                try {
                    batch.execute();
                } catch(IOException e){
                    e.printStackTrace();
                    fileShareCallback.onFailure(e.getMessage());
                }
            }
        }).start();
    }






    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Creates a folder on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void createFolder(final String folderId, final String title, final String description, final FolderCreateCallback folderCreateCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // *Get the Drive service instance:
                    com.google.api.services.drive.Drive service = getDriveService();

                    // *File's metadata:
                    File driveMetadata = new File();
                    driveMetadata.setTitle(title);
                    driveMetadata.setDescription(description);
                    driveMetadata.setMimeType(EMimeType.FOLDER.getMimeType());

                    // *Set the parent folder:
                    if (folderId != null && !folderId.isEmpty()) {
                        driveMetadata.setParents(Arrays.asList(new ParentReference().setId(folderId)));
                    } else{
                        driveMetadata.setParents(Arrays.asList(new ParentReference().setId(service.about().get().execute().getRootFolderId())));
                    }

                    // *Insert the file to Drive:
                    File driveFile = service.files().insert(driveMetadata).execute();

                    // *Return the file id to the callback:
                    folderCreateCallback.onSuccess(driveFile.getId());
                } catch (IOException e){
                    e.printStackTrace();
                    folderCreateCallback.onFailure(e.getMessage());
                } catch (Exception e){
                    e.printStackTrace();
                    folderCreateCallback.onFailure(e.getMessage());
                }
            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Deletes a folder on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void deleteFolder(final FolderDeleteCallback folderDeleteCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Shares a folder on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void shareFolder(final FolderShareCallback folderShareCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
}
