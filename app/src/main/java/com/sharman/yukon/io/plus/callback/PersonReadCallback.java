package com.sharman.yukon.io.plus.callback;

import com.google.api.services.plus.model.Person;

/**
 * Created by poten on 12/10/2015.
 */
public interface PersonReadCallback {
    public void onSuccess(Person person);
    public void onFailure(String errorMessage);
}
