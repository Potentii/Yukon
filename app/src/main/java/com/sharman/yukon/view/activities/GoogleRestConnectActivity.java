package com.sharman.yukon.view.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;
import com.sharman.yukon.model.YukonAccount;
import com.sharman.yukon.model.YukonAccountKeeper;
import com.sharman.yukon.view.activities.util.AndroidUtil;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GoogleRestConnectActivity extends AppCompatActivity {
    private static GoogleAccountCredential credential;
    protected static final int REQUEST_ACCOUNT_PICKER = 1000;
    protected static final int REQUEST_AUTHORIZATION = 1001;
    protected static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    protected static final String ACCOUNT_KEEPER_KEY = "accountKeeper";
    protected static final String ACCOUNT_FILE = "accounts";
    protected static final String CONFIG_FILE = "appConfigs";
    protected static final String CONFIG_FAV_ROLE_KEY = "favRole";
    protected static final String CONFIG_FAV_ROLE_STUDENT = "student";
    protected static final String CONFIG_FAV_ROLE_TEACHER = "teacher";

    protected YukonAccountKeeper yukonAccountKeeper;

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


        // *Try to set the actionToolbar:
        try {
            Toolbar actionToolbar = (Toolbar) findViewById(R.id.actionToolbar);
            setSupportActionBar(actionToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e){
            System.err.println("No actionBar");
        }

        try {
            yukonAccountKeeper = getYukonAccountKeeperRegister();
        } catch (NullPointerException | JSONException e) {
            yukonAccountKeeper = new YukonAccountKeeper(null, new YukonAccount[]{});
        }

        if(yukonAccountKeeper.getMainAccount() != null){
            tryToConnect();
        }
    }




    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Credential methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    protected GoogleAccountCredential generateCredential(){
        String accountName;

        try{
            accountName = yukonAccountKeeper.getMainAccount().getEmail();
        }catch (NullPointerException e){
            accountName = null;
        }

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(accountName);

        GoogleRestConnectActivity.credential = credential;
        return credential;
    }


    protected void removeCredential(){
        AndroidUtil androidUtil = new AndroidUtil(this);

        try {
            YukonAccountKeeper yukonAccountKeeper = new YukonAccountKeeper(androidUtil.readFromSharedPreferences(ACCOUNT_FILE, ACCOUNT_KEEPER_KEY, null));
            yukonAccountKeeper.setMainAccount(null);
            androidUtil.writeToSharedPreferences(ACCOUNT_FILE, ACCOUNT_KEEPER_KEY, yukonAccountKeeper.toString());
        }catch (NullPointerException | JSONException e){
            androidUtil.writeToSharedPreferences(ACCOUNT_FILE, ACCOUNT_KEEPER_KEY, null);
        }

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
            System.out.println(yukonAccountKeeper.toString());
            System.out.println(getYukonAccountKeeperRegister().toString());
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


    private boolean addingAccount = false;


    protected void switchAccount(@Nonnull String accountEmail){
        onAccountChoose(accountEmail);
    }

    protected void addAccount(){
        addingAccount = true;
        chooseAccount();
    }




    private void requestAuthorization(){
        if(getCredential() != null && getCredential().getSelectedAccountName() != null && !getCredential().getSelectedAccountName().trim().equals("")) {
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
                        onAccountChoose(accountName);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    System.out.println("REQUEST_ACCOUNT_PICKER NOT OK");
                    onAccountNotChoose();
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




    private void onAccountChoose(String accountName){
        try {
            YukonAccount[] secondaryAccountArray = yukonAccountKeeper.getSecondaryAccountArray();

            if(!addingAccount){
                secondaryAccountArray = removeSecondaryAccount(secondaryAccountArray, accountName);
            }
            secondaryAccountArray = addSecondaryAccount(secondaryAccountArray, yukonAccountKeeper.getMainAccount());

            yukonAccountKeeper.setSecondaryAccountArray(secondaryAccountArray);
            yukonAccountKeeper.setMainAccount(new YukonAccount(null, accountName, null));

            commitYukonAccountKeeperRegister();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        generateCredential();
        requestAuthorization();

        if(addingAccount) {
            addingAccount = false;
            Intent refreshIntent = new Intent(this, getClass());
            startActivity(refreshIntent);
            finish();
        }
    }

    private void onAccountNotChoose(){
        // *If the user is just adding new accounts:
        if(addingAccount){
            addingAccount = false;
            return;
        }

        disconnectFlag();
    }


    private YukonAccount[] addSecondaryAccount(YukonAccount[] originalArray, YukonAccount newAccount){
        if(newAccount == null){
            return originalArray;
        }

        originalArray = originalArray == null
                ? new YukonAccount[]{}
                : originalArray;
        final int initialSize = originalArray.length;

        try {
            for(YukonAccount yukonAccount : originalArray){
                if(yukonAccount.getEmail().equals(newAccount.getEmail())){
                    return originalArray;
                }
            }
        } catch (NullPointerException e){
            return originalArray;
        }

        YukonAccount[] newArray = new YukonAccount[initialSize+1];
        System.arraycopy(originalArray, 0, newArray, 0, initialSize);
        newArray[initialSize] = newAccount;

        return newArray;
    }

    private YukonAccount[] removeSecondaryAccount(YukonAccount[] originalArray, String accountName){
        originalArray = originalArray == null
                ? new YukonAccount[]{}
                : originalArray;
        final int initialSize = originalArray.length;

        List<YukonAccount> yukonAccountList = new ArrayList<>();

        for(int i=0; i<initialSize; i++){
            if(!originalArray[i].getEmail().equals(accountName)){
                yukonAccountList.add(originalArray[i]);
            }
        }

        return yukonAccountList.toArray(new YukonAccount[yukonAccountList.size()]);
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

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, GoogleRestConnectActivity.this, REQUEST_GOOGLE_PLAY_SERVICES).show();
    }



    protected void updateMainAccountInfo(){
        try {
            YukonAccount mainAccount = yukonAccountKeeper.getMainAccount();

            if(mainAccount.getUserId() == null || mainAccount.getUserId().trim().isEmpty()) {
                new PlusIOHandler(getCredential()).readPerson("me", new PersonReadCallback() {
                    @Override
                    public void onSuccess(final Person person) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    YukonAccount mainAccount = yukonAccountKeeper.getMainAccount();

                                    mainAccount.setUserId(person.getId());
                                    mainAccount.setDisplayName(person.getDisplayName());

                                    yukonAccountKeeper.setMainAccount(mainAccount);
                                    commitYukonAccountKeeperRegister();

                                    onMainAccountInfoUpdateSuccess(yukonAccountKeeper);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    onFailure(e);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        onMainAccountInfoUpdateFail(yukonAccountKeeper);
                    }
                });
            } else{
                onMainAccountInfoUpdateSuccess(yukonAccountKeeper);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    protected void onMainAccountInfoUpdateSuccess(YukonAccountKeeper yukonAccountKeeper){

    }
    protected void onMainAccountInfoUpdateFail(YukonAccountKeeper yukonAccountKeeper){

    }



    protected void commitYukonAccountKeeperRegister(){
        try {
            new AndroidUtil(this).writeToSharedPreferences(ACCOUNT_FILE, ACCOUNT_KEEPER_KEY, yukonAccountKeeper.toString());
        } catch (NullPointerException e){
            new AndroidUtil(this).writeToSharedPreferences(ACCOUNT_FILE, ACCOUNT_KEEPER_KEY, null);
        }
    }

    protected YukonAccountKeeper getYukonAccountKeeperRegister() throws JSONException, NullPointerException {
        return new YukonAccountKeeper(new AndroidUtil(this).readFromSharedPreferences(ACCOUNT_FILE, ACCOUNT_KEEPER_KEY, null));
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

    @Nullable
    public ActionBar getActionToolbar(){
        return getSupportActionBar();
    }
}
