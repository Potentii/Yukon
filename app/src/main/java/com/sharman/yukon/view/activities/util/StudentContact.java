package com.sharman.yukon.view.activities.util;

/**
 * Created by poten on 25/10/2015.
 */
public class StudentContact {
    private String name;
    private String id;
    private String imageUri;

    public StudentContact(String name, String id, String imageUri) {
        this.name = name;
        this.id = id;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
