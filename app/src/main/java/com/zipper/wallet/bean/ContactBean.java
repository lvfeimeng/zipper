package com.zipper.wallet.bean;

public class ContactBean {

    private String photo;
    private String name;
    private String key;

    public ContactBean() {
    }

    public ContactBean(String photo, String name, String key) {
        this.photo = photo;
        this.name = name;
        this.key = key;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
