package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.CreateActvity;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.RuntListSeria;

import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class BackUpAcitivty extends CreateActvity {

    Button btnBackup;

    final int SAVE_PRIVATE = 100;
    final int TRANSMIT_WORDS = 101;
    final int ERROR = 404;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            hideProgressDialog();
            Object obj = msg.obj;
            switch (msg.what) {
                case ERROR:

                    if(obj!=null){

                        toast(obj.toString());
                    }else{
                        toast("生成数据错误");
                    }


                    break;

                case TRANSMIT_WORDS:
                    if(obj!=null){
                        if(obj instanceof  List){
                            PreferencesUtils.putBoolean(mContext,KEY_IS_LOGIN,true,PreferencesUtils.USER);
                            PreferencesUtils.clearData(mContext,PreferencesUtils.VISITOR);
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
                showInputDialog("请输入密码","输入您设置的登录密码","Password","","取消","确认", InputType.TYPE_TEXT_VARIATION_PASSWORD,new RuntHTTPApi.ResPonse(){

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
                            //hideProgressDialog();
                            showTipDialog("密码错误","知道了",null);
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

            //randomSeed = Utils.hexStringToByteArray("937b52d8e571ed26f32c71dcaff6d57a");

            List<String> words = CreateAcountUtils.getMnemonicCode(randomSeed);//一局随机数获取助记词
            for(String str : words){
                MyLog.i(TAG,"words :"+str);
            }
            byte[] mnemonicSeed =  CreateAcountUtils.createMnemSeed(words);//由助记词和密码生成种子,方法内含有转换512哈系数方式


            DeterministicKey master = CreateAcountUtils.CreateRootKey(mnemonicSeed);//生成根公私钥对象
            CreateAcountUtils.saveCoins(master, mContext, new CreateAcountUtils.Callback() {
                @Override
                public void saveSuccess() {
                    String full_address = "zp" + CreateAcountUtils.getWalletAddr(master, 60);
                    //putString("full_address",full_address);
                    CreateAcountUtils.saveWallet(Utils.bytesToHexString(randomSeed), Utils.bytesToHexString(mnemonicSeed), full_address, new RuntHTTPApi.ResPonse() {
                        @Override
                        public void doSuccessThing(Map<String, Object> param) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = TRANSMIT_WORDS;
                            msg.obj = words;
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void doErrorThing(Map<String, Object> param) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = ERROR;
                            msg.obj = param.get("error");
                            mHandler.sendMessage(msg);
                        }
                    });
                }

                @Override
                public void saveFailure() {
                    Message msg = new Message();
                    msg.what = ERROR;
                    msg.obj = "";
                    mHandler.sendMessage(msg);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            MyLog.e(TAG,e+"");
            toast(e.getMessage());
            hideProgressDialog();
        }

    }


}
