package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

/**
 * Created by Administrator on 2018/3/29.
 */

public class AgreementActivity extends BaseActivity {

    Button btnNext;
    CheckBox check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        btnNext = (Button)findViewById(R.id.btn_next);
        check = (CheckBox)findViewById(R.id.check);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check.isChecked()){
                    Intent intent = new Intent(AgreementActivity.this,WelcomActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    toast("需要同意用户协议哦！！！");
                }
            }
        });
    }
}
