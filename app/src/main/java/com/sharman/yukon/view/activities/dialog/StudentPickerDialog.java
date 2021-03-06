package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


import com.sharman.yukon.R;
import com.sharman.yukon.view.activities.util.AndroidUtil;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.StudentContact;
import com.sharman.yukon.view.activities.util.Validatable;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by poten on 25/10/2015.
 */
public class StudentPickerDialog extends DialogFragment implements Validatable{
    // *Holds the student data (name, email, picture) to build the list
    private List<StudentContact> studentContactList = new ArrayList<>();
    private List<String> idList;

    private DialogCallback dialogCallback;

    private LayoutInflater layoutInflater;
    private LinearLayout container;

    private ArrayAdapter<String> studentsArrayAdapter;
    private AutoCompleteTextView studentIn;

    private Context context;

    private String invalidText = "";



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_student_picker, null);
        container = (LinearLayout) view.findViewById(R.id.rowContainer);
        studentIn = (AutoCompleteTextView) view.findViewById(R.id.studentIn);



        // *If the list of contacts is empty, retrieve the user phone contact list
        if(studentContactList.isEmpty()){
            studentContactList = new AndroidUtil(getActivity()).queryContacts();
        }



        // *If the idList isn't null:
        if(idList != null){
            // *The idList may contain ids which are not on the contacts list

            container.removeAllViews();

            // *Searching for the ids on the user's contact list:
            for(String id : idList){
                StudentContact studentContactFound = null;
                for(StudentContact studentContact : studentContactList){
                    if(id.equals(studentContact.getId())){
                        studentContactFound = studentContact;
                        break;
                    }
                }


                // *Add the row on the container:
                if(studentContactFound != null){
                    addRow(studentContactFound);
                } else{
                    addRow(id);
                }
            }
        }



        // *ArrayApater was not set yet?
        if(studentsArrayAdapter == null) {
            String[] emailArray = new String[studentContactList.size()];

            for(int i=0; i<emailArray.length; i++){
                emailArray[i] = studentContactList.get(i).getId();
            }

            studentsArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, emailArray);
        }


        studentIn.setAdapter(studentsArrayAdapter);


        // *When the user select an item from the dropdown list:
        studentIn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = studentIn.getText().toString();
                if(idList != null){
                    for(int j = 0; j < idList.size(); j++){
                        // *If the contact is already selected:
                        if(text.equals(idList.get(j))){
                            // TODO tell the user that this contact is already selected
                            studentIn.setText("");
                            return;
                        }
                    }
                }

                for(int j = 0; j < studentContactList.size(); j++) {
                    StudentContact studentContact = studentContactList.get(j);
                    if (text.equals(studentContact.getId())) {
                        addRow(studentContact);
                        idList.add(studentContact.getId());
                        studentIn.setText("");
                        break;
                    }
                }
            }
        });



        // *When the user hit the DONE button on keyboard:
        studentIn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // *The user finished to edit the text:
                    String text = studentIn.getText().toString();
                    StudentContact studentContact = new StudentContact(text, text, null);

                    addRow(studentContact);
                    idList.add(studentContact.getId());
                    studentIn.setText("");
                    return true;
                }
                return false;
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





    private void addRow(final String studentId){
        addRow(new StudentContact(studentId, studentId, null));
    }
    private void addRow(final StudentContact studentContact){
        final View row = layoutInflater.inflate(R.layout.dialog_student_picker_row, null);
        final ImageView studentImg = (ImageView) row.findViewById(R.id.studentImg);
        final TextView studentNameOut = (TextView) row.findViewById(R.id.studentNameOut);
        final TextView studentEmailOut = (TextView) row.findViewById(R.id.studentEmailOut);
        final ImageButton removeBtn = (ImageButton) row.findViewById(R.id.removeBtn);

        new AndroidUtil(getActivity()).formatPersonImageView_AndroidContacts(studentImg, studentContact.getImageUri());

        studentNameOut.setText(studentContact.getName());
        studentEmailOut.setText(studentContact.getId());

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRow(row);
                idList.remove(studentContact.getId());
            }
        });

        container.addView(row);
    }

    private void removeRow(View row) {
        container.removeView(row);
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public List<StudentContact> getStudentContactList() {
        return studentContactList;
    }
    public void setStudentContactList(List<StudentContact> studentContactList) {
        this.studentContactList = studentContactList;
    }

    public List<String> getIdList() {
        return idList;
    }
    public void setIdList(List<String> idList) {
        this.idList = new ArrayList<>(idList);
    }

    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public void setContext(Context context) {
        this.context = context;
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Validatable methods:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    @Override
    public boolean isValid() {
        if(idList == null || idList.size()==0){
            try {
                invalidText = context.getResources().getString(R.string.output_invalidField_studentPicker_empty);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public String getInvalidText() {
        return invalidText;
    }
}
