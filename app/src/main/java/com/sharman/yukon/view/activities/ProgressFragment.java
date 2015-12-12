package com.sharman.yukon.view.activities;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharman.yukon.R;

import java.util.ArrayList;
import java.util.List;


public class ProgressFragment extends Fragment {
    private Activity activity;
    private TextView messageOut;
    private TextView messageDetailOut;

    private List<String> stackedMessageList = new ArrayList<>();
    private List<String> stackedDetailedMessageList = new ArrayList<>();
    private boolean instantiated = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        messageOut = (TextView) view.findViewById(R.id.messageOut);
        messageDetailOut = (TextView) view.findViewById(R.id.messageDetailOut);

        instantiated = true;

        for (String message : stackedMessageList) {
            setProgressMessage(message);
        }
        stackedMessageList.clear();

        for (String message : stackedDetailedMessageList) {
            setProgressDetailMessage(message);
        }
        stackedDetailedMessageList.clear();

        return view;
    }


    public void setProgressMessage(int messageId) {
        try {
            setProgressMessage(activity.getResources().getString(messageId));
        } catch (android.content.res.Resources.NotFoundException | NullPointerException e) {
            setProgressMessage("");
        }
    }

    public void setProgressMessage(final String message) {
        if(!instantiated){
            stackedMessageList.add(message);
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (messageOut != null) {
                    messageOut.setText(message);
                }

                if (messageDetailOut != null) {
                    messageDetailOut.setText("");
                }
            }
        });

    }



    public void setProgressDetailMessage(int messageId){
        try {
            setProgressDetailMessage(activity.getResources().getString(messageId));
        }catch (android.content.res.Resources.NotFoundException | NullPointerException e){
            setProgressDetailMessage("");
        }
    }

    public void setProgressDetailMessage(final String message) {
        if(!instantiated){
            stackedDetailedMessageList.add(message);
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (messageDetailOut != null) {
                    messageDetailOut.setText(message);
                }
            }
        });
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isInstantiated() {
        return instantiated;
    }
}
