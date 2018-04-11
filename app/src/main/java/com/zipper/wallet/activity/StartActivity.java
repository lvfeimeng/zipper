package com.zipper.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class StartActivity extends BaseActivity {

    private Button btnCreate,btnImport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_start);
        super.onCreate(savedInstanceState);
        btnCreate = (Button)findViewById(R.id.btn_create);
        btnImport = (Button)findViewById(R.id.btn_import);
        boolean isLogin = PreferencesUtils.getBoolean(mContext,KEY_IS_LOGIN,false,PreferencesUtils.USER);
        if (true){
            btnCreate.setVisibility(View.VISIBLE);
            btnImport.setVisibility(View.VISIBLE);
            /*SQLiteDatabase sqlDB = mContext.openOrCreateDatabase("zipper.db", Context.MODE_PRIVATE,null);
            sqlDB.execSQL("CREATE TABLE IF NOT EXISTS coininfo (id INTEGER PRIMARY KEY NOT NULL ,type INTEGER NOT NULL,name VARCHAR(42) NOT NULL,full_name VARCHAR(420) NOT NULL,addr_algorithm VARCHAR(42) NOT NULL,addr_algorithm_param TEXT,sign_algorithm VARCHAR(42) NOT NULL,sign_algorithm_param TEXT,token_type VARCHAR(42),token_addr VARCHAR(42));");
            //sqlDB.execSQL("drop table coininfo");
            SqliteUtils.openDataBase(mContext);*/

            /*RuntHTTPApi.toReApi(RuntHTTPApi.URL_GET_COINS,new HashMap<>(),new RuntHTTPApi.MyStringCallBack(mContext,new RuntHTTPApi.ResPonse() {
                @Override
                public void doSuccessThing(Map<String, Object> param) {
                    SQLiteDatabase db = LitePal.getDatabase();
                    RuntHTTPApi.printMap(param,"");
                    Log.i("StartActivity",(param.get("data") instanceof Collection)+"");
                    if(param.get("data") instanceof Collection){
                        for(Map map :(List<Map>)param.get("data") ){
                            CoinInfo coinInfo = new CoinInfo(map);
                            Log.i(TAG,coinInfo.getName()+"信息正在保存");
                            coinInfo.save();
                            Log.i(TAG,"信息保存成功");
                        }
                    }
                    SqliteUtils.openDataBase(mContext);
                }

                @Override
                public void doErrorThing(Map<String, Object> param) {

                }
            }));*/
            List<CoinInfo> list = DataSupport.findAll(CoinInfo.class);
            for(CoinInfo coinInfo : list){
                RuntHTTPApi.printMap(coinInfo.toMap(),"");
            }

            /*try {
                CreateAcountUtils.instance(mContext);
                CreateAcountUtils.createADAccount(CreateAcountUtils.createMnemSeed(CreateAcountUtils.getMnemonicCode(CreateAcountUtils.createRandomSeed())),"3421");
            } catch (MnemonicException.MnemonicLengthException e) {
                e.printStackTrace();
            }*/

        }else {
            btnCreate.setVisibility(View.GONE);
            btnImport.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
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
                        startActivity(new Intent(mContext, RiskActivity.class));

                    }
                });
        btnImport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mContext, AddPropertyActivity.class));
                    }
                });
    }
}
