package com.zipper.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.animations.MyAnimations;
import com.zipper.wallet.utils.PreferencesUtils;

/**
 * Created by Administrator on 2018/4/8.
 */

public class StartActivity extends AppCompatActivity {

    private Button btnCreate, btnImport,btnHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btnCreate = (Button) findViewById(R.id.btn_create);
        btnImport = (Button) findViewById(R.id.btn_import);
        btnHome = (Button) findViewById(R.id.btn_wallet_home);
        btnCreate.setVisibility(View.INVISIBLE);
        btnImport.setVisibility(View.INVISIBLE);
        btnHome.setVisibility(View.GONE);
        boolean isfirst = PreferencesUtils.getBoolean(this, "is_first", true, PreferencesUtils.PROJECT);
        if(isfirst){
            startActivity(new Intent(this,
                    WelcomActivity.class));
            finish();
            return;
        }
        boolean isLogin = PreferencesUtils.getBoolean(this, "islogin", false, PreferencesUtils.USER);
        if (true) {

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
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RiskActivity.class));

            }
        });
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, ImportWalletActivity.class));
            }
        });


        btnHome.setOnClickListener(v ->
                startActivity(new Intent(StartActivity.this, MyWalletActivity.class))
        );

    }
}
