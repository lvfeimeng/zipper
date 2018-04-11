package com.zipper.wallet.database;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/11.
 */

public class CoinInfo extends DataSupport {



    @Column(unique = true, defaultValue = "unknown")
    private int id;

    private int type;
    private String name,full_name,addr_algorithm,addr_algorithm_param,sign_algorithm,sign_algorithm_param,token_type,token_addr;

    public CoinInfo() {
    }

    public CoinInfo(Map map){

        try {
            setId(Integer.parseInt(map.get("id")+""));
            setType(Integer.parseInt(map.get("type")+""));
            setName(map.get("name")+"");
            setFull_name(map.get("full_name")+"");
            setAddr_algorithm(map.get("addr_algorithm")+"");
            setAddr_algorithm_param(map.get("addr_algorithm_param")+"");
            setSign_algorithm(map.get("sign_algorithm")+"");
            setSign_algorithm_param(map.get("sign_algorithm_param")+"");
            setToken_type(map.get("token_type")+"");
            setToken_addr(map.get("token_addr")+"");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Map toMap(){
        Map map = new HashMap();
        map.put("id",getId());
        map.put("type",getType());
        map.put("name",getName());
        map.put("full_name",getFull_name());
        map.put("addr_algorithm",getAddr_algorithm());
        map.put("addr_algorithm_param",getAddr_algorithm_param());
        map.put("sign_algorithm",getSign_algorithm());
        map.put("sign_algorithm_param",getSign_algorithm_param());
        map.put("token_type",getToken_type());
        map.put("token_addr",getToken_addr());
        return map;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    public String getAddr_algorithm_param() {
        return addr_algorithm_param;
    }

    public void setAddr_algorithm_param(String addr_algorithm_param) {
        this.addr_algorithm_param = addr_algorithm_param;
    }

    public String getSign_algorithm() {
        return sign_algorithm;
    }

    public void setSign_algorithm(String sign_algorithm) {
        this.sign_algorithm = sign_algorithm;
    }

    public String getSign_algorithm_param() {
        return sign_algorithm_param;
    }

    public void setSign_algorithm_param(String sign_algorithm_param) {
        this.sign_algorithm_param = sign_algorithm_param;
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
}
