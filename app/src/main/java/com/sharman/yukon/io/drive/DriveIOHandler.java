package com.sharman.yukon.io.drive;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.sharman.yukon.io.drive.callback.FileCreateCallback;
import com.sharman.yukon.io.drive.callback.FileDeleteCallback;
import com.sharman.yukon.io.drive.callback.FileEditCallback;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.drive.callback.FileShareCallback;
import com.sharman.yukon.io.drive.callback.FileShareEditCallback;
import com.sharman.yukon.io.drive.callback.FolderCreateCallback;
import com.sharman.yukon.io.drive.callback.FolderDeleteCallback;
import com.sharman.yukon.io.drive.callback.FolderShareCallback;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.io.drive.util.PermissionStruct;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void readFile(final String fileId, final FileReadCallback fileReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // *Get the Drive service instance:
                com.google.api.services.drive.Drive service = getDriveService();

                try {
                    File file = service.files().get(fileId).execute();
                    String content = readFile(service, file);
                    fileReadCallback.onSuccess(content);
                } catch (IOException e) {
                    e.printStackTrace();
                    fileReadCallback.onFailure(e);
                    if(e instanceof UserRecoverableAuthIOException){
                        //GoogleRestConnectActivity.requestAuthorization((UserRecoverableAuthIOException) e);
                    }
                }
            }
        }).start();
    }

    public void readFile(final File file, final FileReadCallback fileReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // *Get the Drive service instance:
                com.google.api.services.drive.Drive service = getDriveService();

                try {
                    String content = readFile(service, file);
                    fileReadCallback.onSuccess(content);
                } catch (IOException e) {
                    e.printStackTrace();
                    fileReadCallback.onFailure(e);
                }
            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Query for files on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void queryFiles(final String query, final FileQueryCallback fileQueryCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // *Get the Drive service instance:
                com.google.api.services.drive.Drive service = getDriveService();
                List<File> driveFileList = new ArrayList<File>();

                try {
                    Drive.Files.List request = service.files().list().setQ(query);

                    do {
                        FileList files = request.execute();

                        driveFileList.addAll(files.getItems());
                        request.setPageToken(files.getNextPageToken());
                    } while (request.getPageToken() != null && request.getPageToken().length() > 0);

                    fileQueryCallback.onResult(driveFileList);
                } catch (IOException e){
                    e.printStackTrace();
                    fileQueryCallback.onResult(driveFileList);
                    return;
                }
            }
        }).start();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Edits a file on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void editFile(final String fileId, final String newTitle, final String newMimeType, final String newContent, final FileEditCallback fileEditCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // *Get the Drive service instance:
                com.google.api.services.drive.Drive service = getDriveService();

                try {
                    File file = service.files().get(fileId).execute();

                    if(newTitle != null){
                        file.setTitle(newTitle);
                    }

                    if(newMimeType != null){
                        file.setMimeType(newMimeType);
                    }

                    // *Write to the file:
                    ByteArrayContent fileContent = ByteArrayContent.fromString(file.getMimeType(), newContent);

                    File updatedFile = service.files().update(fileId, file, fileContent).execute();

                    fileEditCallback.onSuccess();
                } catch (IOException e){
                    e.printStackTrace();
                    fileEditCallback.onFailure(e.getMessage());
                }
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
     *  * Shares a file on Drive:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // Doesn't work for ownership transferring:
    public void editFileShare(final String fileId, final String userEMail, final String newRole, final FileShareEditCallback fileShareEditCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {

                // *Get the Drive service instance:
                com.google.api.services.drive.Drive service = getDriveService();


                try {
                    // *Retrieve the permission list for this file:
                    List<Permission> permissionList = service.permissions().list(fileId).execute().getItems();


                    // *Set the "permission edited" callback:
                    JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
                        @Override
                        public void onSuccess(Permission permission, HttpHeaders responseHeaders) {
                            fileShareEditCallback.onSuccess();
                        }

                        @Override
                        public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                            fileShareEditCallback.onFailure(e.getMessage());
                        }
                    };

                    BatchRequest batch = service.batch();

                    // *Searches for permissions with the user "userEMail":
                    for(int i=0; i<permissionList.size(); i++){
                        if(permissionList.get(i).getEmailAddress().equals(userEMail)){
                            Permission newPermission = new Permission();
                            newPermission.setValue(permissionList.get(i).getValue());
                            newPermission.setType(permissionList.get(i).getType());
                            newPermission.setRole(newRole);

                            service.permissions()
                                    .update(fileId, permissionList.get(i).getId(), newPermission)
                                    .queue(batch, callback);;
                        }
                    }

                    batch.execute();

                } catch (IOException e) {
                    e.printStackTrace();
                    fileShareEditCallback.onFailure(e.getMessage());
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









    private String readFile(com.google.api.services.drive.Drive service, File file) throws IOException{
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            InputStream inputStream = downloadFile(service, file);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
            buffer.close();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return sb.toString();
    }


    private InputStream downloadFile(com.google.api.services.drive.Drive service, File file) throws IOException{
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0){
            HttpResponse response = service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
            return response.getContent();
        } else{
            return null;
        }
    }
}
