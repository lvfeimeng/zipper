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
import com.zipper.wallet.utils.PreferencesUtils;

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
