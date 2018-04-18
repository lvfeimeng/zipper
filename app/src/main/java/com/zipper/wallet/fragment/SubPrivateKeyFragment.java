package com.zipper.wallet.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
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
import com.zipper.wallet.activity.ImportWalletActivity;
import com.zipper.wallet.activity.MyWalletActivity;
import com.zipper.wallet.base.BaseFragment;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.utils.Utils;

import java.util.Map;

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
    final int SAVE_PRIVATE = 100;
    final int TRANSMIT_WORDS = 101;
    final int ERROR = 404;

    public SubPrivateKeyFragment() {
        // Required empty public constructor
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object obj = msg.obj;
            switch (msg.what) {
                case SAVE_PRIVATE:
                    break;
                case ERROR:

                    if(obj!=null){

                        toast(obj.toString());
                    }else{
                        toast("生成数据错误");
                    }


                    break;

                case TRANSMIT_WORDS:
                    ((ImportWalletActivity)getActivity()).hideProgressDialog();
                    PreferencesUtils.putBoolean(mContext,((ImportWalletActivity)getActivity()).KEY_IS_LOGIN,true,PreferencesUtils.USER);
                    PreferencesUtils.clearData(mContext,PreferencesUtils.VISITOR);
                    startActivity(new Intent(getActivity(), MyWalletActivity.class)
                            .putExtra("isFromImportPage", true));
                    break;

            }
        }
    };
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
                generateWalletAddress(key);
            } else {
                byte[] bytes = null;
                try {
                     bytes = new EncryptedData(key).decrypt(keyPassword);
                }catch (Exception e){
                    toast("密钥或密码错误");
                    return;
                }
                generateWalletAddress(Utils.bytesToHexString(bytes));
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

    private void generateWalletAddress(String prikey) {
        ((ImportWalletActivity)getActivity()).showProgressDialog("正在导入。。。");
        new Thread(){
            @Override
            public void run() {
                try {
                    CreateAcountUtils.instance(mContext);

                    DeterministicKey master = CreateAcountUtils.CreateRootKey(Utils.hexStringToByteArray(prikey));//生成根公私钥对象

                    CreateAcountUtils.saveCoins(master, new RuntHTTPApi.ResPonse() {
                        @Override
                        public void doSuccessThing(Map<String, Object> param) {
                            String firstAddr = param.get("firstAddr").toString();
                            CreateAcountUtils.saveWallet(null, prikey, firstAddr, new RuntHTTPApi.ResPonse() {
                                @Override
                                public void doSuccessThing(Map<String, Object> param) {


                                    MyLog.i(TAG,"mnemonicSeed :"+prikey);
                                    MyLog.i(TAG,"512PrivateKey:"+Utils.bytesToHexString(master.getPrivKeyBytes()));
                                    MyLog.i(TAG,"512publicKey:"+Utils.bytesToHexString(master.getPubKey()));
                                    MyLog.i(TAG,"512PrivateKey33:"+Utils.bytesToHexString(master.getPrivKeyBytes33()));
                                    MyLog.i(TAG,"512publicKeyhash:"+Utils.bytesToHexString(master.getPubKeyHash()));
                                    MyLog.i(TAG,"512publicKeyExtended:"+Utils.bytesToHexString(master.getPubKeyExtended()));
                                    MyLog.i(TAG,"firstAddr:"+firstAddr);
                                    Message msg = new Message();
                                    msg.what = TRANSMIT_WORDS;
                                    mHandler.sendMessage(msg);
                                }

                                @Override
                                public void doErrorThing(Map<String, Object> param) {

                                    Message msg = new Message();
                                    msg.what = ERROR;
                                    msg.obj = param.get("error");
                                    mHandler.sendMessage(msg);
                                }
                            });
                        }

                        @Override
                        public void doErrorThing(Map<String, Object> param) {
                            Message msg = new Message();
                            msg.what = ERROR;
                            msg.obj = param.get("error");
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
