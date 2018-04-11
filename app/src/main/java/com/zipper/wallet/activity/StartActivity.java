package com.zipper.wallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.SqliteUtils;

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
            SQLiteDatabase sqlDB = mContext.openOrCreateDatabase("test.db", Context.MODE_PRIVATE,null);
            sqlDB.execSQL("CREATE TABLE IF NOT EXISTS coininfo (id INTEGER PRIMARY KEY NOT NULL ,name VARCHAR(42) NOT NULL,full_name VARCHAR(420) NOT NULL,addr_algorithm VARCHAR(42) NOT NULL, addr_algorithm_param TEXT,sign_algorithm VARCHAR(42) NOT NULL,sign_algorithm_param TEXT,token_type VARCHAR(42), token_addr VARCHAR(42));");
            SqliteUtils.openDataBase(mContext);
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
