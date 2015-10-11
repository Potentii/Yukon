package com.sharman.yukon.view.activities;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.sharman.yukon.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by poten on 04/10/2015.
 */
@Deprecated
public abstract class GoogleConnectActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private GoogleAccountCredential credential;
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { DriveScopes.DRIVE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // *Initialize Google Android API client:
        this.googleApiClient = new GoogleApiClient.Builder(this)
                // *Adding the Plus API to the client:
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)

                // *Adding the Drive API to the client:
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)

                // *Adding the callbacks to handle with google connection events:
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // *Initialize Google REST API credential:
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));


        //this.googleApiClient.disconnect();
        //this.googleApiClient.connect();
    }


    @Override
    public void onStart(){
        super.onStart();
        this.googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // *Verifies if google play services are available:
        isGooglePlayServicesAvailable();

        // *Verifies if the user has logged in:
        if(credential.getSelectedAccountName() == null) {
            chooseAccount();
        }/* else{
            new MakeRequestTask(credential).execute();
        }*/
    }

    @Override
    public void onStop(){
        super.onStop();
        this.googleApiClient.disconnect();
    }


    /* *
     * * Google REST API connection workflow:
     * *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    System.out.println("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }


    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }


    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, GoogleConnectActivity.this, REQUEST_GOOGLE_PLAY_SERVICES).show();
    }


    /*
    protected boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
    */


    /* *
     * * Google Android API connection workflow:
     * *
     */
    // *Quando estabelecer conexão com o google:
    @Override
    public void onConnected(Bundle bundle){
        Toast.makeText(this, "CONECTADO", Toast.LENGTH_SHORT).show();
    }


    // *Quando a conexão com o google for suspensa:
    @Override
    public void onConnectionSuspended(int i){
    }


    // *Quando a tentativa de conexão com o google falhar:
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        if(connectionResult.hasResolution()) {
            try{
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch(IntentSender.SendIntentException e){
                Toast.makeText(this, "Houve uma falha na conexão", Toast.LENGTH_SHORT);
            }
        } else{
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
    public GoogleAccountCredential getCredential() {
        return credential;
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

        // Before execute
        @Override
        protected void onPreExecute() {
        }


        // After execute
        @Override
        protected void onPostExecute(List<String> output) {
            for (String fileName : output) {
                System.out.println(fileName);
            }
        }


        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleConnectActivity.REQUEST_AUTHORIZATION);
                } else {
                    System.out.println("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                System.out.println("Request cancelled.");
            }
        }
    }*/
}
