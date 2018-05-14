package com.zipper.wallet.bean;

public class PayAddressBean {


    /**
     * address : 0x1111111111222222222233333333334444444444
     * value : 4000000000000000000
     */

    private String address;
    private String value;
    private String decimals;
    private String name;

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
