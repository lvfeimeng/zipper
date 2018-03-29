package com.zipper.wallet;

import android.content.Intent;
import android.os.Bundle;

import com.zipper.wallet.activity.AgreementActivity;
import com.zipper.wallet.activity.BackUpAcitivty;
import com.zipper.wallet.activity.MnemonicActivity;
import com.zipper.wallet.activity.WelcomActivity;
import com.zipper.wallet.base.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,MnemonicActivity.class);
        startActivity(intent);
        finish();
    }
}
