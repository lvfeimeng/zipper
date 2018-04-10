package com.zipper.wallet.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
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
    //    protected RadioButton raidoBalance;
//    protected RadioButton raidoAmount;
//    protected RadioGroup radioGroup;
    protected SeekBar seekBar;
    protected TextView textLeft;
    protected TextView textRight;
    protected TextView textCenter;
    protected TextView textCost;
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
//        raidoBalance = (RadioButton) findViewById(R.id.raido_balance);
//        raidoAmount = (RadioButton) findViewById(R.id.raido_amount);
//        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        textLeft = (TextView) findViewById(R.id.text_left);
        textRight = (TextView) findViewById(R.id.text_right);
        textCenter = (TextView) findViewById(R.id.text_center);
        textCost = (TextView) findViewById(R.id.text_cost);
        btnNext = (Button) findViewById(R.id.btn_next);

        textSelectCoins.setOnClickListener(v -> startActivityForResult(new Intent(this, SelectCoinsActivity.class), 100));
        findViewById(R.id.img_back).setOnClickListener(v -> finish());
        textCost.setOnClickListener(v ->
                {
                    if (bottomDialog == null) {
                        initDialog();
                    }
                    bottomDialog.show();
                }
        );
    }

    Dialog bottomDialog = null;
    View contentView = null;
    RadioGroup radioGroup;
    RadioButton raidoBalance, raidoSwitch;
    TextView textConfirm;
    ImageView imgClose;
    int checkedId;

    private void initDialog() {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_miner_cost, null);

        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels;
        params.bottomMargin = 0;
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        textConfirm = contentView.findViewById(R.id.text_confirm);
        imgClose = contentView.findViewById(R.id.img_close);
        radioGroup = contentView.findViewById(R.id.radio_group);
        raidoBalance = contentView.findViewById(R.id.radio_balance);
        raidoSwitch = contentView.findViewById(R.id.radio_switch);

        imgClose.setOnClickListener(v -> dismissDialog());
        textConfirm.setOnClickListener(v -> {
            switch (checkedId) {
                case R.id.raido_balance:
                    textCost.setText("从余额中扣除");
                    break;
                case R.id.radio_switch:
                    textCost.setText("从转账金额中扣除");
                    break;
                default:
                    break;
            }
            dismissDialog();
        });
        checkedId = R.id.raido_balance;
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            this.checkedId = checkedId;
            radioGroup.check(checkedId);
        });
    }

    private void dismissDialog() {
        if (bottomDialog != null && bottomDialog.isShowing()) {
            bottomDialog.dismiss();
        }
    }

    private void setRadioButtonDrawable(RadioButton radioButton) {
        if (radioButton == null) {
            return;
        }
        Drawable drawable = getResources().getDrawable(R.mipmap.ok_blue);
        drawable.setBounds(0, dp2px(5), 0, dp2px(5));
        radioButton.setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_scan) {
            scanCode();
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanCode() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        //Intent intent = new Intent(MyWalletActivity.this, CaptureActivity.class);
                        Intent intent = new Intent(this, ScanQrCodeActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        toast("相机权限被禁止，请先开启权限");
                    }
                });
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
