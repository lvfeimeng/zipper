package com.zipper.wallet.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.SwitchAccountBean;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.ContactDetailsBean;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.dialog.ConfirmSwitchAccountDialog;
import com.zipper.wallet.dialog.MinerCostTypeDialog;
import com.zipper.wallet.ether.EtherRawTransaction;
import com.zipper.wallet.number.BigNumber;
import com.zipper.wallet.utils.AlgorithmUtils;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferAccountActivity extends BaseActivity implements HomeContract.View {

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
    private TextView textBalance;

    private BigNumber realCount, totalCount, cost, recommend, maxCommend, minCommend, chaCommend;
    private String payerAddress, payeeName, payeeAddress, unit, inputAmount, realAmount, minerCost, remark;
    private int coinsType, minerCostType;
    SwitchAccountBean bean;
    ConfirmSwitchAccountDialog confirmDialog;

    MinerCostTypeDialog minerDialog;
    CoinInfo coinsChoosed;
    ContactDetailsBean contactDetailsBean;

    private String toAddress = "";

    private HomePresenter presenter;

    private double rate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_transfer_account);
        initView();
        presenter = new HomePresenter(this, this);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("转账");/////
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
        textBalance = (TextView) findViewById(R.id.text_balance);

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
                    .putExtra("isOpenDetail", false), 200);
        });

        btnNext.setOnClickListener(v -> {
            submit();
        });

        seekBar.setProgress(0);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (coinsChoosed == null) {
                    toast("请选择币种");
                    return;
                }
                rate = 1.0 * progress / seekBar.getMax();

                String str = "" + rate;
                MyLog.i(TAG, "str:" + str);

                cost = minCommend.add(chaCommend.multiply(new BigNumber(str)));
                MyLog.i(TAG, "cast:" + cost);
                minerCost = cost.toString();
                textMinerCost.setText(minerCost);

                realAmountResultSetting(miner_cost_type);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    /**
     * 提交数据
     */
    private void submit() {
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
        if (TextUtils.isEmpty(inputAmount)) {
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
        bean.setTotalAmount(inputAmount);
        bean.setMinerCost(minerCost);
        bean.setRealAmount(realAmount);
        bean.setPayeeName(payeeName);
        bean.setPayeeAddress(payeeAddress.toUpperCase());
        bean.setPayerAddress(coinsChoosed.getAddr().toUpperCase());//创建时已做eth前加0x的处理
        bean.setType(coinsChoosed.getName());
        bean.setRemark(remark);
        confirmDialog = new ConfirmSwitchAccountDialog(this, bean);
        confirmDialog.show();
        confirmDialog.handleResult(() ->
                confirm()
        );
    }

    private void confirm() {
        if (TextUtils.isEmpty(coinsChoosed.getAmount()) || "null".equalsIgnoreCase(coinsChoosed.getAmount())) {
            toast("账户余额为空");
            return;
        }
        if (TextUtils.isEmpty(coinsChoosed.getDecimals()) || "null".equalsIgnoreCase(coinsChoosed.getDecimals())) {
            //toast("");
            return;
        }
        String total = new BigNumber(coinsChoosed.getAmount()).divide(new BigNumber(coinsChoosed.getDecimals())).toString();
        if (miner_cost_type == 0) {//从余额中扣除
            //判断输入的转账金额+矿工费用<=账户余额
            int result = new BigNumber(inputAmount).add(cost).compare(new BigNumber(total));
            if (result == 1) {
                toast("转账金额+矿工费用不能大于账户余额");
                return;
            }
        } else if (miner_cost_type == 1) {//从转账金额中扣除
            //判断输入的转账金额<账户余额&&转账金额>矿工费用
            int result = new BigNumber(inputAmount).compare(new BigNumber(total));
            int result2 = new BigNumber(inputAmount).compare(cost);
            if (result == 1) {
                toast("转账金额不能大于账户余额");
                return;
            }
            if (result2 == -1) {
                toast("转账金额不足以支付矿工费用");
                return;
            }
        }
        inputPwd();
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
                    toAddress = bundle.getString(CodeUtils.RESULT_STRING);
                    editWalletAddress.setText(toAddress);
                    //toast("解析结果:" + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    toast("解析二维码失败");
                }
                break;
            case 100://选择币种
                coinsChoosed = (CoinInfo) data.getSerializableExtra("coin_type");
                if (coinsChoosed == null || coinsChoosed.getAmount() == null || coinsChoosed.getDecimals() == null) {
                    toast("币种包含的数据信息为空");
                    return;
                }
                String account = new BigNumber(coinsChoosed.getAmount()).divide(new BigNumber(coinsChoosed.getDecimals())).toString();
                textBalance.setText("余额 " + account + " " + coinsChoosed.getName());
                textBalance.setVisibility(View.VISIBLE);

                setSeekBarEnable();
                if (coinsChoosed.getGas_price() == null) {
                    recommend = new BigNumber("0");
                } else {
                    recommend = new BigNumber(coinsChoosed.getGas_price()).multiply(new BigNumber("21000")).divide(new BigNumber(coinsChoosed.getDecimals()));
                }
                minCommend = recommend.multiply(new BigNumber("0.2"));
                maxCommend = recommend.multiply(new BigNumber("20"));
                chaCommend = maxCommend.subtract(minCommend);
                textSelectCoins.setText(coinsChoosed.getName());
                rate = 1.0 * seekBar.getProgress() / seekBar.getMax();
                cost = minCommend.add(chaCommend.multiply(new BigNumber("" + rate)));

                minerCost = cost.toString();
                MyLog.i(TAG, "totalCount:" + totalCount);
                textMinerCost.setText(minerCost);
                MyLog.i(TAG, "cast:" + cost);
                if (inputAmount == null || inputAmount.equals("") || inputAmount.length() == 0) {
                    return;
                }
                //realAmountResultSetting(miner_cost_type);
                break;
            case 200://选择联系人
                contactDetailsBean = (ContactDetailsBean) data.getSerializableExtra("bean");
                payeeName = contactDetailsBean.getName();
                payeeAddress = contactDetailsBean.getAddress();
                String showAddress = "【" + payeeName + "】" + payeeAddress.toUpperCase();
                editWalletAddress.setText(showAddress);
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
                    inputAmount = s.toString().trim();
                    setSeekBarEnable();
                    try {
                        MyLog.i(TAG, "inputAmount:" + inputAmount);
                        totalCount = new BigNumber(inputAmount);
                        MyLog.i(TAG, "totalCount:" + totalCount);
//                        if (cost != null) {
//                            minerCost = cost.toString();
//                            realCount = totalCount.subtract(cost);
//                        } else {
//                            minerCost = "0";
//                            realCount = totalCount;
//                        }
//                        MyLog.i(TAG, "totalCount:" + totalCount);
//                        cost=
//                        minerCost = cost.toString();
//                        textMinerCost.setText(minerCost);

                        realAmountResultSetting(miner_cost_type);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(payeeAddress) || TextUtils.isEmpty(inputAmount)) {
                    btnNext.setEnabled(false);
                } else {
                    btnNext.setEnabled(true);
                }
            }
        });
    }

    private void setSeekBarEnable() {
        if (coinsChoosed != null && editCount.getText() != null && editCount.getText().toString().length() > 0) {
            seekBar.setEnabled(true);
        } else {
            seekBar.setEnabled(false);
        }
    }

    private int miner_cost_type = 0;

    private MinerCostTypeDialog.Callback minerCallback = new MinerCostTypeDialog.Callback() {
        @Override
        public void minerCostType(int type, String text) {
            if (coinsChoosed == null) {
                toast("请先选择币种");
                return;
            }
            if (cost == null) {
                toast("矿工费用计算出错");
                return;
            }
            textCostType.setText(text);
            if (miner_cost_type != type) {//矿工费用方式发生变化
                realAmountResultSetting(type);
                miner_cost_type = type;
            } else {//矿工费用方式未发生变化
                //不做处理
            }
        }
    };

    private void realAmountResultSetting(int type) {
        if (TextUtils.isEmpty(inputAmount)) {
            toast("请输入转账金额");
            return;
        }
        if (type == 0) {//从余额中扣除
            realCount = totalCount;
            realAmount = realCount.toString();
            textRealAmount.setText("到账金额：" + realAmount);
        } else if (type == 1) {//从转账金额中扣除
            realCount = totalCount.subtract(cost);
            realAmount = realCount.toString();
            textRealAmount.setText("到账金额：" + realAmount);
        }
    }

//    private DecimalFormat df = new DecimalFormat("0.00000000");

//    private String getRealValue(String rawValue) {
//        return String.valueOf(Double.parseDouble(rawValue) / Double.parseDouble(coinsChoosed.getDecimals()));
//    }

    private void inputPwd() {
        showInputDialog("验证密码", "", "Password", "", "取消", "确认", InputType.TYPE_TEXT_VARIATION_PASSWORD, new RuntHTTPApi.ResPonse() {

            @Override
            public void doSuccessThing(final Map<String, Object> param) {
                showProgressDialog("正在验证。。。");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String pwd = param.get(INPUT_TEXT).toString().trim();
                            BigInteger nonce = new BigInteger(coinsChoosed.getNonce());
                            BigInteger gasPrice = new BigNumber("0.2").add(new BigNumber(String.valueOf((20 - 0.2) * rate))).multiply(new BigNumber(coinsChoosed.getGas_price())).getBigInteger();

                            BigInteger gasLimit = new BigInteger("21000");
                            toAddress = editWalletAddress.getText().toString().trim();
                            BigInteger value = new BigNumber(realAmount).multiply(new BigNumber(coinsChoosed.getDecimals())).getBigInteger();
                            EtherRawTransaction eth = EtherRawTransaction.createTransaction(nonce, gasPrice, gasLimit, payeeAddress.toLowerCase(), value, "0x");
                            //String signed = Utils.bytesToHexString(eth.getEncodedRaw());
                            //WalletInfo walletInfo = DataSupport.find(WalletInfo.class, 0);
                            List<WalletInfo> list = new ArrayList<>();
                            SqliteUtils.openDataBase(mContext);
                            List<Map> maps = SqliteUtils.selecte("walletinfo");
                            for (Map map : maps) {
                                list.add(new WalletInfo(map));
                            }
                            WalletInfo walletInfo = list.get(0);
                            byte[] seed = new EncryptedData(walletInfo.getEsda_seed()).decrypt(pwd);
                            DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
                            CreateAcountUtils.instance(mContext);
                            DeterministicKey master2 = CreateAcountUtils.getAccount(master, 60);
                            if (master2 == null) {
                                return;
                            }
                            BigInteger bigInteger = master2.getPrivKey();
                            ECKey ecKey = new ECKey(bigInteger, null, false);
                            byte[] bytes = eth.Sign(ecKey);//eth.getEncodedRaw()
                            //String sss = Utils.bytesToHexString(eth.getEncodedRaw());
                            String signed = Utils.bytesToHexString(bytes);
                            Map<String, String> map = new HashMap<>();
                            map.put("signed", "0X" + signed);
                            String json = new Gson().toJson(map);
                            presenter.sendTransaction(coinsChoosed.getId(), json);
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                hideProgressDialog();
                                toast("交易提交失败");
                            });
                        }
                    }
                }.start();
                alertDialog.dismiss();
            }

            @Override
            public void doErrorThing(Map<String, Object> param) {

            }
        });
    }

    @Override
    public void doSuccess(int type, Object obj) {
        hideProgressDialog();
        toast("转账交易已提交");
        String json = (String) obj;
        Log.d(TAG, "doSuccess: json=" + json);
    }

    @Override
    public void doFailure() {
        hideProgressDialog();
    }
}
