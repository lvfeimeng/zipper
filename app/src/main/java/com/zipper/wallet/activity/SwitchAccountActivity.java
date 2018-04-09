package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

public class SwitchAccountActivity extends BaseActivity {

    public static final int REQUEST_CODE = 1000;

    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected AppBarLayout appBar;
    protected EditText editWalletAddress;
    protected TextView textSelectCoins;
    protected TextView textShowCoin;
    protected EditText editCount;
    protected EditText editRemark;
    protected RadioButton raidoBalance;
    protected RadioButton raidoAmount;
    protected RadioGroup radioGroup;
    protected SeekBar seekBar;
    protected TextView textLeft;
    protected TextView textRight;
    protected TextView textCenter;
    protected Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_switch_account);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("转账");
        editWalletAddress = (EditText) findViewById(R.id.editWalletAddress);
        textSelectCoins = (TextView) findViewById(R.id.text_select_coins);
        textShowCoin = (TextView) findViewById(R.id.text_show_coin);
        editCount = (EditText) findViewById(R.id.edit_count);
        editRemark = (EditText) findViewById(R.id.edit_remark);
        raidoBalance = (RadioButton) findViewById(R.id.raido_balance);
        raidoAmount = (RadioButton) findViewById(R.id.raido_amount);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        textLeft = (TextView) findViewById(R.id.text_left);
        textRight = (TextView) findViewById(R.id.text_right);
        textCenter = (TextView) findViewById(R.id.text_center);
        btnNext = (Button) findViewById(R.id.btn_next);

        textSelectCoins.setOnClickListener(v ->
                startActivityForResult(
                        new Intent(this, SelectCoinsActivity.class),
                        100));
        findViewById(R.id.img_back)
                .setOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    toast("解析结果:" + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    toast("解析二维码失败");
                }
            }
        }
    }

}
