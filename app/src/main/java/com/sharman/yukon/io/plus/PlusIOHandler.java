package com.sharman.yukon.io.plus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.io.plus.callback.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by poten on 12/10/2015.
 */
public final class PlusIOHandler {
    private GoogleAccountCredential credential;
    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public PlusIOHandler(GoogleAccountCredential credential) {
        this.credential = credential;
    }


    private com.google.api.services.plus.Plus getPlusService(){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new com.google.api.services.plus.Plus.Builder(
                transport, jsonFactory, this.credential)
                .setApplicationName("Yukon")
                .build();
    }


    public void readPerson(@Nonnull final String userId, @Nonnull final PersonReadCallback personReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.google.api.services.plus.Plus service = getPlusService();

                try {
                    Person person = service.people().get(userId).execute();
                    personReadCallback.onSuccess(person);
                } catch (IOException e) {
                    e.printStackTrace();
                    personReadCallback.onFailure(e);
                }
            }
        }).start();
    }



    public void readPersonImg(@Nonnull final Person person, @Nonnull final PersonImgReadCallback personImgReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bitmapURL = person.getImage().getUrl();
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(bitmapURL).getContent());
                    personImgReadCallback.onSuccess(bitmap, bitmapURL);
                } catch(NullPointerException | IOException e) {
                    e.printStackTrace();
                    personImgReadCallback.onFailure(e.getMessage());
                }
            }
        }).start();
    }



    public void readPersonImg(@Nonnull final String userId, @Nonnull final PersonImgReadCallback personImgReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.google.api.services.plus.Plus service = getPlusService();

                try {
                    Person person = service.people().get(userId).execute();
                    String bitmapURL = person.getImage().getUrl();
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(bitmapURL).getContent());
                    personImgReadCallback.onSuccess(bitmap, bitmapURL);
                } catch(NullPointerException | IOException e) {
                    e.printStackTrace();
                    personImgReadCallback.onFailure(e.getMessage());
                }
            }
        }).start();
    }



    public void readPersonCoverImg(@Nonnull final Person person, @Nonnull final PersonImgReadCallback personImgReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bitmapURL = person.getCover().getCoverPhoto().getUrl();
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(bitmapURL).getContent());
                    personImgReadCallback.onSuccess(bitmap, bitmapURL);
                } catch(NullPointerException | IOException e) {
                    e.printStackTrace();
                    personImgReadCallback.onFailure(e.getMessage());
                }
            }
        }).start();
    }

    public void readPersonCoverImg(@Nonnull final String userId, @Nonnull final PersonImgReadCallback personImgReadCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.google.api.services.plus.Plus service = getPlusService();

                try {
                    Person person = service.people().get(userId).execute();
                    String bitmapURL = person.getCover().getCoverPhoto().getUrl();
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(bitmapURL).getContent());
                    personImgReadCallback.onSuccess(bitmap, bitmapURL);
                } catch(NullPointerException | IOException e) {
                    e.printStackTrace();
                    personImgReadCallback.onFailure(e.getMessage());
                }
            }
        }).start();
    }



    public void listPersonContacts(@Nonnull final String userId, @Nonnull final PersonContactsListCallback personContactsListCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.google.api.services.plus.Plus service = getPlusService();

                List<Person> personList = new ArrayList<Person>();

                try {
                    Plus.People.List listPeople = getPlusService().people().list(userId, "visible");
                    listPeople.setOrderBy("alphabetical");

                    PeopleFeed peopleFeed = listPeople.execute();

                    personList = peopleFeed.getItems();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                personContactsListCallback.onResult(personList);
            }
        }).start();
    }


    public void getCoverURL(@Nonnull final String userId, @Nonnull final PhotoURLCallback photoURLCallback){
        readPerson(userId, new PersonReadCallback() {
            @Override
            public void onSuccess(Person person) {
                photoURLCallback.onSuccess(person.getCover().getCoverPhoto().getUrl());
            }

            @Override
            public void onFailure(Exception exception) {
                photoURLCallback.onFailure(exception);
            }
        });
    }

    public void getImgURL(@Nonnull final String userId, @Nonnull final PhotoURLCallback photoURLCallback){
        readPerson(userId, new PersonReadCallback() {
            @Override
            public void onSuccess(Person person) {
                photoURLCallback.onSuccess(person.getImage().getUrl());
            }

            @Override
            public void onFailure(Exception exception) {
                photoURLCallback.onFailure(exception);
            }
        });
    }

}
