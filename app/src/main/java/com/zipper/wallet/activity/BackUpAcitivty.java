package com.zipper.wallet.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.base.CreateActvity;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.RuntListSeria;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.core.AbstractHD;
import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.utils.Utils;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class BackUpAcitivty extends CreateActvity {

    Button btnBackup;

    final int SAVE_PRIVATE = 100;
    final int TRANSMIT_WORDS = 101;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SAVE_PRIVATE:
                    break;

                case TRANSMIT_WORDS:
                    Object obj = msg.obj;
                    if(obj!=null){
                        if(obj instanceof  List){
                            hideProgressDialog();
                            Intent intent = new Intent(mContext,MnemonicActivity.class);
                            intent.putExtra("list",new RuntListSeria<String>((List<String>) obj));
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        toast("未曾生成数据");
                    }

                    break;

            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_backup);
        super.onCreate(savedInstanceState);

        btnBackup = (Button)findViewById(R.id.btn_backup);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        },5000);

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("请输入密码","", InputType.TYPE_TEXT_VARIATION_PASSWORD,new RuntHTTPApi.ResPonse(){

                    @Override
                    public void doSuccessThing(final Map<String, Object> param) {
                        String pwd = PreferencesUtils.getString(mContext,KEY_WALLET_PWD,PreferencesUtils.VISITOR);
                        if(pwd.equals(param.get(INPUT_TEXT).toString().trim())){
                            alertDialog.dismiss();
                            showProgressDialog("正在导出。。。");
                            new Thread(){
                                @Override
                                public void run() {
                                    createAccount();
                                }
                            }.start();

                        }else{
                            toast("密码错误");
                        }
                    }

                    @Override
                    public void doErrorThing(Map<String, Object> param) {

                    }
                });
            }
        });
    }


    private void createAccount(){

        try {

            CreateAcountUtils.instance(mContext);//首先实例化助记词类的单例模式

            byte[] randomSeed = CreateAcountUtils.createRandomSeed();//生成128位字节流


            List<String> words = CreateAcountUtils.getMnemonicCode(randomSeed);//一局随机数获取助记词

            byte[] seed = CreateAcountUtils.createMnemSeed(words);//由助记词和密码生成种子,方法内含有转换512哈系数方式


            DeterministicKey master = CreateAcountUtils.CreateRootKey(seed);//生成根公私钥对象
            //DeterministicKey accountKey = CreateAcountUtils.getAccount(master);


            String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
            String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
            String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥


            SQLiteDatabase sqlDB = mContext.openOrCreateDatabase("zipper.db", Context.MODE_PRIVATE,null);
            sqlDB.execSQL("CREATE TABLE IF NOT EXISTS coininfo (id INTEGER PRIMARY KEY NOT NULL ,type INTEGER NOT NULL,name VARCHAR(42) NOT NULL,full_name VARCHAR(420) NOT NULL,addr_algorithm VARCHAR(42) NOT NULL,addr_algorithm_param TEXT,sign_algorithm VARCHAR(42) NOT NULL,sign_algorithm_param TEXT,token_type VARCHAR(42),token_addr VARCHAR(42),addr VARCHAR(42));");
            //sqlDB.execSQL("drop table coininfo");
            SqliteUtils.openDataBase(mContext);
            RuntHTTPApi.toReApi(RuntHTTPApi.URL_GET_COINS,new HashMap<>(),new RuntHTTPApi.MyStringCallBack(mContext,new RuntHTTPApi.ResPonse() {
                @Override
                public void doSuccessThing(Map<String, Object> param) {
                    SQLiteDatabase db = LitePal.getDatabase();
                    RuntHTTPApi.printMap(param,"");
                    Log.i("StartActivity",(param.get("data") instanceof Collection)+"");
                    if(param.get("data") instanceof Collection){
                        for(Map map :(List<Map>)param.get("data") ){
                            CoinInfo coinInfo = new CoinInfo(map);
                            Log.i(TAG,coinInfo.getName()+"信息正在保存");
                            if(coinInfo.getName().equals("BTC")){
                               String addr =  CreateAcountUtils.getAccount(master,coinInfo.getType()).toAddress();
                                Log.i(TAG,"addr:"+addr);
                                coinInfo.setAddr(addr);
                            }else{
                                String addr =  CreateAcountUtils.getWalletAddr(master,coinInfo.getType());
                                Log.i(TAG,"addr:"+addr);
                                coinInfo.setAddr(addr);
                            }
                            coinInfo.save();
                            Log.i(TAG,"信息保存成功");

                        }
                        List<CoinInfo> coins = DataSupport.findAll(CoinInfo.class);

                        String firstAddr = CreateAcountUtils.getAddress(CreateAcountUtils.getAccount(master,60).deriveSoftened(AbstractHD.PathType.EXTERNAL_ROOT_PATH.getValue()),coins.get(0).getId());

                        //Log.i(TAG,"randomSeed :"+Utils.bytesToHexString(randomSeed));
                        for(String str : words){
                            //Log.i(TAG,"words :"+str);
                        }
                        //Log.i(TAG,"mnemonicSeed :"+mnemonicSeed);
                        //Log.i(TAG,"512PrivateKey:"+priKey);
                        ///Log.i(TAG,"512publicKey:"+pubkey);
                        //Log.i(TAG,"firstAddr:"+firstAddr);
                        WalletInfo walletInfo = new WalletInfo(mContext);
                        walletInfo.setName(PreferencesUtils.getString(mContext, BaseActivity.KEY_WALLET_NAME,PreferencesUtils.VISITOR));
                        walletInfo.setTip(PreferencesUtils.getString(mContext,BaseActivity.KEY_WALLET_PWD_TIP,PreferencesUtils.VISITOR));
                        walletInfo.setEsda_seed(new EncryptedData(Utils.hexStringToByteArray(priKey),
                                PreferencesUtils.getString(mContext,BaseActivity.KEY_WALLET_PWD,PreferencesUtils.VISITOR),
                                false)
                                .toEncryptedString());
                        walletInfo.setMnem_seed( new EncryptedData(Utils.hexStringToByteArray(Utils.bytesToHexString(randomSeed)),
                                PreferencesUtils.getString(mContext,BaseActivity.KEY_WALLET_PWD,PreferencesUtils.VISITOR),
                                false)
                                .toEncryptedString());
                        walletInfo.setAddress(firstAddr);
                        //walletInfo.setId(4);

                        ContentValues cValue = new ContentValues();
                        for(Object key : walletInfo.toMap().keySet()){
                            cValue.put(key.toString(), walletInfo.toMap().get(key)+"");
                        }

                        db.insert("walletinfo",null,cValue);
                        Log.i(TAG,"钱包数据保存成功");

                        //Log.i(TAG,"publicKey:"+Utils.bytesToHexString(keys.get(1)));
                        //Log.i(TAG,"512publicKey:"+Utils.bytesToHexString(keys512.get(1)));
                        Message msg = new Message();
                        msg.what = TRANSMIT_WORDS;
                        msg.obj = words;
                        mHandler.sendMessage(msg);
                    }
                }

                @Override
                public void doErrorThing(Map<String, Object> param) {

                }
            }));
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e+"");
        }

    }



}
