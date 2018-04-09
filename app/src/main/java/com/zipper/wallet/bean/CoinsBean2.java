package com.zipper.wallet.bean;

public class CoinsBean2 {

    private String id;
    private String shortName;
    private String count;

    public CoinsBean2() {
    }

    public CoinsBean2(String id, String shortName, String count) {
        this.id = id;
        this.shortName = shortName;
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


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
