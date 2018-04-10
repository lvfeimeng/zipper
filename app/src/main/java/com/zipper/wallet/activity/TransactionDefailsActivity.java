package com.zipper.wallet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

public class TransactionDefailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_defails);
        initView();
    }

    private void initView() {

        mBack = findViewById(R.id.img_back);
        mBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
}
