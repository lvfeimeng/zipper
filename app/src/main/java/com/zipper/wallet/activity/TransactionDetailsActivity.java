package com.zipper.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.PayAddressBean;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.PropertyRecord;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.ether.EtherRawTransaction;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.NetworkUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.utils.Utils;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionDetailsActivity extends BaseActivity implements View.OnClickListener, HomeContract.View {

    protected TextView textAllTransfer;
    protected TextView textAllReceive;
    private ImageView mBack;
    private PropertyRecord record;
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

    private int CONFIRMED = 1;
    private int WAIT_CONFIRMED = 2;
    private int SEEDING = 3;
    private int FAIL = 4;

    String symbol = "";

    private HomePresenter presenter;
    private int coin_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_defails);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        coin_id = intent.getIntExtra("coin_id", 0);
        record = (PropertyRecord) intent.getSerializableExtra("currency");
        initView();
        presenter = new HomePresenter(this, this);
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

        textPayerAddress.setOnClickListener(v ->
                {
                    copyToClipboard(this, textPayerAddress.getText().toString().trim());
                    toast("已复制");
                }
        );
        textPayeeAddress.setOnClickListener(v ->
                {
                    copyToClipboard(this, textPayeeAddress.getText().toString().trim());
                    toast("已复制");
                }
        );
        textTransferId.setOnClickListener(v ->
                {
                    copyToClipboard(this, textTransferId.getText().toString().trim());
                    toast("已复制");
                }
        );
        try {
            if (record.getState() == 1) {
                symbol = "+";
            } else if (record.getState() == -1) {
                symbol = "-";
            }
            updateInfo();
            List<PayAddressBean> payerList = new Gson().fromJson(record.getFrom(),
                    new TypeToken<List<PayAddressBean>>() {
                    }.getType());
            PayAddressBean payerBean = payerList.get(0);
            textPayerAddress.setText(payerBean.getAddress());

            List<PayAddressBean> payeeList = new Gson().fromJson(record.getTo(),
                    new TypeToken<List<PayAddressBean>>() {
                    }.getType());
            PayAddressBean payeeBean = payeeList.get(0);
            textPayeeAddress.setText(payeeBean.getAddress());
            //mDetailsCurrency.setText(symbol + getAmount(record.getValue()) + " " + record.getName());
            textMinerCost.setText(getAmount(record.getFee()) + " " + record.getName());
            String remark = record.getRemark();
            if (TextUtils.isEmpty(remark)) {
                textRemark.setText("无");
            } else {
                textRemark.setText(remark);
            }
            textTransferId.setText(record.getHash());
            Date date = new Date();
            date.setTime(record.getTimestamp() * 1000);
            //+" "+sdf.getTimeZone().getDisplayName(Locale.ROOT)
            textTransferTime.setText(sdf.format(date));

            textAllTransfer = (TextView) findViewById(R.id.text_all_transfer);
            textAllReceive = (TextView) findViewById(R.id.text_all_receive);

            textAllTransfer.setOnClickListener(v -> {
                startActivity(new Intent(this, AddressListActivity.class)
                        .putExtra("type", 0)
                        .putExtra("json", record.getFrom())
                        .putExtra("decimals", record.getDecimals())
                        .putExtra("name", record.getName()));
            });
            textAllReceive.setOnClickListener(v -> {
                startActivity(new Intent(this, AddressListActivity.class)
                        .putExtra("type", 1)
                        .putExtra("json", record.getTo())
                        .putExtra("decimals", record.getDecimals())
                        .putExtra("name", record.getName()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateInfo() {
        int height = Integer.parseInt(record.getHeight());
        if (height > 0) {
            textBlock.setText(String.valueOf(height));
            updateState(CONFIRMED);
        } else if (height == 0) {
            updateState(WAIT_CONFIRMED);
        } else if (height == -1) {
            updateState(SEEDING);
        } else if (height == -2) {
            updateState(FAIL);
        }
    }

    private String getAmount(String amount) {
        BigDecimal decimal = new BigDecimal(amount).divide(new BigDecimal(record.getDecimals()), 8, BigDecimal.ROUND_HALF_UP);
        return decimal.toPlainString();
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z");

    @SuppressLint("ResourceAsColor")
    private void updateState(int state) {
        switch (state) {
            case 1:
                mTextState.setText("已确认");
                mTextState.setTextColor(R.color.color_button_blue);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
                mImgState.setImageResource(R.mipmap.transaction_ok);
                mDetailsCurrency.setText(symbol + getAmount(record.getValue()) + record.getName());
                textBlock.setVisibility(View.VISIBLE);
                break;
            case 2:
                mTextState.setText("待确认");
                mTextState.setTextColor(R.color.color_button_blue);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_ok_bg);
                mImgState.setImageResource(R.mipmap.transaction_ok);
                mDetailsCurrency.setText(symbol + getAmount(record.getValue()) + record.getName());
                textBlock.setVisibility(View.GONE);
                break;
            case 3:
                mTextState.setText("发送中");
                mTextState.setTextColor(R.color.text_minor);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_inprocess_bg);
                mImgState.setImageResource(R.mipmap.inprocess);
                mDetailsCurrency.setText(symbol + getAmount(record.getValue()) + record.getName());
                textBlock.setVisibility(View.GONE);
                break;
            case 4:
                //fuilurehints();
                mTextState.setText("发送失败");
                mTextState.setTextColor(R.color.text_minor);
                mLinerGradient.setBackgroundResource(R.drawable.wallet_fail_bg);
                mImgState.setImageResource(R.mipmap.error);
                mTextUpdate.setVisibility(View.VISIBLE);
                mTextUpdate.setText("重发");
                mDetailsCurrency.setText(symbol + getAmount(record.getValue()) + record.getName());
                textBlock.setVisibility(View.GONE);
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
                    inputPwd();
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

    String payeeAddress = null;
    CoinInfo coinInfo = null;

    private void inputPwd() {
        showInputDialog("验证密码", "", "Password", "", "取消", "确认", InputType.TYPE_TEXT_VARIATION_PASSWORD, new RuntHTTPApi.ResPonse() {

            @Override
            public void doSuccessThing(final Map<String, Object> param) {
                showProgressDialog("正在验证。。。");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            coinInfo = DataSupport.find(CoinInfo.class, coin_id);
                            String pwd = param.get(INPUT_TEXT).toString().trim();
                            BigInteger nonce = new BigInteger(coinInfo.getNonce());
                            BigInteger gasLimit = new BigInteger("21000");
                            BigInteger gasPrice = new BigDecimal(record.getFee()).divide(new BigDecimal(gasLimit)).toBigInteger();
                            List<PayAddressBean> addrList = new Gson().fromJson(record.getTo(), new TypeToken<List<PayAddressBean>>() {
                            }.getType());
                            if (addrList != null && addrList.get(0) != null) {
                                payeeAddress = addrList.get(0).getAddress();
                            } else {
                                payeeAddress = "";
                            }
                            BigInteger value = new BigDecimal(record.getValue()).toBigInteger();
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
                            presenter.sendTransaction(coin_id, json);
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
        try {
            String json = (String) obj;
            Log.d(TAG, "doSuccess: json=" + json);
            if (json == null) {
                return;
            }
            JSONObject jsonObject = new JSONObject(json);
            int errCode = jsonObject.optInt("errCode");
            if (errCode == 0) {
                toast("转账交易已提交");
                record.setHeight("-1");
                record.setHash(jsonObject.optString("data"));
                updateInfo();
                //handleTransferResult(jsonObject.optString("data"), "-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTransferResult(String hash, String height) {
        PropertyRecord record = new PropertyRecord();
        record.setHash(hash);
        record.setAddr(record.getAddr());
        record.setDecimals(record.getDecimals());
        record.setHeight(height);
        record.setFee(record.getFee());
        record.setUnit(record.getUnit());
        record.setName(record.getName());
        record.setTimestamp(System.currentTimeMillis() / 1000);
        record.setValue(record.getValue());
        record.setFrom(record.getFrom());
        record.setTo(record.getTo());
        record.setRemark(record.getRemark());
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
                .putExtra("coin_id", coin_id)
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

    //{"data":"0x4dde249e130619d21eb7b917797ddc6f55aa7ddae4fd41c2e1edc14c1f973f48","errCode":0,"hash":"849391b28d880c9fa97f05422d2d2673"}

    @Override
    public void doFailure() {
        hideProgressDialog();
        record.setHeight("-2");
        record.setHash("");
        updateInfo();
        //handleTransferResult("", "-2");
    }
}
