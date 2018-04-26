package com.zipper.wallet.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9.
 */

public class WalletBean {

    private static int id;

    private static String  mnem_seed/**通过密码 和 初始生成助记词种子的加密而成*/,
            address/**跟地址*/,
            name/**钱包名称*/,tip/**密码提示*/,path/**分层path*/,esda_seed/**跟私钥密文*/;
    static WalletBean wallet;

    private WalletBean(){

    }

    public static boolean setWalletBean(){
        if(wallet == null){
            wallet = new WalletBean();
            return  true;
        }
        return  false;
    }

    public static boolean setWalletBean(Map map){
        if(wallet == null){
            wallet = new WalletBean(map);
            return  true;
        }
        return  false;
    }


    private WalletBean( Map map){

        try {
            setId(Integer.parseInt(map.get("id")+""));
            setAddress(map.get("address")+"");
            setName(map.get("name")+"");
            setTip(map.get("tip")+"");
            setMnem_seed(map.get("mnem_seed")+"");
            setEsda_seed(map.get("esda_seed")+"");
            setPath(map.get("path")+"");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static Map toMap(){
        Map map = new HashMap();
        map.put("id",getId());
        map.put("name",getName());
        map.put("address",getAddress());
        map.put("tip",getTip());
        map.put("mnem_seed",getMnem_seed());
        map.put("esda_seed",getEsda_seed());
        map.put("path",getPath());
        return map;
    }

    @Override
    public  String toString() {
        return "WalletInfo{" +
                "id=" + getId() +
                ", mnem_seed='" + getMnem_seed() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", name='" + getName() + '\'' +
                ", tip='" + getTip() + '\'' +
                ", path='" + getPath() + '\'' +
                ", esda_seed='" + getEsda_seed() + '\'' +
                '}';
    }

    public static int getId() {
        return wallet.id;
    }

    public static void setId(int id) {
        wallet.id = id;
    }

    public static String getMnem_seed() {
        return mnem_seed;
    }

    public static void setMnem_seed(String mnem_seed) {
        wallet.mnem_seed = mnem_seed;
    }

    public static String getAddress() {
        return wallet.address;
    }

    public static void setAddress(String address) {
        wallet.address = address;
    }

    public static String getName() {
        return wallet.name;
    }

    public static void setName(String name) {
        wallet.name = name;
    }

    public static String getTip() {
        return wallet.tip;
    }

    public static void setTip(String tip) {
        wallet.tip = tip;
    }

    public static String getPath() {
        return wallet.path;
    }

    public static void setPath(String path) {
        wallet.path = path;
    }

    public static String getEsda_seed() {
        return wallet.esda_seed;
    }

    public static void setEsda_seed(String esda_seed) {
        wallet.esda_seed = esda_seed;
    }
}
