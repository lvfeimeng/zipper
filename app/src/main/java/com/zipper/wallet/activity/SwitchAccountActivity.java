package com.zipper.wallet.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.SwitchAccountBean;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.ContactDetailsBean;
import com.zipper.wallet.dialog.ConfirmSwitchAccountDialog;
import com.zipper.wallet.dialog.MinerCostTypeDialog;
import com.zipper.wallet.number.BigNumber;
import com.zipper.wallet.utils.AlgorithmUtils;
import com.zipper.wallet.utils.MyLog;

import java.text.DecimalFormat;

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
    protected SeekBar seekBar;
    protected TextView textLeft;
    protected TextView textRight;
    protected Button btnNext;

    private ImageView imgBack;
    private ImageView imgContacts;
    private TextView textCostType;
    private TextView textMinerCost;
    private TextView textRealAmount;
    private TextView textHelp;

    private BigNumber realCount = new BigNumber("0.0"), totalCount = new BigNumber("0.0"), cast = new BigNumber("0.0");
    private String payerAddress, payeeName, payeeAddress, unit, totalAmount, realAmount, minerCost, remark;
    private int coinsType, minerCostType;

    SwitchAccountBean bean;
    ConfirmSwitchAccountDialog confirmDialog;

    MinerCostTypeDialog minerDialog;
    CoinInfo coinsChoosed;
    ContactDetailsBean contactDetailsBean;

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
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        textLeft = (TextView) findViewById(R.id.text_left);
        textRight = (TextView) findViewById(R.id.text_right);
        textCostType = (TextView) findViewById(R.id.text_cost_type);
        btnNext = (Button) findViewById(R.id.btn_next);

        imgBack = (ImageView) findViewById(R.id.img_back);
        imgContacts = (ImageView) findViewById(R.id.img_contacts);
        textMinerCost = (TextView) findViewById(R.id.text_miner_cost);
        textRealAmount = (TextView) findViewById(R.id.text_real_amount);
        textHelp = (TextView) findViewById(R.id.text_help);

        addTextChangedListener(editWalletAddress);
        addTextChangedListener(editCount);

        imgBack.setOnClickListener(v -> finish());
        textHelp.setOnClickListener(v ->
                startActivity(new Intent(this, WebActivity.class)
                        .putExtra("type", 3)));
        textSelectCoins.setOnClickListener(v -> startActivityForResult(new Intent(this, SelectCoinsActivity.class), 100));
        textCostType.setOnClickListener(v ->
                {
                    if (minerDialog == null) {
                        minerDialog = new MinerCostTypeDialog(this);
                        minerDialog.setCallback(minerCallback);
                    }
                    minerDialog.show();
                }
        );

        imgContacts.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, ContactsActivity.class)
                    .putExtra("isOpenDetail",false), 200);
        });

        btnNext.setOnClickListener(v -> {
            submit();
        });
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String str = new DecimalFormat("0.00000000").format(1.0 * progress / seekBar.getMax());
                MyLog.i(TAG, "str:" + str);
                cast = new BigNumber(str);
                MyLog.i(TAG, "cast:" + cast);
                if (totalAmount == null || totalAmount.equals("")) {
                    return;
                }
                minerCost = totalCount.multiply(cast).toString();
                MyLog.i(TAG, "totalCount:" + totalCount);
                textMinerCost.setText(minerCost);

                realCount = totalCount.subtract(totalCount.multiply(cast));
                realAmount = realCount.toString();
                textRealAmount.setText("到账金额：" + realAmount);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //    private String payerAddress, payeeName, payeeAddress, unit, totalAmount, realAmount, minerCost, remark;
//    private int coinsType, minerCostType;
    private void submit() {
        payeeAddress = editWalletAddress.getText().toString().trim();
        if (TextUtils.isEmpty(payeeAddress)) {
            toast("请输入收款人钱包地址");
            return;
        }
        if (!AlgorithmUtils.validAddress(payeeAddress)) {
            toast("收款人钱包地址错误");
            return;
        }
        if (coinsChoosed == null) {
            toast("请选择币种");
            return;
        }
        if (TextUtils.isEmpty(totalAmount)) {
            toast("请输入转账金额");
            return;
        }
        remark = editRemark.getText().toString().trim();
        if (TextUtils.isEmpty(remark)) {
            remark = "无";
        }
        /*minerCost = textMinerCost.getText().toString().trim();
        if (TextUtils.isEmpty(minerCost)) {
            toast("请选择需要支付的矿工费用");
            return;
        }*/
        bean = new SwitchAccountBean();
        bean.setUnit(unit);
        bean.setTotalAmount(totalAmount);
        bean.setMinerCost(minerCost);
        bean.setRealAmount(realAmount);
        bean.setPayeeName(payeeName);
        bean.setPayeeAddress(payeeAddress);
        bean.setPayerAddress("zp" + WalletBean.getAddress());
        bean.setType(coinsChoosed.getName());
        bean.setRemark(remark);
        confirmDialog = new ConfirmSwitchAccountDialog(this, bean);
        confirmDialog.show();
        confirmDialog.handleResult(() -> {
            toast("确认转账");
        });
    }

//    private void setRadioButtonDrawable(RadioButton radioButton) {
//        if (radioButton == null) {
//            return;
//        }
//        Drawable drawable = getResources().getDrawable(R.mipmap.ok_blue);
//        drawable.setBounds(0, dp2px(5), 0, dp2px(5));
//        radioButton.setCompoundDrawables(null, null, drawable, null);
//    }

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
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE:
                //处理扫描结果（在界面上显示）
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    editWalletAddress.setText(result);
                    //toast("解析结果:" + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    toast("解析二维码失败");
                }
                break;
            case 100://选择币种
                coinsChoosed = (CoinInfo) data.getSerializableExtra("coin_type");
                textSelectCoins.setText(coinsChoosed.getName());
                //toast("name=" + item.getShortName());
                break;
            case 200://选择联系人
                contactDetailsBean = (ContactDetailsBean) data.getSerializableExtra("bean");
                editWalletAddress.setText(contactDetailsBean.getAddress());
                break;
            default:
                break;
        }

    }

    private void addTextChangedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText == editWalletAddress) {
                    String text = s.toString().trim();
                    payeeAddress = text.substring(text.lastIndexOf("】") + 1);
                } else if (editText == editCount) {
                    totalAmount = s.toString().trim();
                    try {
                        MyLog.i(TAG, "totalAmount:" + totalAmount);
                        totalCount = new BigNumber(totalAmount);
                        MyLog.i(TAG, "totalCount:" + totalCount);

                        minerCost = totalCount.multiply(cast).toString();
                        MyLog.i(TAG, "totalCount:" + totalCount);
                        textMinerCost.setText(minerCost);

                        realCount = totalCount.subtract(totalCount.multiply(cast));
                        MyLog.i(TAG, "realCount:" + realCount);
                        realAmount = realCount.toString();
                        MyLog.i(TAG, "realAmount:" + realAmount);
                        textRealAmount.setText("到账金额：" + realAmount);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(payeeAddress) || TextUtils.isEmpty(totalAmount)) {
                    btnNext.setEnabled(false);
                } else {
                    btnNext.setEnabled(true);
                }
            }
        });
    }


    private MinerCostTypeDialog.Callback minerCallback = new MinerCostTypeDialog.Callback() {
        @Override
        public void minerCostType(int type, String text) {
            textCostType.setText(text);
//            if (type == 0) {//从余额中扣除
//
//            } else if (type == 1) {//从转账金额中扣除
//
//            }
        }
    };

}
