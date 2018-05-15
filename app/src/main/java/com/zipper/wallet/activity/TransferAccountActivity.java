package com.zipper.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.RegAddr;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.PayAddressBean;
import com.zipper.wallet.bean.SwitchAccountBean;
import com.zipper.wallet.btc.Out;
import com.zipper.wallet.btc.Tx;
import com.zipper.wallet.btc.TxBuilder;
import com.zipper.wallet.database.BtcUtxo;
import com.zipper.wallet.database.CoinBalance;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.ContactDetailsBean;
import com.zipper.wallet.database.PropertyRecord;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.dialog.ConfirmSwitchAccountDialog;
import com.zipper.wallet.dialog.MinerCostTypeDialog;
import com.zipper.wallet.ether.ERC20Token;
import com.zipper.wallet.ether.EtherRawTransaction;
import com.zipper.wallet.utils.AlgorithmUtils;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.ScreenUtils;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.BitherjSettings;
import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.TransactionSignature;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.exception.TxBuilderException;
import net.bither.bitherj.script.ScriptBuilder;
import net.bither.bitherj.utils.Utils;

import org.bouncycastle.util.Arrays;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
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
    protected View viewScale;
    protected RelativeLayout layoutTotalScale;

    private ImageView imgBack;
    private ImageView imgContacts;
    private TextView textCostType;
    private TextView textMinerCost;
    private TextView textRealAmount;
    private TextView textHelp;
    private TextView textBalance;

    private BigDecimal realCount, totalCount, cost, recommend, maxCommend, minCommend;//chaCommend;
    private String payerAddress, payeeName, payeeAddress, unit = "", inputAmount, realAmount, minerCost, remark;
    private int coinsType, minerCostType;
    SwitchAccountBean bean;
    ConfirmSwitchAccountDialog confirmDialog;

    MinerCostTypeDialog minerDialog;
    CoinInfo coinsChoosed;
    ContactDetailsBean contactDetailsBean;

    private boolean isSendTransaction = true;

    private HomePresenter presenter;

    private double rate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_transfer_account);
        initView();
        presenter = new HomePresenter(this, this);
        if (getIntent() != null) {
            coinsChoosed = (CoinInfo) getIntent().getSerializableExtra("item");
            if (coinsChoosed!=null) {
                getCoinBalance();
            }
            //coinInfoSetting(coinsChoosed);
        }
    }

    private void setDefaultScale() {
        double rate = 1.0 / (20 - 0.2);
        int totalWidth = ScreenUtils.getScreenWidth(this) - dp2px(60);
        int realWidth = (int) (rate * totalWidth);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp2px(1), dp2px(15));
        params.leftMargin = realWidth;
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        viewScale.setLayoutParams(params);
        viewScale.setVisibility(View.VISIBLE);
        seekBar.setProgress((int) (rate * seekBar.getMax()));
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

        viewScale = (View) findViewById(R.id.view_scale);
        layoutTotalScale = (RelativeLayout) findViewById(R.id.layout_total_scale);

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

        rate = 1.0 / (20 - 0.2);
        seekBar.setProgress((int) (rate * seekBar.getMax()));
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (coinsChoosed == null) {
                    toast("请选择币种");
                    return;
                }
                rate = 1.0 * progress / seekBar.getMax();

                cost = minCommend.add(recommend.multiply(new BigDecimal(String.valueOf(19.8 * rate))));
                MyLog.i(TAG, "cast:" + cost);
                minerCost = df.format(cost.doubleValue());
                textMinerCost.setText(minerCost + " " + unit);

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
        }else if((inputAmount.indexOf(".") > -1?inputAmount.substring(inputAmount.indexOf(".")).length():inputAmount.length())>8||(inputAmount.indexOf(".") > -1?inputAmount.substring(0,inputAmount.indexOf(".")).length():inputAmount.length())>8){
            toast("转账金额整数和小数部分不得超过8位");
            return;
        }
        remark = editRemark.getText().toString().trim();
        if (TextUtils.isEmpty(remark)) {
            remark = "无";
        }

        if (TextUtils.isEmpty(coinsChoosed.getAmount()) || "null".equals(coinsChoosed.getAmount()) || "0".equals(coinsChoosed.getAmount())) {
            toast("账户余额为0");
            return;
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
        String total = new BigDecimal(coinsChoosed.getAmount()).divide(new BigDecimal(coinsChoosed.getDecimals())).toString();
        if (miner_cost_type == 0) {//从余额中扣除
            //判断输入的转账金额+矿工费用<=账户余额
            MyLog.i(TAG,String.format("inputAmount:%s,cost:%s,total:%s",inputAmount,cost,total));
            int result = new BigDecimal(inputAmount).add(cost).compareTo(new BigDecimal(total));
            if (result == 1) {
                toast("转账金额+矿工费用不能大于账户余额");
                return;
            }
        } else if (miner_cost_type == 1) {//从转账金额中扣除
            //判断输入的转账金额<账户余额&&转账金额>矿工费用
            int result = new BigDecimal(inputAmount).compareTo(new BigDecimal(total));
            int result2 = new BigDecimal(inputAmount).compareTo(cost);
            if (result == 1) {
                toast("转账金额不能大于账户余额");
                return;
            }
            if (result2 == -1) {
                toast("转账金额不足以支付矿工费用");
                return;
            }
        }
        if (payeeAddress.startsWith("zp") || payeeAddress.startsWith("ZP")) {
            payeeAddress = "0x" + payeeAddress.substring(2);
            Map<String, String> map = new HashMap<>();
            map.put("addr", payeeAddress);
            String json = new Gson().toJson(map);
            RegAddr.checkFullAddress(json, new RegAddr.Callback() {
                @Override
                public void doSuccess(String data) {
                    String[] array = data.split("\\|");
                    for (int i = 0, len = array.length; i < len; i++) {
                        if (array[i].equals(String.valueOf(coinsChoosed.getCoin_id())) && i < len - 1) {
                            payeeAddress = array[i + 1];
                            inputPwd();
                        }
                    }
                }

                @Override
                public void doFailure() {

                }
            });
        } else {
            inputPwd();
        }
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
                    payeeAddress = bundle.getString(CodeUtils.RESULT_STRING);
                    editWalletAddress.setText(payeeAddress);
                    //toast("解析结果:" + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    toast("解析二维码失败");
                }
                break;
            case 100://选择币种
                coinsChoosed = (CoinInfo) data.getSerializableExtra("coin_type");
                if (coinsChoosed!=null) {
                    getCoinBalance();
                }
                //coinInfoSetting(coinsChoosed);
                break;
            case 200://选择联系人
                contactDetailsBean = (ContactDetailsBean) data.getSerializableExtra("bean");
                payeeName = contactDetailsBean.getName();
                payeeAddress = contactDetailsBean.getAddress();
                editWalletAddress.setText(
                        new StringBuilder()
                                .append(payeeAddress.toUpperCase())
                                .append("【")
                                .append(payeeName)
                                .append("】")
                );
                editWalletAddress.setSelection(editWalletAddress.length());
                break;
            default:
                break;
        }

    }

    private void getCoinBalance(){
        Map<String, String> map = new HashMap<>();
        map.put("address", coinsChoosed.getAddr());
        isSendTransaction = false;
        presenter.getCoinBalance(coinsChoosed.getCoin_id(), new Gson().toJson(map));
    }

    private void coinInfoSetting(CoinInfo coinsChoosed) {
        if (coinsChoosed == null) {
            //toast("币种包含的数据信息为空");
            return;
        }
//        if ("btc".equalsIgnoreCase(coinsChoosed.getName())) {
//            coinsChoosed.setAmount("100000");
//        }
        textSelectCoins.setText(coinsChoosed.getName());
        payerAddress = coinsChoosed.getAddr();
        if ("eth".equalsIgnoreCase(coinsChoosed.getAddr_algorithm())) {
            unit = "ETH";
        } else {
            unit = coinsChoosed.getName();
        }
        if (TextUtils.isEmpty(coinsChoosed.getDecimals()) || "null".equalsIgnoreCase(coinsChoosed.getDecimals())) {
            toast("币种余额精度为空");
            return;
        }
        String account = new BigDecimal(coinsChoosed.getAmount()).divide(new BigDecimal(coinsChoosed.getDecimals()), 8, BigDecimal.ROUND_HALF_UP).toPlainString();
        textBalance.setText("余额 " + account + " " + coinsChoosed.getName());
        textBalance.setVisibility(View.VISIBLE);

        setSeekBarEnable();
        if ("eth".equalsIgnoreCase(coinsChoosed.getAddr_algorithm())) {
            if (coinsChoosed.getGas_price() == null) {
                recommend = new BigDecimal("0");
            } else {
                if (TextUtils.isEmpty(coinsChoosed.getToken_addr())) {
                    gasLimit = new BigInteger("21000");
                } else {
                    gasLimit = new BigInteger("90000");
                }
                recommend = new BigDecimal(coinsChoosed.getGas_price()).multiply(new BigDecimal(gasLimit)).divide(new BigDecimal(coinsChoosed.getDecimals()));
            }
            minCommend = recommend.multiply(new BigDecimal("0.2"));
            maxCommend = recommend.multiply(new BigDecimal("20"));
//        chaCommend = maxCommend.subtract(minCommend);
//        chaCommend = recommend.multiply(new BigDecimal("19.8"));

            cost = recommend;
        } else if ("btc".equalsIgnoreCase(coinsChoosed.getAddr_algorithm())) {
            if (TextUtils.isEmpty(editCount.getText())) {
                int txSize = TxBuilder.estimationTxSize(1, 2);
                recommend = new BigDecimal(coinsChoosed.getGas_price()).multiply(new BigDecimal(String.valueOf(txSize))).divide(new BigDecimal(coinsChoosed.getDecimals()));
                minCommend = recommend.multiply(new BigDecimal("0.2"));
                maxCommend = recommend.multiply(new BigDecimal("20"));
                cost=recommend;
            }
        }
        minerCost = df.format(cost.doubleValue());
        MyLog.i(TAG, "minerCost:" + minerCost);
        textMinerCost.setText(minerCost + " " + unit);

        setDefaultScale();
        realAmountResultSetting(miner_cost_type);
    }

    private DecimalFormat df = new DecimalFormat("0.00000000");

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
                    payeeAddress = s.toString().trim();
                    if (payeeAddress.contains("【")) {
                        payeeAddress = payeeAddress.substring(0, payeeAddress.lastIndexOf("【"));
                    }
                } else if (editText == editCount) {
                    try {
                        inputAmount = s.toString().trim();
                        MyLog.i(TAG, "inputAmount:" + inputAmount);

                        setSeekBarEnable();

                        totalCount = new BigDecimal(inputAmount);
                        MyLog.i(TAG, "totalCount:" + totalCount);
                        if ("btc".equalsIgnoreCase(coinsChoosed.getAddr_algorithm())) {
                            recommend = getRecommendFee(new BigDecimal(inputAmount)).divide(new BigDecimal(coinsChoosed.getDecimals()));
                            minCommend = recommend.multiply(new BigDecimal("0.2"));
                            maxCommend = recommend.multiply(new BigDecimal("20"));

                            cost = recommend;
                            minerCost = df.format(cost.doubleValue());
                            MyLog.i(TAG, "minerCost:" + minerCost);
                            textMinerCost.setText(minerCost + " " + unit);
                        }
                        realAmountResultSetting(miner_cost_type);
                    } catch (Exception e) {
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
            realAmount = realCount.setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString();
            textRealAmount.setText("到账金额：" + realAmount + " " + unit);
        } else if (type == 1) {//从转账金额中扣除
            realCount = totalCount.subtract(cost);
            realAmount = realCount.setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString();
            textRealAmount.setText("到账金额：" + realAmount + " " + unit);
        }
    }

    String valueString = null;
    String miner_cost = null;

    private void inputPwd() {
        showInputDialog("验证密码", "", "Password", "", "取消", "确认", InputType.TYPE_TEXT_VARIATION_PASSWORD, new RuntHTTPApi.ResPonse() {

            @Override
            public void doSuccessThing(final Map<String, Object> param) {
                showProgressDialog("正在验证。。。");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();
                            String pwd = param.get(INPUT_TEXT).toString().trim();
                            sendEthTransaction(pwd);
                            Looper.loop();
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

    BigInteger gasLimit = null;

    private void sendEthTransaction(String pwd) {
        isSendTransaction = true;

        BigDecimal totalCountDecimal = new BigDecimal(realAmount).multiply(new BigDecimal(coinsChoosed.getDecimals()));
        valueString = totalCountDecimal.toPlainString();

        List<WalletInfo> list = new ArrayList<>();
        SqliteUtils.openDataBase(mContext);
        List<Map> maps = SqliteUtils.selecte("walletinfo");
        for (Map map : maps) {
            list.add(new WalletInfo(map));
        }
        WalletInfo walletInfo = list.get(0);
        byte[] seed = new EncryptedData(walletInfo.getEsda_seed()).decrypt(pwd);
        confirmDialog.dismiss();
        DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);
        CreateAcountUtils.instance(mContext);
        DeterministicKey master2 = CreateAcountUtils.getAccount(master, coinsChoosed.getType());
        if (master2 == null) {
            return;
        }

        if ("eth".equalsIgnoreCase(coinsChoosed.getAddr_algorithm())) {
            BigInteger nonce = new BigInteger(coinsChoosed.getNonce());

            String gasPriceString = new BigDecimal("0.2").add(new BigDecimal(String.valueOf((20 - 0.2) * rate))).multiply(new BigDecimal(coinsChoosed.getGas_price())).setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString();
            BigInteger gasPrice = new BigDecimal(gasPriceString).toBigInteger();

            BigDecimal minerCostDecimal = new BigDecimal("0.2").add(new BigDecimal(String.valueOf((20 - 0.2) * rate))).multiply(new BigDecimal(coinsChoosed.getGas_price())).multiply(new BigDecimal(gasLimit));

            miner_cost = minerCostDecimal.toPlainString();

            BigInteger value = new BigDecimal(valueString).toBigInteger();

            EtherRawTransaction eth = null;
            if (TextUtils.isEmpty(coinsChoosed.getToken_addr())) {
                eth = EtherRawTransaction.createTransaction(nonce, gasPrice, gasLimit, payeeAddress.toLowerCase(), value, "0x");
            } else {
                //6.0以下系统，取消请求权限
                if(Build.VERSION.SDK_INT< Build.VERSION_CODES.N){
                    toast("暂不支持android 6.0 及以前的系统");
                    return;
                }
                eth = EtherRawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinsChoosed.getToken_addr().toLowerCase()
                        , new BigInteger("0"), ERC20Token.transfer(payeeAddress.toLowerCase(), value));
            }

            //String signed = Utils.bytesToHexString(eth.getEncodedRaw());
            //WalletInfo walletInfo = DataSupport.find(WalletInfo.class, 0);

            BigInteger bigInteger = master2.getPrivKey();
            ECKey ecKey = new ECKey(bigInteger, null, false);
            byte[] bytes = eth.Sign(ecKey);//eth.getEncodedRaw()
            //String sss = Utils.bytesToHexString(eth.getEncodedRaw());
            String signed = Utils.bytesToHexString(bytes);
            Map<String, String> map = new HashMap<>();
            map.put("signed", "0X" + signed);

            if (TextUtils.isEmpty(coinsChoosed.getToken_addr())) {
                String json = new Gson().toJson(map);
                presenter.sendTransaction(coinsChoosed.getCoin_id(), json);
            } else {
                map.put("token_address", coinsChoosed.getToken_addr().toLowerCase());
                String json = new Gson().toJson(map);
                presenter.sendTokenTransaction(coinsChoosed.getCoin_id(), coinsChoosed.getToken_type(),json);
            }
        } else if ("btc".equalsIgnoreCase(coinsChoosed.getAddr_algorithm())) {
            try {
                miner_cost=cost.multiply(new BigDecimal(coinsChoosed.getDecimals())).toPlainString();
                TxBuilder txBuilder = new TxBuilder();
                BigDecimal totalValue = totalCountDecimal.add(new BigDecimal(miner_cost));
                List<BtcUtxo> utxos = getUtxos(totalValue);

                List<Out> outList = new ArrayList<>();
                outList.addAll(getBtcOuts(utxos));

                List<Long> amoutList = new ArrayList<>();
                amoutList.add(Long.valueOf(totalCountDecimal.toBigInteger().longValue()));

                long longValue = totalValue.toBigInteger().longValue();
                amoutList.add(getBtcChange("" + longValue, utxos));

                List<String> addressList = new ArrayList<>();
                addressList.add(payeeAddress);
                addressList.add(payerAddress);

                BitherjSettings.addressHeader = Integer.parseInt(coinsChoosed.getAddr_public());
                BitherjSettings.p2shHeader = Integer.parseInt(coinsChoosed.getAddr_script());
                BitherjSettings.signForkID = Integer.parseInt(coinsChoosed.getSign_fork());
                Tx tx = txBuilder.buildTxFromAllAddress(outList, amoutList, addressList);

                byte signType = (byte) TransactionSignature.calcSigHashValue(TransactionSignature.SigHash.ALL, false);
                List<byte[]> unsignedHashes = tx.getUnsignedInHashes(signType);
                ArrayList<byte[]> signatures = new ArrayList<byte[]>();
                for (int i = 0; i < unsignedHashes.size(); i++) {
                    byte[] unsigned = unsignedHashes.get(i);

                    TransactionSignature signature = new TransactionSignature(master2.sign(unsigned, null),
                            TransactionSignature.SigHash.ALL, false);
                    signatures.add(ScriptBuilder.createInputScript(signature, master2).getProgram());
                }
                tx.signWithSignatures(signatures);
                byte[] bytes = tx.bitcoinSerialize();
                String signed = Utils.bytesToHexString(bytes);
                Message msg = mHandler.obtainMessage();
                msg.obj = signed;
                msg.what = 1;
                mHandler.sendMessage(msg);
            } catch (TxBuilderException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String signed = (String) msg.obj;
//            toast("signed = " + signed);
            Log.d(TAG, "sendEthTransaction: signed = " + signed);

            Map<String, String> map = new HashMap<>();
            map.put("signed", signed);

            String json = new Gson().toJson(map);
            presenter.sendTransaction(coinsChoosed.getCoin_id(), json);
        }
    };

    private void sendTokenTransaction(String pwd) {
        //EtherRawTransaction token=EtherRawTransaction.createTransaction()
    }

    @Override
    public void doSuccess(int coin_id, Object obj) {
        Log.d(TAG, "doSuccess: coin_id=" + coin_id+ " obj:"+obj);
        MyLog.i(TAG,"obj class:"+obj.getClass());
        if (isSendTransaction) {
            hideProgressDialog();
        }
        if (obj == null) {
            handleTransferResult("", "-2");
            return;
        }

        try {
            if (isSendTransaction) {
                String json = (String) obj;
                if (json == null) {
                    return;
                }
                JSONObject jsonObject = new JSONObject(json);
                int errCode = jsonObject.optInt("errCode");
                if (errCode == 0) {
                    toast("转账交易已提交");
                    if (confirmDialog != null && confirmDialog.isShowing()) {
                        confirmDialog.dismiss();
                    }
                    handleTransferResult(jsonObject.optString("data"), "-1");
                }else{
                    handleTransferResult("", "-2");
                }
            } else {
                CoinBalance balance = (CoinBalance) obj;
                if (balance == null) {
                    return;
                }
                coinsChoosed.setAmount(balance.getAmount());
                coinsChoosed.setGas_price(balance.getGas_price());
                coinsChoosed.setNonce(balance.getNonce());
                coinInfoSetting(coinsChoosed);
                ContentValues values = new ContentValues();
                values.put("amount", coinsChoosed.getAmount());
                values.put("gas_price", coinsChoosed.getGas_price());
                values.put("nonce", coinsChoosed.getNonce());
                DataSupport.update(CoinInfo.class, values, coinsChoosed.getCoin_id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //{"data":"0x4dde249e130619d21eb7b917797ddc6f55aa7ddae4fd41c2e1edc14c1f973f48","errCode":0,"hash":"849391b28d880c9fa97f05422d2d2673"}

    @Override
    public void doFailure() {
        if (isSendTransaction) {
            if (confirmDialog != null && confirmDialog.isShowing()) {
                confirmDialog.dismiss();
            }
            hideProgressDialog();
            handleTransferResult("", "-2");
        } else {
            coinInfoSetting(coinsChoosed);
        }
    }
    private void handleTransferResult(String hash, String height) {
        PropertyRecord record = new PropertyRecord();
        record.setHash(hash);
        record.setAddr(coinsChoosed.getAddr());
        record.setDecimals(coinsChoosed.getDecimals());
        record.setHeight(height);
        record.setFee(miner_cost);
        record.setUnit(coinsChoosed.getName());
        record.setName(coinsChoosed.getName());
        record.setTimestamp(System.currentTimeMillis() / 1000);
        record.setValue(valueString);
        record.setFrom(getAddrJson(coinsChoosed.getAddr(), valueString));
        record.setTo(getAddrJson(payeeAddress, valueString));
        record.setRemark(editRemark.getText().toString().trim());
        int state = 0;
        if (record.getAddr().equalsIgnoreCase(payeeAddress)) {
            state = 1;
        } else {
            state = -1;
        }
        record.setState(state);
        List<PropertyRecord> temps = new ArrayList<>();
        temps.add(record);
        DataSupport.saveAll(temps);
        startActivity(new Intent(mContext, TransactionDetailsActivity.class)
                .putExtra("coin_id", coinsChoosed.getCoin_id())
                .putExtra("currency", record));
        finish();
    }

    private String getAddrJson(String addr, String value) {
        List<PayAddressBean> list = new ArrayList<>();
        PayAddressBean bean = new PayAddressBean();
        bean.setAddress(addr);
        bean.setValue(value);
        list.add(bean);
        return new Gson().toJson(list);
    }

    private BigDecimal getRecommendFee(BigDecimal inputValue) {
        List<BtcUtxo> temps = DataSupport.where("coin_id = " + coinsChoosed.getCoin_id()).find(BtcUtxo.class);
        if (temps == null) {
            return null;
        }
        int index = 0;
        BigDecimal fee = null;
        BigDecimal realValue = new BigDecimal("0");
        for (BtcUtxo temp : temps) {
            int txSize = TxBuilder.estimationTxSize(index + 1, 2);
            fee = new BigDecimal(coinsChoosed.getGas_price()).multiply(new BigDecimal("" + txSize));
            if (realValue.compareTo(inputValue) == -1) {
                realValue = realValue.add(new BigDecimal(temp.getValue()));
                index++;
            } else {
                return fee;
            }
        }
        return fee;
    }

    private List<BtcUtxo> getUtxos(BigDecimal inputValue) {
        List<BtcUtxo> list = new ArrayList<>();
        List<BtcUtxo> temps = DataSupport.where("coin_id = " + coinsChoosed.getCoin_id()).find(BtcUtxo.class);
        if (temps == null) {
            return null;
        }
        int index = 0;
        BigDecimal realValue = new BigDecimal("0");
        for (BtcUtxo temp : temps) {
            //int txSize = TxBuilder.estimationTxSize(index + 1, 2);
            //fee = new BigDecimal(String.valueOf(new BigDecimal(coinsChoosed.getGas_price()).multiply(new BigDecimal("" + txSize))));
            if (realValue.compareTo(inputValue) == -1) {
                realValue = realValue.add(new BigDecimal(temp.getValue()));
                list.add(temp);
                //index++;
            } else {
                return list;
            }
        }
        return null;
    }

    private List<Out> getBtcOuts(List<BtcUtxo> list) {
        List<Out> outs = new ArrayList<>();
        for (BtcUtxo utxo : list) {
            byte[] txHash = Arrays.reverse(Utils.hexStringToByteArray(utxo.getHash()));
            int outSn = utxo.getN();
            byte[] outScript = ScriptBuilder.createOutputScript(payerAddress).getProgram();
            long outValue = Long.parseLong(utxo.getValue());
            outs.add(new Out(txHash, outSn, outScript, outValue));
        }
        return outs;
    }

    private Long getBtcChange(String totalValue, List<BtcUtxo> list) {
        long result = 0;

        for (BtcUtxo temp : list) {
            result += Long.parseLong(temp.getValue());
        }

        return Long.valueOf(result - Long.parseLong(totalValue));
    }

}
