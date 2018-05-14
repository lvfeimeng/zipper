package com.zipper.wallet.database;

import org.litepal.crud.DataSupport;

public class BtcUtxo extends DataSupport {


    /**
     * hash : 168e13667b50bd77fd6f0bb68f9ce5eda467de143a513b6dde7c7b9ce2330272
     * n : 0
     * value : 5000000000
     * script : pubkeyhash
     */
    private int id;
    private String hash;
    private int n;
    private String value;
    private String script;

    private int coin_id;
    private String addr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
