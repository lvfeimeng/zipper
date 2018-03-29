package com.zipper.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

/**
 * Created by Administrator on 2018/3/29.
 */

public class WelcomActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        ((Button)findViewById(R.id.btn_create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomActivity.this,CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
