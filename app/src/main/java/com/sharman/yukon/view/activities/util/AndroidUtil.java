package com.sharman.yukon.view.activities.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.plus.model.Person;
import com.sharman.yukon.R;
import com.sharman.yukon.io.plus.PlusIOHandler;
import com.sharman.yukon.io.plus.callback.PersonImgReadCallback;
import com.sharman.yukon.io.plus.callback.PersonReadCallback;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by poten on 31/10/2015.
 */
public class AndroidUtil {
    private Activity activity;

    public AndroidUtil(Activity activity){
        this.activity = activity;
    }


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


    public void formatPersonImageView_AndroidContacts(@NonNull final ImageView imageView, @Nullable final String imageUri){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // *Try to get the image:
                    Bitmap bitmap = getBitmapFromURI(Uri.parse(imageUri));
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
        });
    }



    public void formatPersonImageView_GPlus(@NonNull final ImageView imageView, @NonNull GoogleAccountCredential credential, final String userId){
        final PlusIOHandler plusIOHandler = new PlusIOHandler(credential);
        plusIOHandler.readPerson(userId, new PersonReadCallback() {
            @Override
            public void onSuccess(Person person) {
                plusIOHandler.readPersonImg(person, new PersonImgReadCallback() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), bitmap);
                                roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
                                roundedBitmapDrawable.setAntiAlias(true);
                                imageView.setImageDrawable(roundedBitmapDrawable);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // *Loading default person picture:
                        Drawable drawable = activity.getResources().getDrawable(R.drawable.default_person_rounded);
                        imageView.setImageDrawable(drawable);
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                // *Loading default person picture:
                Drawable drawable = activity.getResources().getDrawable(R.drawable.default_person_rounded);
                imageView.setImageDrawable(drawable);
            }
        });
    }





    public void fillInfoPhotoToolbar_GPlusImage(@NonNull final View toolbar, @NonNull final GoogleAccountCredential credential, @NonNull final String userId, final String primaryInfoText, final String secondaryInfoText, final String tertiaryInfoText){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ImageView infoImg = (ImageView) toolbar.findViewById(R.id.infoImg);
                    formatPersonImageView_GPlus(infoImg, credential, userId);
                    fillInfoPhotoToolbar(toolbar, primaryInfoText, secondaryInfoText, tertiaryInfoText);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public void fillInfoPhotoToolbar_AndroidContactsImage(@NonNull final View toolbar, final String photoUri, final String primaryInfoText, final String secondaryInfoText, final String tertiaryInfoText){
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


    private void fillInfoPhotoToolbar(@NonNull View toolbar, String primaryInfoText, String secondaryInfoText, String tertiaryInfoText){
        final TextView primaryInfoOut     = (TextView)  toolbar.findViewById(R.id.primaryInfoOut);
        final TextView secondaryInfoOut   = (TextView)  toolbar.findViewById(R.id.secondaryInfoOut);
        final TextView tertiaryInfoOut    = (TextView)  toolbar.findViewById(R.id.tertiaryInfoOut);

        primaryInfoOut.setText(primaryInfoText);
        secondaryInfoOut.setText(secondaryInfoText);
        tertiaryInfoOut.setText(tertiaryInfoText);
    }



    public void writeToSharedPreferences(@NonNull String file, @NonNull String key, String value){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(file, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Nullable
    public String readFromSharedPreferences(@NonNull String file, @NonNull String key, @Nullable String defaultValue){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(file, activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}
