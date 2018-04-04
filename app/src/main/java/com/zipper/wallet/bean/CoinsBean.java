package com.zipper.wallet.bean;

public class CoinsBean {

    private String id;
    private String shortName;
    private String fullName;
    private String icon;
    private String count;

    public CoinsBean() {
    }

    public CoinsBean(String id, String shortName, String fullName, String icon, String count) {
        this.id = id;
        this.shortName = shortName;
        this.fullName = fullName;
        this.icon = icon;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
