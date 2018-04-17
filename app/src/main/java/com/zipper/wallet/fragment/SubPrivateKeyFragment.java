package com.zipper.wallet.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.activity.MyWalletActivity;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.base.BaseFragment;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.utils.Utils;

import java.math.BigInteger;

import static com.zipper.wallet.base.BaseActivity.KEY_IS_LOGIN;

/**
 * 明文私钥、密文私钥共用页面
 */
public class SubPrivateKeyFragment extends BaseFragment {

    private static final String TAG = "SubPrivateKeyFragment";

    protected View rootView;
    protected EditText editPrimaryKey;
    protected EditText editPassword;
    protected EditText editConfirmPassword;
    protected EditText editPasswordHint;
    protected CheckBox checkBox;
    protected TextView textAgreement;
    protected Button btnImport;
    protected TextView textPrimaryKey;
    protected EditText editPrimaryKeyPassword;
    protected LinearLayout layoutPrimaryKey;

    private int type = 0;//0---明文，1--密文

    private Context mContext;

    public SubPrivateKeyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sub_private_key, container, false);
        initView(rootView);
        mContext = getActivity();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type", 0);
            if (type == 0) {
                layoutPrimaryKey.setVisibility(View.GONE);
                editPrimaryKey.setHint("输入明文私钥");
            } else {
                layoutPrimaryKey.setVisibility(View.VISIBLE);
                editPrimaryKey.setHint("输入加密私钥");
            }
            addTextChangedListener(editPrimaryKey);
            addTextChangedListener(editPassword);
            addTextChangedListener(editConfirmPassword);
            if (type == 1) {
                addTextChangedListener(editPrimaryKeyPassword);
            }
        }
    }

    private void initView(View rootView) {
        editPrimaryKey = (EditText) rootView.findViewById(R.id.editPrimaryKey);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editPasswordHint = (EditText) rootView.findViewById(R.id.editPasswordHint);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        textAgreement = (TextView) rootView.findViewById(R.id.textAgreement);
        btnImport = (Button) rootView.findViewById(R.id.btnImport);
        textPrimaryKey = (TextView) rootView.findViewById(R.id.textPrimaryKey);
        editPrimaryKeyPassword = (EditText) rootView.findViewById(R.id.editPrimaryKeyPassword);
        layoutPrimaryKey = (LinearLayout) rootView.findViewById(R.id.layoutPrimaryKey);

        btnImport.setOnClickListener(v -> submit());
    }

    String key, keyPassword, password, confirmPassword, passwordHint;

    private void submit() {
        try {
            key = editPrimaryKey.getText().toString().trim();
            keyPassword = editPrimaryKeyPassword.getText().toString().trim();
            password = editPassword.getText().toString().trim();
            confirmPassword = editConfirmPassword.getText().toString().trim();
            passwordHint = editPasswordHint.getText().toString().trim();
//            //明文--->密文
//            String ciphertext=new EncryptedData(key.getBytes(),keyPassword).toEncryptedString();
//            //密文--->明文
//            byte[]  cleartext=new EncryptedData(key).decrypt(keyPassword);

            if (!checkBox.isChecked()) {
                Toast.makeText(getActivity(), "请同意服务及隐私条款", Toast.LENGTH_SHORT).show();
                return;
            }

            if (type == 0) {
                if (TextUtils.isEmpty(key) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getActivity(), "私钥及密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if (TextUtils.isEmpty(key) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(keyPassword)) {
                    Toast.makeText(getActivity(), "私钥及密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            if (type == 0) {//0---明文，1--密文
                generateWalletAddress(Utils.hexStringToByteArray(key));
            } else {
                byte[] bytes = null;
                try {
                     bytes = new EncryptedData(key).decrypt(keyPassword);
                }catch (Exception e){
                    toast("密钥或密码错误");
                    return;
                }
                generateWalletAddress(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTextChangedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editText == editPrimaryKey) {
                    key = s.toString().trim();
                } else if (editText == editPassword) {
                    password = s.toString().trim();
                } else if (editText == editConfirmPassword) {
                    confirmPassword = s.toString().trim();
                }
                if (type == 1) {
                    if (editText == editPrimaryKeyPassword) {
                        keyPassword = s.toString().trim();
                    }
                }
                if (type == 0) {
                    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                        btnImport.setEnabled(false);
                    } else {
                        btnImport.setEnabled(true);
                    }
                } else {
                    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(keyPassword)) {
                        btnImport.setEnabled(false);
                    } else {
                        btnImport.setEnabled(true);
                    }
                }
            }
        });
    }

    private void generateWalletAddress(byte[] bytes) {

        new Thread(){
            @Override
            public void run() {
                try {
                    CreateAcountUtils.instance(mContext);

                    BigInteger privKey = new BigInteger(1, bytes);
                    ECKey ecKey = new ECKey(privKey);

                    //DeterministicKey master =null;//生成根公私钥对象

                    String priKey = Utils.bytesToHexString(ecKey.getPrivKeyBytes());//根私钥
                    //String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥

                    String firstAddr = ecKey.toAddress1();
                    //String firstAddr = CreateAcountUtils.getAddress(CreateAcountUtils.getAccount(master).deriveSoftened(AbstractHD.PathType.EXTERNAL_ROOT_PATH.getValue()), 60);

                    WalletInfo walletInfo = new WalletInfo(mContext);
                    walletInfo.setName(PreferencesUtils.getString(mContext, BaseActivity.KEY_WALLET_NAME, PreferencesUtils.VISITOR));
                    walletInfo.setTip(PreferencesUtils.getString(mContext, BaseActivity.KEY_WALLET_PWD_TIP, PreferencesUtils.VISITOR));

                    walletInfo.setEsda_seed(new EncryptedData(bytes,password,false).toEncryptedString());
                    //walletInfo.setMnem_seed(Utils.bytesToHexString(MnemonicCode.instance().toEntropy(words)));
                    walletInfo.setAddress(firstAddr);
                    //walletInfo.setId(4);
                    SQLiteDatabase db = SqliteUtils.openDataBase(mContext);

                    ContentValues cValue = new ContentValues();
                    for (Object key : walletInfo.toMap().keySet()) {
                        if(key.toString().indexOf("id")==-1) {
                            cValue.put(key.toString(), walletInfo.toMap().get(key) + "");
                        }
                    }

                    db.insert("walletinfo", null, cValue);
                    Log.i(TAG, "钱包数据保存成功");

                    PreferencesUtils.putBoolean(mContext,KEY_IS_LOGIN,true,PreferencesUtils.USER);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), MyWalletActivity.class)
                                    .putExtra("isFromImportPage", true));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
