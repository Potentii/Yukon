package com.sharman.yukon.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;
import com.google.api.services.drive.model.File;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.io.drive.util.EMimeType;
import com.sharman.yukon.R;
import com.sharman.yukon.io.drive.DriveIOHandler;
import com.sharman.yukon.io.drive.callback.FileQueryCallback;
import com.sharman.yukon.io.drive.callback.FileReadCallback;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonContactsListCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.StudentConfigs;
import com.sharman.yukon.model.TeacherConfigs;
import com.sharman.yukon.view.activities.answering.ExamAnsweringActivity;
import com.sharman.yukon.view.activities.creation.ExamCreateActivity;
import com.sharman.yukon.view.activities.managing.ExamManagingActivity;
import com.sharman.yukon.view.activities.util.recycler.ExamRVAdapter;
import com.sharman.yukon.view.activities.util.recycler.ExamRVInfo;
import com.sharman.yukon.view.activities.util.recycler.OnExamRVItemClickListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.model.Person;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.io.IOException;
import java.net.URL;


public class MainActivity extends GoogleRestConnectActivity {
    private ExamRVAdapter myExamRVAdapter;
    private ExamRVAdapter sharedExamRVAdapter;

    private RecyclerView myExamRecyclerView;
    private RecyclerView sharedExamRecyclerView;

    private Vector<ExamRVInfo> myExamRVInfoVector;
    private Vector<ExamRVInfo> sharedExamRVInfoVector;

    private int myExamLoaded;
    private boolean onMyUpdateCalled;
    private int sharedExamLoaded;
    private boolean onSharedUpdateCalled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myExamRVInfoVector = new Vector<>();
        sharedExamRVInfoVector = new Vector<>();

        myExamRVAdapter = new ExamRVAdapter(this, getCredential(), myExamRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(ExamRVInfo examRVInfo) {
                Intent examManagingIntent = new Intent(getApplicationContext(), ExamManagingActivity.class);
                examManagingIntent.putExtra("teacherConfigs", examRVInfo.getConfigs());
                startActivity(examManagingIntent);
            }
        });

        sharedExamRVAdapter = new ExamRVAdapter(this, getCredential(), sharedExamRVInfoVector, new OnExamRVItemClickListener() {
            @Override
            public void onClick(ExamRVInfo examRVInfo) {
                Intent examAnsweringIntent = new Intent(getApplicationContext(), ExamAnsweringActivity.class);
                examAnsweringIntent.putExtra("studentConfigs", examRVInfo.getConfigs());
                startActivity(examAnsweringIntent);
            }
        });

        myExamRecyclerView = (RecyclerView) findViewById(R.id.myExamRecyclerView);
        myExamRecyclerView.setAdapter(myExamRVAdapter);
        myExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedExamRecyclerView = (RecyclerView) findViewById(R.id.sharedExamRecyclerView);
        sharedExamRecyclerView.setAdapter(sharedExamRVAdapter);
        sharedExamRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }




    private com.google.api.services.admin.directory.Directory getService(){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new com.google.api.services.admin.directory.Directory.Builder(transport, jsonFactory, getCredential())
                .setApplicationName("Yukon")
                .build();
    }


    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        updateMyExamList();
        updateSharedExamList();

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.google.api.services.admin.directory.Directory service = getService();

                try {
                    Users result = service.users().list()
                            .setCustomer("my_customer")
                            .setMaxResults(10)
                            .setOrderBy("email")
                            .setViewType("domain_public")
                            .execute();
                    List<User> users = result.getUsers();
                    if (users != null) {
                        for (User user : users) {
                            System.out.println("ID: " + user.getId());
                            System.out.println("Name: " + user.getName().getFullName());
                            System.out.println("Prim Email: " + user.getPrimaryEmail());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContactsService service = getService();

                try {
                    URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
                    ContactFeed resultFeed = service.getFeed(feedUrl, ContactFeed.class);

                    // Print the results
                    System.out.println(resultFeed.getTitle().getPlainText());

                    for(ContactEntry entry : resultFeed.getEntries()) {

                            if (entry.hasName()) {
                                Name name = entry.getName();
                                if (name.hasFullName()) {
                                    String fullNameToDisplay = name.getFullName().getValue();
                                    if (name.getFullName().hasYomi()) {
                                        fullNameToDisplay += " (" + name.getFullName().getYomi() + ")";
                                    }
                                    System.out.println("\t\t" + fullNameToDisplay);
                                } else {
                                    System.out.println("\t\t (no full name found)");
                                }
                                if (name.hasNamePrefix()) {
                                    System.out.println("\t\t" + name.getNamePrefix().getValue());
                                } else {
                                    System.out.println("\t\t (no name prefix found)");
                                }
                                if (name.hasGivenName()) {
                                    String givenNameToDisplay = name.getGivenName().getValue();
                                    if (name.getGivenName().hasYomi()) {
                                        givenNameToDisplay += " (" + name.getGivenName().getYomi() + ")";
                                    }
                                    System.out.println("\t\t" + givenNameToDisplay);
                                } else {
                                    System.out.println("\t\t (no given name found)");
                                }
                                if (name.hasAdditionalName()) {
                                    String additionalNameToDisplay = name.getAdditionalName().getValue();
                                    if (name.getAdditionalName().hasYomi()) {
                                        additionalNameToDisplay += " (" + name.getAdditionalName().getYomi() + ")";
                                    }
                                    System.out.println("\t\t" + additionalNameToDisplay);
                                } else {
                                    System.out.println("\t\t (no additional name found)");
                                }
                                if (name.hasFamilyName()) {
                                    String familyNameToDisplay = name.getFamilyName().getValue();
                                    if (name.getFamilyName().hasYomi()) {
                                        familyNameToDisplay += " (" + name.getFamilyName().getYomi() + ")";
                                    }
                                    System.out.println("\t\t" + familyNameToDisplay);
                                } else {
                                    System.out.println("\t\t (no family name found)");
                                }
                                if (name.hasNameSuffix()) {
                                    System.out.println("\t\t" + name.getNameSuffix().getValue());
                                } else {
                                    System.out.println("\t\t (no name suffix found)");
                                }
                            } else {
                                System.out.println("\t (no name found)");
                            }



                            System.out.println("Email addresses:");
                            for (Email email : entry.getEmailAddresses()) {
                                System.out.print(" " + email.getAddress());
                                if (email.getRel() != null) {
                                    System.out.print(" rel:" + email.getRel());
                                }
                                if (email.getLabel() != null) {
                                    System.out.print(" label:" + email.getLabel());
                                }
                                if (email.getPrimary()) {
                                    System.out.print(" (primary) ");
                                }
                                System.out.print("\n");
                            }



                            System.out.println("IM addresses:");
                            for (Im im : entry.getImAddresses()) {
                                System.out.print(" " + im.getAddress());
                                if (im.getLabel() != null) {
                                    System.out.print(" label:" + im.getLabel());
                                }
                                if (im.getRel() != null) {
                                    System.out.print(" rel:" + im.getRel());
                                }
                                if (im.getProtocol() != null) {
                                    System.out.print(" protocol:" + im.getProtocol());
                                }
                                if (im.getPrimary()) {
                                    System.out.print(" (primary) ");
                                }
                                System.out.print("\n");
                            }



                            System.out.println("Groups:");
                            for (GroupMembershipInfo group : entry.getGroupMembershipInfos()) {
                                String groupHref = group.getHref();
                                System.out.println("  Id: " + groupHref);
                            }



                            System.out.println("Extended Properties:");
                            for (ExtendedProperty property : entry.getExtendedProperties()) {
                                if (property.getValue() != null) {
                                    System.out.println("  " + property.getName() + "(value) = " +
                                            property.getValue());
                                } else if (property.getXmlBlob() != null) {
                                    System.out.println("  " + property.getName() + "(xmlBlob)= " +
                                            property.getXmlBlob().getBlob());
                                }
                            }



                            Link photoLink = entry.getContactPhotoLink();
                            String photoLinkHref = photoLink.getHref();
                            System.out.println("Photo Link: " + photoLinkHref);
                            if (photoLink.getEtag() != null) {
                                System.out.println("Contact Photo's ETag: " + photoLink.getEtag());
                            }


                            System.out.println("Contact's ETag: " + entry.getEtag());
                        }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
        */


        /*
        new PlusIOHandler(getCredential()).listPersonContacts("me", new PersonContactsListCallback() {
            @Override
            public void onResult(List<Person> personList) {
                for(Person person : personList){
                    List<Person.Emails> emailsList = person.getEmails();
                    try {
                        for (Person.Emails email : emailsList){
                            System.out.println("Value: " + email.getValue());
                            System.out.println("Str: " + email.toString());
                            System.out.println("Pretty Str: " + email.toPrettyString());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        */
        /*
        new Thread(new Runnable() {
            private com.google.api.services.plus.Plus getPlusService(){
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                return new com.google.api.services.plus.Plus.Builder(
                        transport, jsonFactory, getCredential())
                        .setApplicationName("Yukon")
                        .build();
            }
            @Override
            public void run() {
                try {
                    System.out.println(">> Comeco <<");
                    Plus.People.List listPeople = getPlusService().people().list("me", "visible");
                    PeopleFeed peopleFeed = listPeople.execute();
                    listPeople.setMaxResults(5L);
                    List<Person> personList = listPeople.execute().getItems();


                    while (personList != null) {
                        for (Person person : personList) {
                            System.out.println(person.getDisplayName());
                        }

                        // We will know we are on the last page when the next page token is
                        // null.
                        // If this is the case, break.
                        if (peopleFeed.getNextPageToken() == null) {
                            break;
                        }

                        // Prepare the next page of results
                        listPeople.setPageToken(peopleFeed.getNextPageToken());

                        // Execute and process the next page request
                        peopleFeed = listPeople.execute();
                        personList = peopleFeed.getItems();
                    }
                    System.out.println(">> FIM <<");
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();
        */
    }


    public void addExamBtn_onClick(View view) {
        Intent addExamIntent = new Intent(this, ExamCreateActivity.class);
        startActivity(addExamIntent);
        //finish();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Update List:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    private void updateMyExamList(){
        myExamLoaded = 0;
        onMyUpdateCalled = false;
        myExamRVInfoVector.clear();

        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.queryFiles("mimeType = '" + EMimeType.TEACHER_CONFIG.getMimeType() + "'", new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {

                for (int i = 0; i < driveFileList.size(); i++) {

                    driveIOHandler.readFile(driveFileList.get(i), new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                TeacherConfigs teacherConfigs = new TeacherConfigs(content);

                                myExamRVInfoVector.add(new ExamRVInfo(
                                        teacherConfigs.getTeacherIdCache(),
                                        teacherConfigs.getExamTitleCache(),
                                        teacherConfigs.getExamSubjectCache(),
                                        teacherConfigs.getExamDeliveryDateCache(),
                                        content));
                                /*
                                myExamRVInfoVector.add(new ExamRVInfo(
                                        teacherConfigs.getTeacherIdCache(),
                                        teacherConfigs.getExamTitleCache(),
                                        teacherConfigs.getExamSubjectCache(),
                                        teacherConfigs.getExamDeliveryDateCache(),
                                        teacherConfigs.getExamFileId(),
                                        teacherConfigs.getCorrectAnswersFileId()));
                                        */

                                myExamLoaded++;
                                if (myExamLoaded == driveFileList.size()) {
                                    onMyExamUpdateSuccess();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            // TODO Error Configs file
                            onMyExamUpdateFailure();
                        }
                    });
                }
            }
        });
    }


    // *Shared:
    private void updateSharedExamList() {
        sharedExamLoaded = 0;
        onSharedUpdateCalled = false;
        sharedExamRVInfoVector.clear();

        final DriveIOHandler driveIOHandler = new DriveIOHandler(getCredential());
        driveIOHandler.queryFiles("mimeType = '" + EMimeType.STUDENT_CONFIG.getMimeType() + "' and sharedWithMe", new FileQueryCallback() {
            @Override
            public void onResult(final List<File> driveFileList) {

                for (int i = 0; i < driveFileList.size(); i++) {
                    driveIOHandler.readFile(driveFileList.get(i), new FileReadCallback() {
                        @Override
                        public void onSuccess(String content) {
                            try {
                                StudentConfigs studentConfigs = new StudentConfigs(content);

                                sharedExamRVInfoVector.add(new ExamRVInfo(
                                        studentConfigs.getTeacherIdCache(),
                                        studentConfigs.getExamTitleCache(),
                                        studentConfigs.getExamSubjectCache(),
                                        studentConfigs.getExamDeliveryDateCache(),
                                        content));
                                /*
                                sharedExamRVInfoVector.add(new ExamRVInfo(
                                        studentConfigs.getTeacherIdCache(),
                                        studentConfigs.getExamTitleCache(),
                                        studentConfigs.getExamSubjectCache(),
                                        studentConfigs.getExamDeliveryDateCache(),
                                        studentConfigs.getExamFileId(),
                                        studentConfigs.getAnswersFileId(),
                                        studentConfigs.getGradeFileId()));
                                        */

                                sharedExamLoaded++;
                                if (sharedExamLoaded == driveFileList.size()) {
                                    onSharedExamUpdateSuccess();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            // TODO Error Configs file
                            onSharedExamUpdateFailure();
                        }
                    });
                }
            }
        });
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * OnUpdate:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *My:
    private void onMyExamUpdateSuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onMyUpdateCalled){
                    onMyUpdateCalled = true;
                    System.out.println("SUCCESS");
                    myExamRecyclerView.getAdapter().notifyDataSetChanged();
                    myExamRecyclerView.setAdapter(myExamRVAdapter);
                    myExamRecyclerView.getLayoutParams().height = 138 * myExamRVInfoVector.size();
                }
            }
        });
    }
    private void onMyExamUpdateFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onMyUpdateCalled){
                    onMyUpdateCalled = true;
                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    System.out.println("FAILURE");
                }
            }
        });
    }



    // *Shared:
    private void onSharedExamUpdateSuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onSharedUpdateCalled){
                    onSharedUpdateCalled = true;
                    System.out.println("SUCCESS");
                    sharedExamRecyclerView.getAdapter().notifyDataSetChanged();
                    sharedExamRecyclerView.setAdapter(sharedExamRVAdapter);
                    sharedExamRecyclerView.getLayoutParams().height = 138 * sharedExamRVInfoVector.size();
                }
            }
        });
    }
    private void onSharedExamUpdateFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!onSharedUpdateCalled){
                    onSharedUpdateCalled = true;
                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    System.out.println("FAILURE");
                }
            }
        });
    }
}
