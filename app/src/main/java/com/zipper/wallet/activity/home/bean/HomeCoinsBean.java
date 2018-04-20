package com.zipper.wallet.activity.home.bean;

import com.zipper.wallet.bean.CoinsBean;

import java.util.List;

/**
 * Created by AlMn on 2018/04/19.
 */

public class HomeCoinsBean {


    /**
     * data : [{"id":1,"type":0,"name":"btc","full_name":"bitcoin","addr_algorithm":"btc","addr_algorithm_param":"0x00","sign_algorithm":"btc","sing_algorithm_param":"","token_type":"","token_addr":"","decimals":10000000000,"is_default":true},{"id":2,"type":60,"name":"eth","full_name":"ethereum","addr_algorithm":"eth","addr_algorithm_param":"","sign_algorithm":"eth","sing_algorithm_param":"","token_type":"","token_addr":"","decimals":1000000000000000000,"is_default":true}]
     * errCode : 0
     * hash : 028700e89c2a55ac3a91e5ac172d0efc
     */

    private int errCode;
    private String hash;
    /**
     * id : 1
     * type : 0
     * name : btc
     * full_name : bitcoin
     * addr_algorithm : btc
     * addr_algorithm_param : 0x00
     * sign_algorithm : btc
     * sing_algorithm_param :
     * token_type :
     * token_addr :
     * decimals : 10000000000
     * is_default : true
     */

    private List<CoinsBean> data;

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

    public List<CoinsBean> getData() {
        return data;
    }

    public void setData(List<CoinsBean> data) {
        this.data = data;
    }

}
