package com.sharman.yukon;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;

/**
 * Created by poten on 04/10/2015.
 */
public abstract class GoogleConnectActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    protected GoogleApiClient googleApiClient;

    private static final int REQUEST_CODE_RESOLUTION = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.googleApiClient = new GoogleApiClient.Builder(this)

                // *Adding the Plus API to the client:
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))

                // *Adding the Drive API to the client:
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)

                // *Adding the callbacks to handle with google connection events:
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        this.googleApiClient.disconnect();
        this.googleApiClient.connect();
    }


    @Override
    public void onStart(){
        super.onStart();
        this.googleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        this.googleApiClient.disconnect();
    }


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
}
