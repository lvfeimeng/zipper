package com.zipper.wallet.bean;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WalletBean {

    String name,pwd,pwdTip,randomSeed,mnemSeed,priKey,pubKey;
    static WalletBean wallet;

    private WalletBean(){

    }

    public static WalletBean getWalletBean(){
        if(wallet == null){
            wallet = new WalletBean();
        }
        return  wallet;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwdTip() {
        return pwdTip;
    }

    public void setPwdTip(String pwdTip) {
        this.pwdTip = pwdTip;
    }

    public String getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(String randomSeed) {
        this.randomSeed = randomSeed;
    }

    public String getMnemSeed() {
        return mnemSeed;
    }

    public void setMnemSeed(String mnemSeed) {
        this.mnemSeed = mnemSeed;
    }

    public String getPriKey() {
        return priKey;
    }

    public void setPriKey(String priKey) {
        this.priKey = priKey;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }
}
