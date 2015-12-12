package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.sharman.yukon.R;
import com.sharman.yukon.view.activities.util.DialogCallback;
import com.sharman.yukon.view.activities.util.Validatable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by poten on 16/10/2015.
 */
public class DeliveryDateDialog extends DialogFragment implements Validatable {
    private Date date;
    private DialogCallback dialogCallback;

    private Context context;

    private String invalidText = "";



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_delivery_date, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.deliveryDatePicker);

        builder.setView(view);

        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                try {
                    date = new SimpleDateFormat("dd/MM/yyyy").parse(day + "/" + month + "/" + year);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try{
                    dialogCallback.onPositive();
                }catch (NullPointerException e){
                    System.err.println("Forgot to set the DialogCallback");
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try{
                    dialogCallback.onNegative();
                }catch (NullPointerException e){
                    System.err.println("Forgot to set the DialogCallback");
                }
            }
        });


        return builder.create();
    }



    /*
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     *  * Getters and setters:
     *  * ========== * ========== * ========== * ========== * ========== * ========== * ========== * ========== *
     */
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
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
        if(date != null){
            Calendar currentDate = Calendar.getInstance();
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(date);


            long currentDateLong = (currentDate.get(Calendar.YEAR) * 10000) + (currentDate.get(Calendar.MONTH) * 100) + currentDate.get(Calendar.DAY_OF_MONTH);
            long selectedDateLong = (selectedDate.get(Calendar.YEAR) * 10000) + (selectedDate.get(Calendar.MONTH) * 100) + selectedDate.get(Calendar.DAY_OF_MONTH);;

            if(selectedDateLong < currentDateLong){
                try {
                    invalidText = context.getResources().getString(R.string.output_invalidField_datePicker_dateInPast);
                } catch (NullPointerException e){
                    e.printStackTrace();}

                return false;
            }
        } else {
            try {
                invalidText = context.getResources().getString(R.string.output_invalidField_datePicker_empty);
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
