package com.zipper.wallet.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.CoinsBean;
import com.zipper.wallet.dialog.SelectCoinsDialog;

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
    private List<CoinsBean> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_payee_address);
        initView();
        imgBack.setOnClickListener(v -> finish());
        imgShare.setOnClickListener(v -> toast("分享"));
        textFullName.setOnClickListener(v -> {
            requestList();
        });
        btnCopy.setOnClickListener(v->{
            copyToClipboard(this,textWalletAddress.getText().toString().trim());
            btnCopy.setEnabled(false);
            btnCopy.setText("已复制");
        });
    }

    private void requestList() {
        items = new ArrayList<>();

        if (coinsDialog == null) {
            coinsDialog = new SelectCoinsDialog(this, items);
            coinsDialog.setCallback(item -> {
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
