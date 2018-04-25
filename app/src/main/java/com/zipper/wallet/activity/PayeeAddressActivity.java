package com.zipper.wallet.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
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

    private HomePresenter presenter;

    private String full_address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_payee_address);
        if (getIntent() != null) {
            full_address = getIntent().getStringExtra("full_address");
        }

        initView();
        CoinInfo info = new CoinInfo();
        info.setName("全站钱包地址");
        info.setFull_name("全站钱包地址");
        info.setAddr("zp"+full_address);
        info.setChecked(true);
        loadData(info);

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
                } else {
                    items.add(0, info);
                }
            }
        }.start();
    }

    private void loadData(CoinInfo info) {
        textShortName.setText(info.getName().toUpperCase());
        textFullName.setText(info.getFull_name());
        btnCopy.setEnabled(true);
        btnCopy.setText("复制收款地址");
        textWalletAddress.setText(info.getAddr());

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

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager systemService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (systemService != null) {
            systemService.setPrimaryClip(ClipData.newPlainText("text", text));
        }
    }
}
