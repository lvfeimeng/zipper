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
import com.zipper.wallet.number.BigNumber;
import com.zipper.wallet.utils.NetworkUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TransactionDefailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBack;
    private PropertyRecord mCurrency;
    private TextView mDetailsCurrency;
    private TextView mTextUpdate;
    private TextView mTextState;
    private LinearLayout mLinerGradient;
    private ImageView mImgState;

    protected TextView textPayerAddress;
    protected TextView textPayeeAddress;
    protected TextView textMinerCost;
    protected TextView textRemark;
    protected TextView textTransferId;
    protected TextView textBlock;
    protected TextView textTransferTime;

    private int IN = 1;
    private int OUT = 2;
    private int FAIL = 4;

    @SuppressLint("HandlerLeak")
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

        textPayerAddress = (TextView) findViewById(R.id.text_payer_address);
        textPayeeAddress = (TextView) findViewById(R.id.text_payee_address);
        textMinerCost = (TextView) findViewById(R.id.text_miner_cost);
        textRemark = (TextView) findViewById(R.id.text_remark);
        textTransferId = (TextView) findViewById(R.id.text_transfer_id);
        textBlock = (TextView) findViewById(R.id.text_block);
        textTransferTime = (TextView) findViewById(R.id.text_transfer_time);

        if (mCurrency.getAddr().equals(mCurrency.getTo())) {
            mTextState.setText("等待转入");
            handlerThread(IN);
        } else if (mCurrency.getAddr().equals(mCurrency.getFrom())) {
            handlerThread(OUT);
        }
//        textPayeeAddress.setText();
        textMinerCost.setText(new BigNumber(mCurrency.getFee()).divide(new BigNumber(mCurrency.getDeciamls())).toString()+" "+mCurrency.getName());
        textRemark.setText("无");
        textTransferId.setText(mCurrency.getHash());
        Date date = new Date();
        date.setTime(mCurrency.getTimestamp()*1000);
        textTransferTime.setText(sdf.format(date));
        textBlock.setText(mCurrency.getHeight());
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String getFormatData(String amount, String decimals) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals) || "null".equalsIgnoreCase(amount) || "null".equalsIgnoreCase(decimals)) {
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
                //收入成功--确认收入
                mTextState.setText("已收入");
                mTextState.setTextColor(R.color.color_button_blue);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
                mImgState.setBackgroundResource(R.mipmap.transaction_ok);
                mDetailsCurrency.setText("+" + mCurrency.getValue() + mCurrency.getName());
                break;

            case 2:
                //转出成功--确认转出
                mTextState.setText("已转出");
                mTextState.setTextColor(R.color.color_button_blue);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
                mImgState.setBackgroundResource(R.mipmap.transaction_ok);
                mDetailsCurrency.setText("-" + mCurrency.getValue() + mCurrency.getName());
                break;

            case 3:
                //等待转出--待确认
                mTextState.setText("等待转出");
                mTextState.setTextColor(R.color.text_minor);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_inprocess_bg);
                mImgState.setBackgroundResource(R.mipmap.inprocess);
                mDetailsCurrency.setText("-" + mCurrency.getValue() + mCurrency.getName());
                break;

            case 4:
                //转出失败--失败
                fuilurehints();
                mTextState.setText("已扣除");
                mTextState.setTextColor(R.color.text_minor);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_fail_bg);
                mImgState.setBackgroundResource(R.mipmap.error);
                mTextUpdate.setVisibility(View.VISIBLE);
                mTextUpdate.setText("重发");
                mDetailsCurrency.setText("-" + mCurrency.getValue() + mCurrency.getName());
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

                if (NetworkUtils.getNetworkType(this, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)) == NetworkUtils.NetworkType.NONE) {
                    showTipDialog("没有网络连接", "是否开启网络设置", "取消", "去设置", new RuntHTTPApi.ResPonse() {
                        @Override
                        public void doSuccessThing(Map<String, Object> param) {
                            NetworkUtils.setNetwork(mContext);
                        }

                        @Override
                        public void doErrorThing(Map<String, Object> param) {
                        }
                    });
                } else if (!NetworkUtils.checkNetworkState(this)) {
                    toast("连接不到互联网，请稍后再试！！！");
                } else {
                    Intent intent = new Intent(this, TransferAccountActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("details", mCurrency);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }

    private void fuilurehints() {

        showTipDialog("转账失败", "服务器连接失败，请稍后重试", "", "知道了", null);

    }
}
