package com.zipper.wallet.database;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/11.
 */

public class CoinInfo extends DataSupport implements Serializable {
    @Column(ignore = true)
    public boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

//    @Column(unique = true, defaultValue = "unknown")
//    private int id;

    private boolean is_default;

    public boolean isDefault() {
        return is_default;
    }

    public void setDefault(boolean is_default) {
        this.is_default = is_default;
    }

    @SerializedName("id")
    private int coin_id;

    private int type;
    private String name, full_name, addr_algorithm, sign_algorithm, token_type, token_addr, addr;

    private String addr_public, addr_script, sign_fork;

    private String decimals;
    private String icon;
    private String amount;
    private String gas_price;//矿工基本费用=gas_price*gas_limit
    private String nonce;

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getGas_price() {
        return gas_price;
    }

    public void setGas_price(String gas_price) {
        this.gas_price = gas_price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public CoinInfo() {
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public CoinInfo(Map map) {

        try {
            setCoin_id(Integer.parseInt(map.get("coin_id") + ""));
            setType(Integer.parseInt(map.get("type") + ""));
            setName(map.get("name") + "");
            setFull_name(map.get("full_name") + "");
            setAddr_algorithm(map.get("addr_algorithm") + "");
            setSign_algorithm(map.get("sign_algorithm") + "");
            setToken_type(map.get("token_type") + "");
            setToken_addr(map.get("token_addr") + "");
            setAddr(map.get("addr") + "");
            setDecimals(map.get("decimals") + "");
            setIcon(map.get("icon") + "");
            setAmount(map.get("amount") + "");
            setAddr_public(map.get("addr_public") + "");
            setAddr_script(map.get("addr_script") + "");
            setSign_fork(map.get("sign_fork") + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map toMap() {
        Map map = new HashMap();
        map.put("coin_id", getCoin_id());
        map.put("type", getType());
        map.put("name", getName());
        map.put("full_name", getFull_name());
        map.put("addr_algorithm", getAddr_algorithm());
        map.put("sign_algorithm", getSign_algorithm());
        map.put("token_type", getToken_type());
        map.put("token_addr", getToken_addr());
        map.put("addr", getAddr());
        map.put("decimals", getDecimals());
        map.put("icon", getIcon());
        map.put("amount", getAmount());
        map.put("addr_public", getAddr_public());
        map.put("addr_script", getAddr_script());
        map.put("sign_fork", getSign_fork());
        return map;
    }

    public String getAddr_public() {
        return addr_public;
    }

    public void setAddr_public(String addr_public) {
        this.addr_public = addr_public;
    }

    public String getAddr_script() {
        return addr_script;
    }

    public void setAddr_script(String addr_script) {
        this.addr_script = addr_script;
    }

    public String getSign_fork() {
        return sign_fork;
    }

    public void setSign_fork(String sign_fork) {
        this.sign_fork = sign_fork;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getAddr_algorithm() {
        return addr_algorithm;
    }

    public void setAddr_algorithm(String addr_algorithm) {
        this.addr_algorithm = addr_algorithm;
    }

    public String getSign_algorithm() {
        return sign_algorithm;
    }

    public void setSign_algorithm(String sign_algorithm) {
        this.sign_algorithm = sign_algorithm;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getToken_addr() {
        return token_addr;
    }

    public void setToken_addr(String token_addr) {
        this.token_addr = token_addr;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }
}
