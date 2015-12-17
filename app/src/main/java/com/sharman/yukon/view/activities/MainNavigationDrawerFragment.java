package com.sharman.yukon.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sharman.yukon.R;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PhotoURLCallback;
import com.sharman.yukon.model.YukonAccount;
import com.sharman.yukon.model.YukonAccountKeeper;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.GetResourceCacheCallback;
import com.sharman.yukon.view.activities.util.RegisterResourceCacheCallback;
import com.sharman.yukon.view.activities.util.ResourceCache;
import com.sharman.yukon.view.activities.util.ValidateResourceCacheCallback;

public class MainNavigationDrawerFragment extends Fragment {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout contentContainer;
    private LayoutInflater layoutInflater;
    private boolean showingMainList;



    public MainNavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_navigation_drawer, container, false);
    }


    public void setUp(final GoogleRestConnectActivity activity, DrawerLayout drawerLayout, Toolbar toolbar, YukonAccountKeeper yukonAccountKeeper, final GoogleAccountCredential credential) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, toolbar, R.string.navigationDrawer_open, R.string.navigationDrawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                activity.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                activity.invalidateOptionsMenu();
            }
        };


        final AndroidUtil androidUtil = new AndroidUtil(activity);

        try {
            View view = getView();
            layoutInflater = activity.getLayoutInflater();
            final ImageView mainAccountPhotoImg = (ImageView) view.findViewById(R.id.mainAccountImg);
            final ImageView mainAccountCoverImg = (ImageView) view.findViewById(R.id.mainAccountCoverImg);
            final TextView mainAccountNameOut = (TextView) view.findViewById(R.id.mainAccountNameOut);
            final TextView mainAccountEmailOut = (TextView) view.findViewById(R.id.mainAccountEmailOut);
            final ImageButton manageAccountsBtn = (ImageButton) view.findViewById(R.id.manageAccountsBtn);
            final LinearLayout secondaryAccountImgContainer = (LinearLayout) view.findViewById(R.id.secondaryAccountImgContainer);
            contentContainer = (RelativeLayout) view.findViewById(R.id.contentContainer);

            secondaryAccountImgContainer.removeAllViews();
            contentContainer.removeAllViews();


            final YukonAccount[] secondaryAccountArray = yukonAccountKeeper.getSecondaryAccountArray();
            final YukonAccount mainAccount = yukonAccountKeeper.getMainAccount();

            mainAccountNameOut.setText(mainAccount.getDisplayName());
            mainAccountEmailOut.setText(mainAccount.getEmail());


            // *Filling the secondary account container:
            if (secondaryAccountArray != null) {
                for (int i = 0; i < secondaryAccountArray.length; i++) {
                    final YukonAccount account = secondaryAccountArray[i];
                    ImageView photo = (ImageView) layoutInflater.inflate(R.layout.secondary_account_img, null);
                    System.out.println("Secondary: " + account.getUserId());
                    androidUtil.formatPersonImageView_GPlus(photo, credential, account.getUserId());
                    secondaryAccountImgContainer.addView(photo);

                    photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.switchAccount(account.getEmail());

                            Intent refreshIntent = new Intent(activity, activity.getClass());
                            startActivity(refreshIntent);
                            activity.finish();
                        }
                    });

                }
            }


            androidUtil.formatPersonImageView_GPlus(mainAccountPhotoImg, credential, mainAccount.getUserId());


            // *Try to get the user's G+ cover from cache, download otherwise:
            new ResourceCache(activity).getResource_GPlusProfileCover(mainAccount.getUserId(), new GetResourceCacheCallback<Bitmap, String>() {
                @Override
                public void onFound(final Bitmap cachedResource) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainAccountCoverImg.setImageBitmap(cachedResource);
                        }
                    });
                }

                @Override
                public void onNotFound(final RegisterResourceCacheCallback<Bitmap, String> registerResourceCacheCallback) {
                    new PlusIOHandler(credential).readPersonCoverImg(mainAccount.getUserId(), new PersonImgReadCallback() {
                        @Override
                        public void onSuccess(Bitmap bitmap, String bitmapURL) {
                            // *Save this image on cache:
                            registerResourceCacheCallback.register(bitmap, bitmapURL);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // *Do nothing
                        }
                    });
                }

                @Override
                public void onValidationRequested(final ValidateResourceCacheCallback<String> validateResourceCacheCallback) {
                    new PlusIOHandler(credential).getCoverURL(mainAccount.getUserId(), new PhotoURLCallback() {
                        @Override
                        public void onSuccess(String photoURL) {
                            validateResourceCacheCallback.validate(photoURL);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // TODO error
                        }
                    });
                }

                @Override
                public void onValidatedCache(Bitmap validatedResource) {
                    // *Apply the validated photo:
                    onFound(validatedResource);
                }


            });


            layoutInflater.inflate(R.layout.fragment_main_navigation_drawer_list, contentContainer);
            showingMainList = true;

            manageAccountsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manageAccountsBtn_onClick(view);
                }
            });

        } catch (NullPointerException e){
            e.printStackTrace();
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }


    public void manageAccountsBtn_onClick(View view){
        if(contentContainer == null || layoutInflater == null){
            return;
        }

        contentContainer.removeAllViews();

        if(showingMainList){
            layoutInflater.inflate(R.layout.fragment_main_navigation_drawer_manage_account, contentContainer);
        } else{
            layoutInflater.inflate(R.layout.fragment_main_navigation_drawer_list, contentContainer);
        }

        showingMainList = !showingMainList;
    }
}
