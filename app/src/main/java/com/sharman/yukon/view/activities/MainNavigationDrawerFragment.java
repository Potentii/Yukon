package com.sharman.yukon.view.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;

public class MainNavigationDrawerFragment extends Fragment {
    private static final String PREF_FILE_NAME = "drawer";
    private static final String KEY_USER_LEARNED = "userLearned";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View container;

    private GoogleAccountCredential credential;

    private boolean mUserLearned;
    private boolean mFromSavedIntanceState;



    public MainNavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearned = Boolean.valueOf(readToSharedPreferences(getActivity(), KEY_USER_LEARNED, "false"));
        if(savedInstanceState != null){
            mFromSavedIntanceState = true;


            final PlusIOHandler plusIOHandler = new PlusIOHandler(credential);
            plusIOHandler.readPerson("me", new PersonReadCallback() {
                @Override
                public void onSuccess(Person person) {

                }

                @Override
                public void onFailure(Exception exception) {

                }
            });

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_navigation_drawer, container, false);
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        container = getActivity().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.navigationDrawer_open, R.string.navigationDrawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(!mUserLearned){
                    mUserLearned = true;
                    writeToSharedPreferences(getActivity(), KEY_USER_LEARNED, mUserLearned+"");
                }

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                getActivity().invalidateOptionsMenu();
            }
        };

        if(!mUserLearned && !mFromSavedIntanceState){
            mDrawerLayout.openDrawer(container);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }



    private static void writeToSharedPreferences(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String readToSharedPreferences(Context context, String key, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

}
