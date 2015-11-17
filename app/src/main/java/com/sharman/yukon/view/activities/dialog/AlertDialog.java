package com.sharman.yukon.view.activities.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharman.yukon.R;
import com.sharman.yukon.view.activities.util.DialogCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by poten on 15/11/2015.
 */
public class AlertDialog extends DialogFragment {
    private DialogCallback dialogCallback;

    @Nullable
    private String titleTxt;
    @Nullable
    private Drawable titleDrw;
    private String contentTxt;

    @Nullable
    private String positiveBtnTxt;
    @Nullable
    private String negativeBtnTxt;
    @Nullable
    private String neutralBtnTxt;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.dialog_alert, null);

        TextView titleOut = (TextView) view.findViewById(R.id.titleOut);
        TextView contentOut = (TextView) view.findViewById(R.id.contentOut);
        ImageView titleImg = (ImageView) view.findViewById(R.id.titleImg);

        if(titleTxt != null){
            titleOut.setText(titleTxt);
        }

        if(titleDrw != null){
            titleImg.setImageDrawable(titleDrw);
        }

        if(contentTxt != null){
            contentOut.setText(contentTxt);
        }


        builder.setView(view);



        if(positiveBtnTxt != null){
            builder.setPositiveButton(positiveBtnTxt, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try{
                        dialogCallback.onPositive();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }
            });
        }



        if(negativeBtnTxt != null){
            builder.setNegativeButton(negativeBtnTxt, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try{
                        dialogCallback.onNegative();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });
        }



        if(neutralBtnTxt != null){
            builder.setNeutralButton(neutralBtnTxt, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        dialogCallback.onNeutral();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }



        return builder.create();
    }





    public DialogCallback getDialogCallback() {
        return dialogCallback;
    }
    public void setDialogCallback(@Nonnull DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @Nullable
    public String getTitleTxt() {
        return titleTxt;
    }
    public void setTitleTxt(@Nullable String titleTxt) {
        this.titleTxt = titleTxt;
    }

    @Nullable
    public Drawable getTitleDrw() {
        return titleDrw;
    }
    public void setTitleDrw(@Nullable Drawable titleDrw) {
        this.titleDrw = titleDrw;
    }

    @Nonnull
    public String getContentTxt() {
        return contentTxt;
    }
    public void setContentTxt(@Nonnull String contentTxt) {
        this.contentTxt = contentTxt;
    }

    @Nullable
    public String getPositiveBtnTxt() {
        return positiveBtnTxt;
    }
    public void setPositiveBtnTxt(@Nullable String positiveBtnTxt) {
        this.positiveBtnTxt = positiveBtnTxt;
    }

    @Nullable
    public String getNegativeBtnTxt() {
        return negativeBtnTxt;
    }
    public void setNegativeBtnTxt(@Nullable String negativeBtnTxt) {
        this.negativeBtnTxt = negativeBtnTxt;
    }

    @Nullable
    public String getNeutralBtnTxt() {
        return neutralBtnTxt;
    }
    public void setNeutralBtnTxt(@Nullable String neutralBtnTxt) {
        this.neutralBtnTxt = neutralBtnTxt;
    }
}
