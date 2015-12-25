package com.sharman.yukon.view.activities.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.sharman.yukon.R;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PhotoURLCallback;
import com.sharman.yukon.view.activities.dialog.AlertDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by poten on 31/10/2015.
 */
public class AndroidUtil {
    private Activity activity;



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Constructor:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public AndroidUtil(Activity activity){
        this.activity = activity;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Query methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    /**
     * Queries for contacts of the user's phone.
     * @return A list of {@link StudentContact} containing the contacts' Name, E-mail, and Image URI
     */
    public List<StudentContact> queryContacts(){
        List<StudentContact> contactList = new ArrayList<>();
        HashSet<String> emailHash = new HashSet<>();

        ContentResolver cr = activity.getContentResolver();

        String[] PROJECTION = new String[] {
                ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                //ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Email.DATA
                //ContactsContract.CommonDataKinds.Photo.CONTACT_ID
        };

        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";

        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);

        if(cur.moveToFirst()) {
            do{
                String displayName = cur.getString(1);
                String thumbUri = cur.getString(2);
                String email = cur.getString(3);

                // *Only unique e-mails:
                if(emailHash.add(email.toLowerCase())) {
                    contactList.add(new StudentContact(displayName, email, thumbUri));
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return contactList;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Toast methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    /**
     * Displays a {@link android.widget.Toast} on the UI Thread.
     * @param text The text of the toast
     * @param duration The duration of the toast, it could be {@code Toast.LENGTH_SHORT} or {@code Toast.LENGTH_LONG}
     */
    public void showToast(final String text, final int duration){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, duration).show();
            }
        });
    }

    public void showToast(int textId, int duration){
        try {
            showToast(activity.getResources().getString(textId), duration);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * URI methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public Bitmap getBitmapFromURI(Uri uri) {
        InputStream input = null;

        try {
            input = activity.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Format person image methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    /**
     * Sets the {@code bitmap} to the given {@code imageView}, and format it on a circular shape.
     * @param imageView the {@link ImageView} that will be formatted.
     * @param bitmap the {@link Bitmap} that will be displayed, if it's null will be replaced with a default one.
     */
    public void formatPersonImageView(@Nonnull final ImageView imageView, @Nullable final Bitmap bitmap){
        try {
            // *Try to format the image:
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
            roundedBitmapDrawable.setAntiAlias(true);
            imageView.setImageDrawable(roundedBitmapDrawable);
        } catch (Exception e){
            // *Loading default person picture:
            Drawable drawable = activity.getResources().getDrawable(R.drawable.default_person_rounded);
            imageView.setImageDrawable(drawable);
        }
    }

    public void formatPersonImageView_AndroidContacts(@Nonnull final ImageView imageView, @Nullable final String imageUri){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // *Try to get the image:
                    Bitmap bitmap = getBitmapFromURI(Uri.parse(imageUri));
                    formatPersonImageView(imageView, bitmap);
                } catch (Exception e){
                    // *Loading default person picture:
                    formatPersonImageView(imageView, null);
                }
            }
        });
    }

    public void formatPersonImageView_GPlus(@Nonnull final ImageView imageView, @Nonnull final GoogleAccountCredential credential, @Nullable final String userId){
        if(userId == null || userId.trim().isEmpty()){
            // *Apply the default photo:
            formatPersonImageView(imageView, null);
            return;
        }

        ResourceCache resourceCache = new ResourceCache(activity);
        resourceCache.getResource_GPlusProfilePhoto(userId, new GetResourceCacheCallback<Bitmap, String>() {
            @Override
            public void onFound(final Bitmap resource) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        formatPersonImageView(imageView, resource);
                    }
                });
            }

            @Override
            public void onNotFound(final RegisterResourceCacheCallback<Bitmap, String> registerResourceCacheCallback) {

                new PlusIOHandler(credential).readPersonImg(userId, new PersonImgReadCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, String bitmapURL) {
                        // *Save this image on cache:
                        registerResourceCacheCallback.register(bitmap, bitmapURL);

                        // *Apply the found photo:
                        onFound(bitmap);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // *Apply the default photo:
                        onFound(null);
                    }
                });
            }

            @Override
            public void onValidationRequested(final ValidateResourceCacheCallback<String> validateResourceCacheCallback) {
                new PlusIOHandler(credential).getImgURL(userId, new PhotoURLCallback() {
                    @Override
                    public void onSuccess(String photoURL) {
                        validateResourceCacheCallback.validate(photoURL);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onValidatedCache(Bitmap validatedResource) {
                // *Apply the validated photo:
                onFound(validatedResource);
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Info photo toolbar methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void fillInfoPhotoToolbar_GPlusImage(@Nonnull final View toolbar, @Nonnull final GoogleAccountCredential credential, @Nonnull final String userId, final String primaryInfoText, final String secondaryInfoText, final String tertiaryInfoText){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ImageView infoImg = (ImageView) toolbar.findViewById(R.id.infoImg);
                    formatPersonImageView_GPlus(infoImg, credential, userId);
                    fillInfoPhotoToolbar(toolbar, primaryInfoText, secondaryInfoText, tertiaryInfoText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void fillInfoPhotoToolbar_AndroidContactsImage(@Nonnull final View toolbar, final String photoUri, final String primaryInfoText, final String secondaryInfoText, final String tertiaryInfoText){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ImageView infoImg = (ImageView) toolbar.findViewById(R.id.infoImg);
                    formatPersonImageView_AndroidContacts(infoImg, photoUri);
                    fillInfoPhotoToolbar(toolbar, primaryInfoText, secondaryInfoText, tertiaryInfoText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillInfoPhotoToolbar(@Nonnull View toolbar, String primaryInfoText, String secondaryInfoText, String tertiaryInfoText){
        final TextView primaryInfoOut     = (TextView)  toolbar.findViewById(R.id.primaryInfoOut);
        final TextView secondaryInfoOut   = (TextView)  toolbar.findViewById(R.id.secondaryInfoOut);
        final TextView tertiaryInfoOut    = (TextView)  toolbar.findViewById(R.id.tertiaryInfoOut);

        primaryInfoOut.setText(primaryInfoText);
        secondaryInfoOut.setText(secondaryInfoText);
        tertiaryInfoOut.setText(tertiaryInfoText);
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Alert dialog methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void showAlertDialog(@Nullable final Integer titleDrwId, @Nullable final Integer titleTxtId, @Nullable final Integer contentTxtId, @Nullable final Integer positiveBtnTxtId, @Nullable final Integer neutralBtnTxtId, @Nullable final Integer negativeBtnTxtId, @Nonnull final DialogCallback dialogCallback) {
        Drawable titleDrw = null;
        String titleTxt = null;
        String contentTxt = null;
        String positiveBtnTxt = null;
        String neutralBtnTxt = null;
        String negativeBtnTxt = null;

        if(titleDrwId != null) {
            titleDrw = activity.getResources().getDrawable(titleDrwId);
        }
        if(titleTxtId != null) {
            titleTxt = activity.getResources().getString(titleTxtId);
        }
        if(contentTxtId != null) {
            contentTxt = activity.getResources().getString(contentTxtId);
        }
        if(positiveBtnTxtId != null) {
            positiveBtnTxt = activity.getResources().getString(positiveBtnTxtId);
        }
        if(neutralBtnTxtId != null) {
            neutralBtnTxt = activity.getResources().getString(neutralBtnTxtId);
        }
        if(negativeBtnTxtId != null) {
            negativeBtnTxt = activity.getResources().getString(negativeBtnTxtId);
        }

        showAlertDialog(titleDrw, titleTxt, contentTxt, positiveBtnTxt, neutralBtnTxt, negativeBtnTxt, dialogCallback);
    }

    public void showAlertDialog(@Nullable final Drawable titleDrw, @Nullable final String titleTxt, @Nullable final String contentTxt, @Nullable final String positiveBtnTxt, @Nullable final String neutralBtnTxt, @Nullable final String negativeBtnTxt, @Nonnull final DialogCallback dialogCallback){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog();
                alertDialog.setTitleDrw(titleDrw);
                alertDialog.setTitleTxt(titleTxt);
                alertDialog.setContentTxt(contentTxt);
                alertDialog.setPositiveBtnTxt(positiveBtnTxt);
                alertDialog.setNeutralBtnTxt(neutralBtnTxt);
                alertDialog.setNegativeBtnTxt(negativeBtnTxt);
                alertDialog.setDialogCallback(dialogCallback);
                alertDialog.show(activity.getFragmentManager(), "alert_dialog");
            }
        });
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Shared preferences methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public void writeToSharedPreferences(@Nonnull String file, @Nonnull String key, String value){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(file, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Nullable
    public String readFromSharedPreferences(@Nonnull String file, @Nonnull String key, @Nullable String defaultValue){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(file, activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}
