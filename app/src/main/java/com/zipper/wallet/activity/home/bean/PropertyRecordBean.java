package com.zipper.wallet.activity.home.bean;

import com.zipper.wallet.database.PropertyRecord;

import java.util.List;

/**
 * Created by AlMn on 2018/04/21.
 */

public class PropertyRecordBean {

    private int errCode;
    private String hash;

    private List<PropertyRecord> data;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<PropertyRecord> getData() {
        return data;
    }

    public void setData(List<PropertyRecord> data) {
        this.data = data;
    }
}
