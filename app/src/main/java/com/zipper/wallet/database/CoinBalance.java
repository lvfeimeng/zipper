package com.zipper.wallet.database;

import org.litepal.crud.DataSupport;

/**
 * Created by AlMn on 2018/04/20.
 */

public class CoinBalance extends DataSupport {


    /**
     * address : 1JBV1C2ifgFjFjEuzAQVj88y3UtKRYRfWU
     * amount : 5000000000
     * tx_cnt : 1
     */

    private String address;
    private String amount;
    private int tx_cnt;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getTx_cnt() {
        return tx_cnt;
    }

    public void setTx_cnt(int tx_cnt) {
        this.tx_cnt = tx_cnt;
    }
}
