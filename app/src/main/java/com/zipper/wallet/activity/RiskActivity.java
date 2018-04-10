package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zipper.wallet.R;
import com.zipper.wallet.base.CreateActvity;

public class RiskActivity extends CreateActvity {
    Button btnOk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_risk_assessment);
        super.onCreate(savedInstanceState);
        btnOk = (Button)findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(nextClick);
        titlebar.setRightOnclickListener(nextClick);
    }


    View.OnClickListener nextClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(mContext, CreateAccountActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
