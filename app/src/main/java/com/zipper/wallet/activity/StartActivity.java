package com.zipper.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.SqliteUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/8.
 */

public class StartActivity extends BaseActivity {

    private Button btnCreate, btnImport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_start);
        super.onCreate(savedInstanceState);
        btnCreate = (Button) findViewById(R.id.btn_create);
        btnImport = (Button) findViewById(R.id.btn_import);
        boolean isLogin = PreferencesUtils.getBoolean(mContext, KEY_IS_LOGIN, false, PreferencesUtils.USER);
        if (true) {
            btnCreate.setVisibility(View.VISIBLE);
            btnImport.setVisibility(View.VISIBLE);
//            SQLiteDatabase sqlDB =  SqliteUtils.openDataBase(mContext);
//            sqlDB.execSQL("drop table coininfo");
//            sqlDB.execSQL("CREATE TABLE IF NOT EXISTS coininfo (id INTEGER PRIMARY KEY NOT NULL ,type INTEGER NOT NULL,name VARCHAR(42) NOT NULL,full_name VARCHAR(420) NOT NULL,addr_algorithm VARCHAR(42) NOT NULL,addr_algorithm_param TEXT,sign_algorithm VARCHAR(42) NOT NULL,sing_algorithm_param TEXT,token_type VARCHAR(42),token_addr VARCHAR(42));");
//
//            SqliteUtils.openDataBase(mContext);
//            RuntHTTPApi.toReApi(RuntHTTPApi.URL_GET_COINS,new HashMap<>(),new RuntHTTPApi.MyStringCallBack(mContext,new RuntHTTPApi.ResPonse() {
//                @Override
//                public void doSuccessThing(Map<String, Object> param) {
//                    SQLiteDatabase db = LitePal.getDatabase();
//                    RuntHTTPApi.printMap(param,"");
//                    Log.i("StartActivity",(param.get("data") instanceof Collection)+"");
//                    if(param.get("data") instanceof Collection){
//                        for(Map map :(List<Map>)param.get("data") ){
//                            CoinInfo coinInfo = new CoinInfo(map);
//                            Log.i(TAG,coinInfo.getName()+"信息正在保存");
//                            coinInfo.save();
//                            Log.i(TAG,"信息保存成功");
//                        }
//                    }
//                    SqliteUtils.openDataBase(mContext);
//                }
//
//                @Override
//                public void doErrorThing(Map<String, Object> param) {
//
//                }
//            }));
//
//            /*WalletInfo hd = new WalletInfo(mContext);
//            hd.createTable(mContext);*/
//            SqliteUtils.test();
//
//            List<WalletInfo> list = new ArrayList<>();
//            SqliteUtils.openDataBase(mContext);
//            List<Map> maps = SqliteUtils.selecte("walletinfo");
//            for(Map map : maps){
//                list.add(new WalletInfo(map));
//            }
//
//            for(WalletInfo walletInfo : list){
//                if(walletInfo.getName().equals(PreferencesUtils.getString(mContext,KEY_WALLET_NAME,PreferencesUtils.VISITOR))){
//                    Log.i(TAG,String.format("name:%s", walletInfo.getName()));
//                }
//                RuntHTTPApi.printMap(walletInfo.toMap(),"");
//            }


        } else {
            btnCreate.setVisibility(View.GONE);
            btnImport.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(mContext,
                                    MyWalletActivity.class));
                            finish();
                        }
                    });
                }
            }, 2000);
        }
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, CreatePwdAcitivty.class));

            }
        });
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ImportWalletActivity.class));
            }
        });


        findViewById(R.id.btn_wallet_home).setOnClickListener(v ->
                startActivity(new Intent(mContext, MyWalletActivity.class))
        );

    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }
}
