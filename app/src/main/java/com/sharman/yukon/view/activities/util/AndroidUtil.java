package com.sharman.yukon.view.activities.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;
import android.widget.Toast;

import com.sharman.yukon.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
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


    public void formatContactImageView(final ImageView imageView, final String imageUri){
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
                    // *Loading default contact picture:
                    Drawable drawable = activity.getResources().getDrawable(R.drawable.default_person_rounded);
                    imageView.setImageDrawable(drawable);
                }
            }
        });
    }

}
