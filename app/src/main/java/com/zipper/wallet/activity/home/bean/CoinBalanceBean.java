package com.zipper.wallet.activity.home.bean;

import com.zipper.wallet.database.CoinBalance;

/**
 * Created by AlMn on 2018/04/20.
 */

public class CoinBalanceBean {

    private int errCode;
    private String hash;

    private CoinBalance data;

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

    public CoinBalance getData() {
        return data;
    }

    public void setData(CoinBalance data) {
        this.data = data;
    }
}
