package com.sharman.yukon.view.activities.util;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by poten on 06/12/2015.
 */
public class CompoundButtonController {
    private List<CompoundButton> compoundButtonList;

    public CompoundButtonController(){
        compoundButtonList = new ArrayList<>();
    }

    private void onSomeButtonClicked(CompoundButton compoundButton){
        for (CompoundButton button : compoundButtonList) {
            if(!compoundButton.equals(button)) {
                button.setChecked(false);
            }
        }
    }

    public void addCompoundButton(@Nonnull CompoundButton compoundButton){
        if(compoundButton instanceof RadioButton){
            compoundButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        onSomeButtonClicked((CompoundButton) view);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        compoundButtonList.add(compoundButton);
    }

    public void removeCompoundButton(@Nonnull CompoundButton compoundButton){
        compoundButton.setOnClickListener(null);
        compoundButtonList.remove(compoundButton);
    }

}
