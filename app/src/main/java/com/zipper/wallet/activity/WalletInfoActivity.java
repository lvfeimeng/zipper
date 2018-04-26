package com.zipper.wallet.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.ActivityManager;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.dialog.DeleteWalletDialog;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.utils.Utils;

import org.litepal.crud.DataSupport;

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
            textAddr.setText("zp" + walletInfo.getAddress());
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
            initData();
            inputPwd();
//            startActivity(new Intent(this, ExportWalletActivity.class));
        });
        textDeleteWallet.setOnClickListener(v -> deleteWallet());
    }

    private void deleteWallet() {

        showInputDialog("验证私钥密码", "", "Password","","取消","确认", InputType.TYPE_TEXT_VARIATION_PASSWORD, new RuntHTTPApi.ResPonse() {

            @Override
            public void doSuccessThing(Map<String, Object> param) {
                initData();
                showProgressDialog("正在验证。。。");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String pwd = param.get(INPUT_TEXT).toString().trim();
                            MyLog.i(TAG, walletInfo.getEsda_seed() + " " + pwd);
                            byte[] bytes = new EncryptedData(walletInfo.getEsda_seed()).decrypt(pwd);
                            if (bytes == null) {
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();

                                    if (dialog == null) {
                                        dialog = new DeleteWalletDialog(mContext);
                                        dialog.setCallback(() -> {
                                            SQLiteDatabase sqlDB = mContext.openOrCreateDatabase(SqliteUtils.DB, Context.MODE_PRIVATE, null);
                                            try {
                                                sqlDB.execSQL("drop table walletinfo");
                                            } catch (SQLiteException e) {
                                                e.printStackTrace();
                                            }
                                            SqliteUtils.test();
                                            PreferencesUtils.clearData(mContext, PreferencesUtils.USER);
                                            startActivity(new Intent(mContext, StartActivity.class));
                                            ActivityManager.getInstance().finishAllActivity();
                                            finish();
                                        });
                                    }
                                    dialog.show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
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

    private void rightClick() {
        if (editWalletName.isFocusable()) {//当前为可编辑状态
            textRight.setText("编辑");
            editWalletName.setFocusable(false);
            editWalletName.setFocusableInTouchMode(false);
            editWalletName.setCursorVisible(false);
            if (walletInfo != null) {
                ContentValues values = new ContentValues();
                values.put("name", editWalletName.getText().toString().trim());
                DataSupport.update(WalletInfo.class, values, walletInfo.getId());
            }
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
        showInputDialog("验证私钥密码", "", "Password","","取消","确认", InputType.TYPE_TEXT_VARIATION_PASSWORD, new RuntHTTPApi.ResPonse() {

            @Override
            public void doSuccessThing(final Map<String, Object> param) {
                showProgressDialog("正在导出。。。");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            String pwd = param.get(INPUT_TEXT).toString().trim();
                            MyLog.i(TAG, walletInfo.getEsda_seed() + " " + pwd);
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
                                        //finish();
                                    }
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
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


    private String getMnemonicWord(WalletInfo walletInfo, String pwd) throws Exception {
        CreateAcountUtils.instance(this);
        if (walletInfo.getMnem_seed() == null || walletInfo.getMnem_seed().trim().equals("") || walletInfo.getMnem_seed().trim().indexOf("null") > -1) {
            return "";
        }
        byte[] radomSeed = new EncryptedData(walletInfo.getMnem_seed()).decrypt(pwd);
        CreateAcountUtils.instance(this);
        List<String> words = CreateAcountUtils.getMnemonicCode(radomSeed);
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
