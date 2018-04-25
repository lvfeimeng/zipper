package com.zipper.wallet.database;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by admin on 2018/4/23.
 */

public class ContactDetailsBean extends DataSupport implements Serializable{

    private int id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String remarks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ContactDetailsBean(int id, String name, String address, String phone, String email, String remarks) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.remarks = remarks;
    }

    public ContactDetailsBean() {
    }
}
