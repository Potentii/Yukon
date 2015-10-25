package com.sharman.yukon.view.activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.plus.PlusScopes;

import java.io.IOException;
import java.util.Arrays;

public abstract class GoogleRestConnectActivity extends ActionBarActivity {
    private static GoogleAccountCredential credential;
    protected static final int REQUEST_ACCOUNT_PICKER = 1000;
    protected static final int REQUEST_AUTHORIZATION = 1001;
    protected static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    protected static final String PREF_ACCOUNT_NAME = "accountName";
    protected static final String PREF_FILE = "account";

    protected static final String[] SCOPES = {
            DriveScopes.DRIVE,
            PlusScopes.USERINFO_PROFILE,
            PlusScopes.PLUS_ME,
            PlusScopes.PLUS_LOGIN,
            PlusScopes.USERINFO_EMAIL
    };
    private boolean connected;
    private boolean connectedOnceCalled;



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Android methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setConnected(false);
        connectedOnceCalled = false;
    }

    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences settings = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String accountNameCached = settings.getString(PREF_ACCOUNT_NAME, null);


        if(accountNameCached != null && !accountNameCached.equals("")){
            tryToConnect();
        }
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Credential methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected GoogleAccountCredential generateCredential(){
        SharedPreferences settings = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String accountNameCached = settings.getString(PREF_ACCOUNT_NAME, null);

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(accountNameCached);

        GoogleRestConnectActivity.credential = credential;
        return credential;
    }


    protected void removeCredential(){
        SharedPreferences settings = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, null);
        editor.apply();

        credential = null;
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Connection callbacks:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    // *Connected:
    protected void connectedFlag(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setConnected(true);
                if(!connectedOnceCalled){
                    connectedOnceCalled = true;
                    onConnectOnce();
                }
                onConnect();

            }
        });
    }

    protected void onConnect(){
    }

    protected void onConnectOnce(){
        System.out.println(">> CONNECTED ONCE <<");
        try {
            System.out.println(credential.getSelectedAccountName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // *Disconnected:
    protected void disconnectFlag(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setConnected(false);
                onDisconnect();
                connectedOnceCalled = false;

            }
        });
    }

    protected void onDisconnect(){
        System.out.println(">> DISCONNECTED <<");
        if(!(this instanceof DisconnectedActivity)){
            Intent backToLoginScreenIntent = new Intent(this, DisconnectedActivity.class);
            startActivity(backToLoginScreenIntent);
            finish();
        }
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Authorization methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected void tryToConnect(){
        if(isGooglePlayServicesAvailable()) {
            if (getCredential() == null) {
                generateCredential();
            }

            if (getCredential().getSelectedAccountName() == null) {
                chooseAccount();
            } else{
                requestAuthorization();
            }
        }
    }

    protected void tryToDisconnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    getCredential().getGoogleAccountManager().invalidateAuthToken(getCredential().getToken());
                    removeCredential();
                    disconnectFlag();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }




    private void requestAuthorization(){
        if(getCredential() != null && getCredential().getSelectedAccountName() != null && !getCredential().getSelectedAccountName().equals("")) {
            final StringBuilder sb = new StringBuilder();

            sb.append("oauth2:");
            for (int i = 0; i < SCOPES.length; i++) {
                sb.append(SCOPES[i]);
                if (i != SCOPES.length - 1) {
                    sb.append(" ");
                }
            }

            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (isGooglePlayServicesAvailable()) {
                        String token = "";
                        try {

                            token = getCredential().getToken();
                            getCredential().getGoogleAccountManager().invalidateAuthToken(token);
                            token = getCredential().getToken();

                        } catch (UserRecoverableAuthException e) {
                            // *User can do something:
                            e.printStackTrace();
                            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);

                        } catch (GoogleAuthException e) {
                            // *User can't do something:
                            e.printStackTrace();

                        } catch (IOException e) {
                            // *The authentication failed due to some problem, maybe we need to ask if the user want to retry:
                            e.printStackTrace();
                        } finally {
                            if(!token.equals("")){
                                // *When user alread had been logged and authorized this app:
                                connectedFlag();
                            }
                        }
                    }
                }
            }).start();
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    System.out.println("REQUEST_GOOGLE_PLAY_SERVICES OK");
                } else{
                    System.out.println("REQUEST_GOOGLE_PLAY_SERVICES NOT OK");
                    isGooglePlayServicesAvailable();
                }
                break;

            // *Called after the account picker:
            case REQUEST_ACCOUNT_PICKER:

                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    System.out.println("REQUEST_ACCOUNT_PICKER OK");

                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        // *If the user authorize:
                        getCredential().setSelectedAccountName(accountName);
                        SharedPreferences settings = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();

                        requestAuthorization();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    System.out.println("REQUEST_ACCOUNT_PICKER NOT OK");
                    disconnectFlag();
                }
                break;

            // *Called after the consent screen:
            case REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    System.out.println(">> REQUEST_AUTHORIZATION OK <<");
                    connectedFlag();
                } else {
                    System.out.println(">> REQUEST_AUTHORIZATION NOT OK <<");
                    disconnectFlag();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    private void chooseAccount() {
        startActivityForResult(getCredential().newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    protected boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            setConnected(false);
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            setConnected(false);
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, GoogleRestConnectActivity.this, REQUEST_GOOGLE_PLAY_SERVICES).show();
    }


    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and Setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public boolean isConnected() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public static GoogleAccountCredential getCredential() {
        return credential;
    }
}
