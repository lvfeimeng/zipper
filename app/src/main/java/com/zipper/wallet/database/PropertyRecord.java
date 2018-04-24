package com.zipper.wallet.database;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by AlMn on 2018/04/21.
 */

public class PropertyRecord extends DataSupport implements Serializable {

    /**
     * timestamp : 1524204129
     * hash : 0xca62017fbab765506880ab41a54af2e4c8c0ccea8eee21a6b52c41aed103db19
     * from : 0xea674fdde714fd979de3edf0f56aa9716b898ec8
     * to : 0x19776d3be75e08de504a7f5b4f7c02f90d210aea
     * value : 50072814947893670
     * fee : 0
     * height : 5472635
     */

    private long timestamp;
    private String hash;
    private String from;
    private String to;
    private String value;
    private String fee;
    private String height;

    private String name;
    private String unit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
