package com.zipper.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.PropertyRecord;
import com.zipper.wallet.utils.NetworkUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.text.DecimalFormat;
import java.util.Map;

public class TransactionDefailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBack;
    private PropertyRecord mCurrency;
    private TextView mDetailsCurrency;
    private TextView mTextUpdate;
    private TextView mTextState;
    private LinearLayout mLinerGradient;
    private ImageView mImgState;

    private int IN = 1;
    private int OUT = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateState(msg.what);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_defails);
        Intent intent = getIntent();
        mCurrency = (PropertyRecord) intent.getSerializableExtra("currency");
        initView();
    }

    private void initView() {

        mBack = findViewById(R.id.img_back);
        mDetailsCurrency = findViewById(R.id.details_currency);
        mTextUpdate = findViewById(R.id.txt_right);

        mTextState = findViewById(R.id.text_state);
        mLinerGradient = findViewById(R.id.liner_gradient);
        mImgState = findViewById(R.id.img_state);
        mBack.setOnClickListener(this);
        mTextUpdate.setOnClickListener(this);

        mDetailsCurrency.setText(mCurrency.getValue());

        if (mCurrency.getAddr().equals(mCurrency.getTo())) {
            mTextState.setText("等待转入");
            handlerThread(IN);
        } else if (mCurrency.getAddr().equals(mCurrency.getFrom())) {
            handlerThread(OUT);
        }


    }

    private String getFormatData(String amount, String decimals) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals)||"null".equalsIgnoreCase(amount)||"null".equalsIgnoreCase(decimals)) {
            return "0";
        }
        double result = Double.parseDouble(amount) / Double.parseDouble(decimals);
        return new DecimalFormat("0.00000000").format(result);
    }

    private void handlerThread(int num) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                switch (num) {

                    case 1:
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                        break;

                    case 2:
                        msg.what = 2;
                        mHandler.sendMessage(msg);
                        break;

                }
            }
        }, 1000);
    }

    @SuppressLint("ResourceAsColor")
    private void updateState(int details) {
        switch (details) {

            case 1:
                //收入成功
                mTextState.setText("已收入");
                mTextState.setTextColor(R.color.color_button_blue);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
                mImgState.setBackgroundResource(R.mipmap.transaction_ok);
                mTextUpdate.setVisibility(View.GONE);
                break;

            case 2:
                //转出成功
                mTextState.setText("已转出");
                mTextState.setTextColor(R.color.color_button_blue);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
                mImgState.setBackgroundResource(R.mipmap.transaction_ok);
                mTextUpdate.setVisibility(View.GONE);
                break;

            case 3:
                //等待转出
                mTextState.setText("等待转出");
                mTextState.setTextColor(R.color.text_minor);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_inprocess_bg);
                mImgState.setBackgroundResource(R.mipmap.inprocess);
                mTextUpdate.setVisibility(View.VISIBLE);
                break;

            default:
                break;

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_right:

                if(NetworkUtils.getNetworkType(this, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)) == NetworkUtils.NetworkType.NONE){
                    showTipDialog("没有网络连接","是否开启网络设置","取消","去设置", new RuntHTTPApi.ResPonse() {
                        @Override
                        public void doSuccessThing(Map<String, Object> param) {
                            NetworkUtils.setNetwork(mContext);
                        }

                        @Override
                        public void doErrorThing(Map<String, Object> param) {
                        }
                    });
                }else if(!NetworkUtils.checkNetworkState(this)){
                    toast("连接不到互联网，请稍后再试！！！");
                }else {
                    startActivity(new Intent(this, SwitchAccountActivity.class));
                }
                break;
        }
    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }
}
