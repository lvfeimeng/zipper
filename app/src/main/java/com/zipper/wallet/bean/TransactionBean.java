package com.zipper.wallet.bean;

/**
 * Created by admin on 2018/4/10.
 */

public class TransactionBean {

    private String currency;
    private String date;
    private String confirmNum;
    private String formPrice;

    public TransactionBean(String currency, String date, String confirmNum, String formPrice) {
        this.currency = currency;
        this.date = date;
        this.confirmNum = confirmNum;
        this.formPrice = formPrice;
    }

    public TransactionBean() {

    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getConfirmNum() {
        return confirmNum;
    }

    public void setConfirmNum(String confirmNum) {
        this.confirmNum = confirmNum;
    }

    public String getFormPrice() {
        return formPrice;
    }

    public void setFormPrice(String formPrice) {
        this.formPrice = formPrice;
    }
}
