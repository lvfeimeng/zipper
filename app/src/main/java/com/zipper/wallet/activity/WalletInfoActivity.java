package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.dialog.DeleteWalletDialog;

public class WalletInfoActivity extends BaseActivity {

    protected ImageView imgBack;
    protected TextView textRight;
    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected TextView textAddr;
    protected EditText editWalletName;
    protected TextView textModifyPassword;
    protected TextView textExportWallet;
    protected TextView textDeleteWallet;

    private DeleteWalletDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_wallet_info);
        initView();
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        textRight = (TextView) findViewById(R.id.text_right);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        textAddr = (TextView) findViewById(R.id.text_addr);
        editWalletName = (EditText) findViewById(R.id.edit_Wallet_name);
        textModifyPassword = (TextView) findViewById(R.id.text_modify_password);
        textExportWallet = (TextView) findViewById(R.id.text_export_wallet);
        textDeleteWallet = (TextView) findViewById(R.id.text_delete_wallet);

        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("钱包信息");
        imgBack.setOnClickListener(v -> finish());
        textRight.setOnClickListener(v -> rightClick());
        textModifyPassword.setOnClickListener(v -> {
            startActivity(new Intent(this,UpdatePasActivity.class));
        });
        textExportWallet.setOnClickListener(v -> {
        });
        textDeleteWallet.setOnClickListener(v -> deleteWallet());
    }

    private void deleteWallet() {
        if (dialog == null) {
            dialog = new DeleteWalletDialog(this);
            dialog.setCallback(() -> {
                toast("删除钱包");
            });
        }
        dialog.show();
    }

    private void rightClick() {
        if (editWalletName.isFocusable()) {//当前为可编辑状态
            textRight.setText("编辑");
        } else {//当前为不可编辑状态
            textRight.setText("保存");
            editWalletName.setFocusable(true);
            editWalletName.setFocusableInTouchMode(true);
            editWalletName.requestFocus();
            editWalletName.setSelection(editWalletName.length());
        }
    }
}
