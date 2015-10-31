package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


import com.sharman.yukon.R;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.StudentNameIdImageStruct;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by poten on 25/10/2015.
 */
public class StudentPickerDialog extends DialogFragment {
    // *Holds the student data (name, email, picture) to build the list
    private List<StudentNameIdImageStruct> studentNameIdImageStructList = new ArrayList<>();
    private List<String> idList;

    private DialogCallback dialogCallback;

    private LayoutInflater layoutInflater;
    private LinearLayout container;

    private ArrayAdapter<String> studentsArrayAdapter;
    private AutoCompleteTextView studentIn;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_student_picker, null);
        container = (LinearLayout) view.findViewById(R.id.rowContainer);
        studentIn = (AutoCompleteTextView) view.findViewById(R.id.studentIn);

        if(studentNameIdImageStructList.isEmpty()){
            studentNameIdImageStructList = queryContacts();
        }


        if(studentsArrayAdapter == null) {
            String[] emailArray = new String[studentNameIdImageStructList.size()];

            for(int i=0; i<emailArray.length; i++){
                emailArray[i] = studentNameIdImageStructList.get(i).getId();
            }

            studentsArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, emailArray);
        }


        studentIn.setAdapter(studentsArrayAdapter);


        studentIn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = studentIn.getText().toString();
                for(int j=0; j< studentNameIdImageStructList.size(); j++){
                    StudentNameIdImageStruct studentNameIdImageStruct = studentNameIdImageStructList.get(j);
                    if(studentNameIdImageStruct.getId().equals(text)){
                        addRow(studentNameIdImageStruct.getName(), studentNameIdImageStruct.getId(), studentNameIdImageStruct.getImageUri());
                        studentIn.setText("");
                    }
                }
            }
        });


        builder.setView(view);


        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogCallback.onPositive();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    dialogCallback.onNegative();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        return builder.create();
    }







    private void addRow(final String name, final String id, final String imageUri){
        final View row = layoutInflater.inflate(R.layout.dialog_student_picker_row, null);
        final ImageView studentImg = (ImageView) row.findViewById(R.id.studentImg);
        final TextView studentNameOut = (TextView) row.findViewById(R.id.studentNameOut);
        final TextView studentEmailOut = (TextView) row.findViewById(R.id.studentEmailOut);
        final ImageButton removeBtn = (ImageButton) row.findViewById(R.id.removeBtn);

        try {
            Bitmap bitmap = getBitmapFromURI(getActivity(), Uri.parse(imageUri));
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
            roundedBitmapDrawable.setAntiAlias(true);
            studentImg.setImageDrawable(roundedBitmapDrawable);
        } catch (Exception e){
            // Loading default contact picture
            Drawable drawable = getResources().getDrawable(R.drawable.default_person_rounded);
            studentImg.setImageDrawable(drawable);
        }

        studentNameOut.setText(name);
        studentEmailOut.setText(id);

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRow(row, id);
            }
        });

        container.addView(row);
        idList.add(id);
    }

    private void removeRow(View row, String id) {
        container.removeView(row);
        idList.remove(id);
    }



    public static Bitmap getBitmapFromURI(Context context, Uri uri) {
        InputStream input = null;

        try {
            input = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }




    private List<StudentNameIdImageStruct> queryContacts(){
        List<StudentNameIdImageStruct> contactList = new ArrayList<>();
        HashSet<String> emailHash = new HashSet<>();

        Context context = getActivity();
        ContentResolver cr = context.getContentResolver();

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
                    contactList.add(new StudentNameIdImageStruct(displayName, email, thumbUri));
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return contactList;
    }





    public List<String> getIdList() {
        return idList;
    }
    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }
}
