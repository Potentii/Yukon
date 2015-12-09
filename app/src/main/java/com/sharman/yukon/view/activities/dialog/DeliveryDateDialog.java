package com.sharman.yukon.view.activities.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import java.util.Date;

/**
 * Created by poten on 16/10/2015.
 */
public class DeliveryDateDialog extends DialogFragment implements Validatable {
    private Date date;
    private DialogCallback dialogCallback;

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
                    e.printStackTrace();
                }

            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try{
                    dialogCallback.onNegative();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });


        return builder.create();
    }


    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }


    @Override
    public boolean isValid() {
        return date != null;
    }
}
