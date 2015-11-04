package com.sharman.yukon.io.plus.callback;

import com.google.api.services.plus.model.Person;

import java.util.List;

/**
 * Created by poten on 26/10/2015.
 */
public interface PersonContactsListCallback {
    public void onResult(List<Person> personList);
}
