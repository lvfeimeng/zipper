package com.zipper.wallet.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.SqliteUtils;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/10.
 */

public class WalletInfo extends DataSupport {


    @Column(unique = true, defaultValue = "unknown")
    private int id;

    private String  mnem_seed/**通过密码 和 初始生成助记词种子的加密而成*/,
            address/**跟地址*/,
            name/**钱包名称*/,tip/**密码提示*/,path/**分层path*/,esda_seed/**跟私钥密文*/;

    public void createTable(Context context){
        //CREATE TABLE IF NOT EXISTS coininfo (id INTEGER PRIMARY KEY NOT NULL ,type INTEGER NOT NULL,name VARCHAR(42) NOT NULL,full_name VARCHAR(420) NOT NULL,addr_algorithm VARCHAR(42) NOT NULL,addr_algorithm_param TEXT,sign_algorithm VARCHAR(42) NOT NULL,sing_algorithm_param TEXT,token_type VARCHAR(42),token_addr VARCHAR(42));

        SqliteUtils.execSQL("CREATE TABLE IF NOT EXISTS walletinfo (id INTEGER PRIMARY KEY NOT NULL," +
                "path VARCHAR(420) ,address VARCHAR(420) NOT NULL ,name VARCHAR(42) NOT NULL ,tip varchar(42)," +
                "mnem_seed VARCHAR(420) NOT NULL,esda_seed varchar(420) );");
        MyLog.i("WalletInfo","createTable 表创建成功");
    }

    public WalletInfo(Context context){
        createTable(context);
    }

    public WalletInfo( Map map){

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


    public Map toMap(){
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
    public String toString() {
        return "WalletInfo{" +
                "id=" + id +
                ", mnem_seed='" + mnem_seed + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", tip='" + tip + '\'' +
                ", path='" + path + '\'' +
                ", esda_seed='" + esda_seed + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMnem_seed() {
        return mnem_seed;
    }

    public void setMnem_seed(String mnem_seed) {
        this.mnem_seed = mnem_seed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEsda_seed() {
        return esda_seed;
    }

    public void setEsda_seed(String esda_seed) {
        this.esda_seed = esda_seed;
    }
}
