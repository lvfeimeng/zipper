package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.dialog.DeleteWalletDialog;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.mnemonic.MnemonicException;
import net.bither.bitherj.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private WalletInfo walletInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_wallet_info);
        initView();
        initData();
    }

    private void initData() {
        try {
            List<WalletInfo> list = new ArrayList<>();
            SqliteUtils.openDataBase(WalletInfoActivity.this);
            List<Map> maps = SqliteUtils.selecte("walletinfo");
            for (Map map : maps) {
                list.add(new WalletInfo(map));
            }
            walletInfo = list.get(0);
            if (walletInfo == null) {
                return;
            }
            if (!TextUtils.isEmpty(walletInfo.getName()) && !"null".equalsIgnoreCase(walletInfo.getName())) {
                editWalletName.setText(walletInfo.getName());
            } else {
                editWalletName.setText("我的钱包");
            }
            textAddr.setText("zp"+walletInfo.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            startActivity(new Intent(this, UpdatePasActivity.class));
        });
        textExportWallet.setOnClickListener(v -> {
            inputPwd();
//            startActivity(new Intent(this, ExportWalletActivity.class));
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
            editWalletName.setFocusable(false);
            editWalletName.setFocusableInTouchMode(false);
            editWalletName.setCursorVisible(false);
        } else {//当前为不可编辑状态
            textRight.setText("保存");
            editWalletName.setFocusable(true);
            editWalletName.setFocusableInTouchMode(true);
            editWalletName.requestFocus();
            editWalletName.setSelection(editWalletName.length());
            editWalletName.setCursorVisible(true);
        }
    }

    private void inputPwd() {
        showInputDialog("请输入密码", "", "", InputType.TYPE_TEXT_VARIATION_PASSWORD, new RuntHTTPApi.ResPonse() {

            @Override
            public void doSuccessThing(final Map<String, Object> param) {
                showProgressDialog("正在导出。。。");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String pwd = param.get(INPUT_TEXT).toString().trim();
                            byte[] bytes = new EncryptedData(walletInfo.getEsda_seed()).decrypt(pwd);
                            if (bytes == null) {
                                return;
                            }
                            String cleartext = Utils.bytesToHexString(bytes);
                            String ciphertext = new EncryptedData(bytes, pwd).toEncryptedString();
                            String mnemonicWord = getMnemonicWord(walletInfo, pwd);
                            runOnUiThread(() ->
                                    {
                                        startActivity(new Intent(WalletInfoActivity.this, ExportWalletActivity.class)
                                                .putExtra("mnemonicWord", mnemonicWord)
                                                .putExtra("cleartext", cleartext)
                                                .putExtra("ciphertext", ciphertext));
                                        hideProgressDialog();
                                        finish();
                                    }
                            );
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                hideProgressDialog();
                                toast("密码错误");
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


    private String getMnemonicWord(WalletInfo walletInfo, String pwd) throws MnemonicException.MnemonicLengthException {
        CreateAcountUtils.instance(this);
        byte[] mnem_bytes = new EncryptedData(walletInfo.getMnem_seed()).decrypt(pwd);
        CreateAcountUtils.instance(this);
        List<String> words = CreateAcountUtils.getMnemonicCode(mnem_bytes);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < words.size() - 1) {
                sb.append("   ");
            }
        }
        return sb.toString();
    }

}