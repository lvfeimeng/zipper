package com.zipper.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

public class TransactionDefailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBack;
    private String mCurrency;
    private TextView mDetailsCurrency;
    private TextView mTextUpdate;
    private TextView mTextState;
    private LinearLayout mLinerGradient;
    private ImageView mImgState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_defails);
        Intent intent = getIntent();
        mCurrency = intent.getStringExtra("currency");
        initView();
    }

    private void initView() {

        mBack = findViewById(R.id.img_back);
        mDetailsCurrency = findViewById(R.id.details_currency);
        mTextUpdate = findViewById(R.id.txt_right);

        mTextState = findViewById(R.id.text_state);
        mLinerGradient = findViewById(R.id.liner_gradient);
        mImgState = findViewById(R.id.img_state);
//        updateState();
        mBack.setOnClickListener(this);
        mTextUpdate.setOnClickListener(this);

        mDetailsCurrency.setText(mCurrency);


    }

    @SuppressLint("ResourceAsColor")
    private void updateState() {
        //收入成功
        mTextState.setText("已收入");
        mTextState.setTextColor(R.color.color_button_blue);
        mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
        mImgState.setBackgroundResource(R.mipmap.transaction_ok);
        mTextUpdate.setGravity(View.GONE);

        //转出成功
        mTextState.setText("已转出");
        mTextState.setTextColor(R.color.color_button_blue);
        mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
        mImgState.setBackgroundResource(R.mipmap.transaction_ok);
        mTextUpdate.setGravity(View.GONE);

        //等待转出
        mTextState.setText("等待转出");
        mTextState.setTextColor(R.color.text_minor);
        mLinerGradient.setBackgroundResource(R.drawable.wallet_inprocess_bg);
        mImgState.setBackgroundResource(R.mipmap.inprocess);
        mTextUpdate.setGravity(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_right:
                startActivity(new Intent(this, SwitchAccountActivity.class));
                break;
        }
    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }
}
