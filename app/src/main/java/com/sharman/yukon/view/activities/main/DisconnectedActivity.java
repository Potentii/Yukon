package com.sharman.yukon.view.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sharman.yukon.R;
import com.sharman.yukon.view.activities.GoogleRestConnectActivity;
import com.sharman.yukon.view.activities.util.AndroidUtil;

public class DisconnectedActivity extends GoogleRestConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnected);

        //new AndroidUtil(this).showToast("Connecting...", Toast.LENGTH_SHORT);

    }


    public void connectBtn_onClick(View view){
        tryToConnect();
    }

    public void disconnectBtn_onClick(View view){
        tryToDisconnect();
    }



    @Override
    protected void onConnectOnce(){
        super.onConnectOnce();

        AndroidUtil androidUtil = new AndroidUtil(this);
        String favRole = androidUtil.readFromSharedPreferences(CONFIG_FILE, CONFIG_FAV_ROLE_KEY, null);

        if(favRole == null){
            favRole = CONFIG_FAV_ROLE_STUDENT;
            androidUtil.writeToSharedPreferences(CONFIG_FILE, CONFIG_FAV_ROLE_KEY, favRole);
        }

        if(favRole.equals(CONFIG_FAV_ROLE_STUDENT)){
            Intent studentMainActivityIntent = new Intent(this, StudentMainActivity.class);
            startActivity(studentMainActivityIntent);
            finish();
        } else if(favRole.equals(CONFIG_FAV_ROLE_TEACHER)){
            Intent teacherMainActivityIntent = new Intent(this, TeacherMainActivity.class);
            startActivity(teacherMainActivityIntent);
            finish();
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_disconnected, menu);
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
