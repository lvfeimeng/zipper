package com.zipper.wallet.activity;

import android.os.Bundle;
import android.view.View;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

public class PayeeAddressActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payee_address);
        findViewById(R.id.img_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }
}
