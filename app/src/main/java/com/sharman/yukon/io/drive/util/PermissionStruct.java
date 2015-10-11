package com.sharman.yukon.io.drive.util;

/**
 * Created by poten on 09/10/2015.
 */
public class PermissionStruct {
    private String value;
    private String type;
    private String role;

    public PermissionStruct(String value, String type, String role) {
        this.value = value;
        this.type = type;
        this.role = role;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
