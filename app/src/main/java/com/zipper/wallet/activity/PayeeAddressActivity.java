package com.zipper.wallet.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.dialog.SelectCoinsDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class PayeeAddressActivity extends BaseActivity {

    protected ImageView imgBack;
    protected ImageView imgShare;
    protected TextView textShortName;
    protected TextView textFullName;
    protected ImageView imgQrCode;
    protected TextView textWalletAddress;
    protected Button btnCopy;

    private SelectCoinsDialog coinsDialog;
    private List<CoinInfo> items;

    private String full_address = "";

    private int coin_id = -1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (coin_id == -1) {
                    items.get(0).setChecked(true);
                    loadData(items.get(0));
                } else {
                    //CoinInfo coinInfo=DataSupport.find(CoinInfo.class,coin_id);
                    for (CoinInfo item : items) {
                        if (item.getCoin_id() == coin_id) {
                            item.setChecked(true);
                            loadData(item);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_payee_address);
        if (getIntent() != null) {
            coin_id = getIntent().getIntExtra("coin_id", -1);
            full_address = getIntent().getStringExtra("full_address");
        }

        initView();
        CoinInfo info = new CoinInfo();
        info.setCoin_id(-1);
        info.setName("全站钱包地址");
        info.setFull_name("全站钱包地址");
        info.setAddr("ZP" + full_address.toUpperCase());
        info.setAddr_algorithm("eth");

        imgBack.setOnClickListener(v -> finish());
        imgShare.setOnClickListener(v -> toast("分享"));
        textFullName.setOnClickListener(v -> {
            showCoinsDialog();
        });
        btnCopy.setOnClickListener(v -> {
            copyToClipboard(this, textWalletAddress.getText().toString().trim());
            btnCopy.setEnabled(false);
            btnCopy.setText("已复制");
        });

        new Thread() {
            @Override
            public void run() {
                items = DataSupport.findAll(CoinInfo.class);
                if (items == null) {
                    items = new ArrayList<>();
                    items.add(info);
                    loadData(info);
                } else {
                    items.add(0, info);
                    handler.sendEmptyMessage(100);
                }
            }
        }.start();
    }

    private void loadData(CoinInfo info) {
        btnCopy.setEnabled(true);
        textShortName.setText(info.getName().toUpperCase());
        textFullName.setText(info.getFull_name());
        btnCopy.setEnabled(true);
        btnCopy.setText("复制收款地址");
        String text=null;
        if ("btc".equalsIgnoreCase(info.getAddr_algorithm())) {
            text = info.getAddr();
        } else if ("eth".equalsIgnoreCase(info.getAddr_algorithm())) {
            text = info.getAddr().toUpperCase();
        }
        textWalletAddress.setText(text);

        //带logo二维码
        //CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //不带logo二维码
        Bitmap bitmap = CodeUtils.createImage(info.getAddr(), 200, 200, null);
        imgQrCode.setImageBitmap(bitmap);
    }

    private void showCoinsDialog() {
        if (coinsDialog == null) {
            if (items == null) {
                return;
            }
            coinsDialog = new SelectCoinsDialog(this, items);
            coinsDialog.setCallback(item -> {
                loadData(item);
                for (CoinInfo coinInfo : items) {
                    if (coinInfo == item) {
                        coinInfo.setChecked(true);
                    } else {
                        coinInfo.setChecked(false);
                    }
                }
                coinsDialog.dismiss();
            });
        }
        coinsDialog.show();
    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgShare = (ImageView) findViewById(R.id.img_share);
        textShortName = (TextView) findViewById(R.id.text_short_name);
        textFullName = (TextView) findViewById(R.id.text_full_name);
        imgQrCode = (ImageView) findViewById(R.id.img_qr_code);
        textWalletAddress = (TextView) findViewById(R.id.text_wallet_address);
        btnCopy = (Button) findViewById(R.id.btn_copy);
    }

}
