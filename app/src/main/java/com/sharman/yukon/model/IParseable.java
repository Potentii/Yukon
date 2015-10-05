package com.sharman.yukon.model;

import org.json.JSONObject;

/**
 * Created by poten on 04/10/2015.
 */
public interface IParseable {
    public void parse(JSONObject jsonObject);
    public JSONObject parse();
}
