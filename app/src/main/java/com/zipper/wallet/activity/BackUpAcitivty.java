package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.CreateActvity;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.RuntListSeria;

import net.bither.bitherj.core.AbstractHD;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class BackUpAcitivty extends CreateActvity {

    public static ArrayList<String> wordList = new ArrayList<String>(2048);
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
                showInputDialog("请输入密码","","", InputType.TYPE_TEXT_VARIATION_PASSWORD,new RuntHTTPApi.ResPonse(){

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
        try {

            InputStream stream = mContext.getAssets().open("english.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            this.wordList = new ArrayList<String>(2048);
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);        // Can't happen.
            }
            String word;
            while ((word = br.readLine()) != null) {
                md.update(word.getBytes());
                this.wordList.add(word);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void createAccount(){

        try {

            CreateAcountUtils.instance(mContext);//首先实例化助记词类的单例模式

            byte[] randomSeed = CreateAcountUtils.createRandomSeed();//生成128位字节流


            List<String> words = CreateAcountUtils.getMnemonicCode(randomSeed);//一局随机数获取助记词

            byte[] seed = CreateAcountUtils.createMnemSeed(words);//由助记词和密码生成种子,方法内含有转换512哈系数方式


            DeterministicKey master = CreateAcountUtils.CreateRootKey(seed);//生成根公私钥对象
            DeterministicKey accountKey = CreateAcountUtils.getAccount(master);


            String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
            String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
            String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥

            String firstAddr = CreateAcountUtils.getFirstAddress(CreateAcountUtils.getAccount(master).deriveSoftened(AbstractHD.PathType.EXTERNAL_ROOT_PATH.getValue()));

            WalletBean.getWalletBean().setMnemSeed(mnemonicSeed);
            WalletBean.getWalletBean().setRandomSeed(Utils.bytesToHexString(randomSeed));
            WalletBean.getWalletBean().setPriKey(priKey);
            WalletBean.getWalletBean().setPubKey(pubkey);

            Log.i(TAG,"randomSeed :"+Utils.bytesToHexString(randomSeed));
            for(String str : words){
                Log.i(TAG,"words :"+str);
            }
            Log.i(TAG,"mnemonicSeed :"+mnemonicSeed);
            Log.i(TAG,"512PrivateKey:"+priKey);
            Log.i(TAG,"512publicKey:"+pubkey);
            Log.i(TAG,"firstAddr:"+firstAddr);


            //Log.i(TAG,"publicKey:"+Utils.bytesToHexString(keys.get(1)));
            //Log.i(TAG,"512publicKey:"+Utils.bytesToHexString(keys512.get(1)));
            Message msg = new Message();
            msg.what = TRANSMIT_WORDS;
            msg.obj = words;
            mHandler.sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,e+"");
        }

    }



}
