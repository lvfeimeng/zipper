package com.zipper.wallet.database;

import org.litepal.crud.DataSupport;

/**
 * Created by AlMn on 2018/04/20.
 */

public class CoinBalance extends DataSupport {


    /**
     * "address": "0x570d21bd8dd425093b803439625c26aa7a68e3eb",
     * "amount": 6000000000000000000,
     * "nonce": 0,
     * "tx_cnt": 2,
     * "gas_price": 20000000000
     */

    private String address;
    private String amount;
    private int tx_cnt;

    private String gas_price;
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getGas_price() {
        return gas_price;
    }

    public void setGas_price(String price) {
        this.gas_price = price;
    }

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
