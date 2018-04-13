package com.zipper.wallet.activity;

import android.os.Bundle;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

public class PropertyDetailActivity extends BaseActivity {

//    protected ImageView imgBack;
//    protected TextView textWalletCount;
//    protected Button btnSwitchAccount;
//    protected Button btnCollectingBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_property_detail);
//        initView();
    }

//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.img_back) {
//            finish();
//        } else if (view.getId() == R.id.btn_switch_account) {
//            toast("转账");
//        } else if (view.getId() == R.id.btn_collecting_bill) {
//            startActivity(new Intent(this, PayeeAddressActivity.class));
//        }
//    }
//
//    private void initView() {
//        imgBack = (ImageView) findViewById(R.id.img_back);
//        imgBack.setOnClickListener(PropertyDetailActivity.this);
//        textWalletCount = (TextView) findViewById(R.id.text_wallet_count);
//        btnSwitchAccount = (Button) findViewById(R.id.btn_switch_account);
//        btnSwitchAccount.setOnClickListener(PropertyDetailActivity.this);
//        btnCollectingBill = (Button) findViewById(R.id.btn_collecting_bill);
//        btnCollectingBill.setOnClickListener(PropertyDetailActivity.this);
//    }
}
