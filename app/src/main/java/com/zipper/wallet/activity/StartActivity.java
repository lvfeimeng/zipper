package com.zipper.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.animations.MyAnimations;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.utils.PreferencesUtils;

/**
 * Created by Administrator on 2018/4/8.
 */

public class StartActivity extends BaseActivity {

    private Button btnCreate, btnImport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_start);
        btnCreate = (Button) findViewById(R.id.btn_create);
        btnImport = (Button) findViewById(R.id.btn_import);
        btnCreate.setVisibility(View.INVISIBLE);
        btnImport.setVisibility(View.INVISIBLE);
        boolean isfirst = PreferencesUtils.getBoolean(this, "is_first", true, PreferencesUtils.PROJECT);
        setTransparentStatusBar();
        if (isfirst) {
            startActivity(new Intent(this,
                    WelcomActivity.class));
            finish();
            return;
        }
        /*CreateAcountUtils.instance(this);
        byte[] seed = CreateAcountUtils.createRandomSeed();
        MyLog.i("StartActivity","seed:" + Utils.bytesToHexString(seed));
        EncryptedData ed = new EncryptedData(seed,"abcd",false);
        MyLog.i("StartActivity","seed:" + Utils.bytesToHexString(seed));
        String encrypte = ed.toEncryptedString();
        MyLog.i("StartActivity","Encrypted seed:" + encrypte);
        MyLog.i("StartActivity","decrypt seed:" + Utils.bytesToHexString(new EncryptedData(encrypte).decrypt("abcd")));*/


        boolean isLogin = PreferencesUtils.getBoolean(this, "islogin", false, PreferencesUtils.USER);
        if (!isLogin) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((Activity) StartActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyAnimations.showReBound(btnCreate);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            //MyAnimations.showAnimaInSitu(btnHome);
                        }
                    });
                }
            }, 2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((Activity) StartActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyAnimations.showReBound(btnImport);
                        }
                    });
                }
            }, 2200);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((Activity) StartActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(StartActivity.this,
                                    MyWalletActivity.class));
                            finish();
                        }
                    });
                }
            }, 1000);
        }
        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, WebActivity.class)
                        .putExtra("type", 2)));
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, ImportWalletActivity.class));
            }
        });
    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }


}
