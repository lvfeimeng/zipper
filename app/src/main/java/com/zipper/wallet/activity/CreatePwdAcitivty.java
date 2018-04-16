package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.WebBrowserActivity;
import com.zipper.wallet.base.CreateActvity;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.utils.PreferencesUtils;

public class CreatePwdAcitivty extends CreateActvity {

    EditText edPwd, edPwdRe, edTip;
    CheckBox checkBox;
    Button btnCreate;
    TextView txtPwdReWar, txtStrong, txtAgree;
    ImageView imgPwdSign;
    LinearLayout linSign, linWarining;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_pwd);
        super.onCreate(savedInstanceState);
        edPwd = (EditText) findViewById(R.id.ed_pwd);
        edPwdRe = (EditText) findViewById(R.id.ed_repeat);
        edTip = (EditText) findViewById(R.id.ed_pwd_tip);
        btnCreate = (Button) findViewById(R.id.btn_create);
        checkBox = (CheckBox) findViewById(R.id.check);

        edPwd.addTextChangedListener(pwdWatcher);
        edPwdRe.addTextChangedListener(textWatcher);

        linWarining = (LinearLayout) findViewById(R.id.lin_warning);
        txtStrong = (TextView) findViewById(R.id.txt_strong);
        txtPwdReWar = (TextView) findViewById(R.id.txt_pwdre_warning);
        imgPwdSign = (ImageView) findViewById(R.id.img_pwd_sign);
        linSign = (LinearLayout) findViewById(R.id.lin_sign);
        txtAgree = (TextView) findViewById(R.id.txt_agreement);
        txtAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, WebBrowserActivity.class);
                intent.putExtra(PARAMS_TITLE, "服务协议");
                intent.putExtra(PARAMS_URL, "file:///android_asset/agreement.html");
                startActivity(intent);

                /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LinearLayout dialogView = (LinearLayout)getLayoutInflater().inflate(R.layout.layout_web, null);
                WebView webView = (WebView) dialogView.findViewById(R.id.web_view);
                dialogView.removeView(webView);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl("file:///android_asset/agreement.html");
                builder.setView(webView);
                builder.show();*/

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && !edPwd.getText().toString().trim().equals("") && !edPwd.getText().toString().trim().equals("")) {
                    btnCreate.setEnabled(true);
                } else {
                    btnCreate.setEnabled(false);
                }
            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edPwd.getText().length() < 8) {
                    showTipDialog("密码不少于8位", null);
                    return;
                }
                if (isNull(edPwd)) {
                    showTipDialog("请填写密码", null);
                    return;
                }
                if (isNull(edPwdRe)) {
                    showTipDialog("请再填写一次密码", null);
                    return;
                }
                if (!edPwd.getText().toString().trim().equals(edPwdRe.getText().toString().trim())) {
                    showTipDialog("密码不一致", null);
                    return;
                }

                if (checkBox.isChecked()) {
                    showProgressDialog(getString(R.string.creating));
                    PreferencesUtils.putString(mContext, KEY_WALLET_PWD, edPwd.getText().toString(), PreferencesUtils.VISITOR);
                    PreferencesUtils.putString(mContext, KEY_WALLET_PWD_TIP, edTip.getText().toString(), PreferencesUtils.VISITOR);
                    WalletBean.getWalletBean().setPwd(edPwd.getText().toString());
                    WalletBean.getWalletBean().setPwdTip(edTip.getText().toString());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Intent intent = new Intent(mContext, BackUpAcitivty.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }, 2000);
                    /*Intent intent = new Intent(mContext,BackUpAcitivty.class);
                    startActivity(intent);
                    finish();*/
                } else {
                    toast("需要同意用户协议哦！！！");
                }


            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (edPwd.getText().equals("") || edPwdRe.getText().equals("")) {
                btnCreate.setEnabled(false);
            } else {
                if (checkBox.isChecked()) {
                    btnCreate.setEnabled(true);
                }
            }

        }
    };

    TextWatcher pwdWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i(TAG, "pwdWatcher " + edPwd.getText());
            if (edPwd.getText().equals("") || edPwd.getText().toString().trim().length() == 0) {
                linSign.setVisibility(View.INVISIBLE);
                txtStrong.setText("");
                linWarining.setVisibility(View.INVISIBLE);
                btnCreate.setEnabled(false);
            } else {
                String pwd = edPwd.getText().toString();
                boolean flag = false;
                try {
                    Integer.parseInt(pwd);
                } catch (Exception e) {
                    flag = true;
                }


                boolean isHigh = false;
                boolean hasUp = false;
                boolean hasLow = false;
                for (int i = 0; i < pwd.length(); i++) {
                    int chars = (int) pwd.toCharArray()[i];
                    if (chars > 64 && chars < 91) {
                        hasUp = true;
                    } else if (chars > 96 && chars < 123) {
                        hasLow = true;
                    } else if (chars > 9) {
                        isHigh = true;
                    }
                }

                Log.i(TAG, "pwdWatcher isHigh:" + isHigh + " hasLow:" + hasLow + " hasUp:" + hasUp + " flag:" + flag + " pwd.length():" + (pwd.length()));

                if (pwd.length() > 7 && isHigh && hasLow && hasUp && flag) {
                    linSign.setVisibility(View.VISIBLE);
                    imgPwdSign.setImageResource(R.mipmap.pwd_good);
                    txtStrong.setTextColor(getResources().getColor(R.color.text_link));
                    txtStrong.setText("很好");
                    linWarining.setVisibility(View.INVISIBLE);
                } else if (pwd.length() > 7 && flag) {
                    Log.i(TAG, "pwdWatcher " + " 一般:" + (pwd.length() > 6 && flag));
                    linSign.setVisibility(View.VISIBLE);
                    imgPwdSign.setImageResource(R.mipmap.pwd_well);
                    txtStrong.setTextColor(getResources().getColor(R.color.text_link));
                    txtStrong.setText("一般");
                    linWarining.setVisibility(View.INVISIBLE);
                } else if (pwd.length() < 8 || !flag) {
                    linSign.setVisibility(View.VISIBLE);
                    imgPwdSign.setImageResource(R.mipmap.pwd_low);
                    txtStrong.setTextColor(getResources().getColor(R.color.btn_delete));
                    ((View) txtStrong.getParent()).setVisibility(View.VISIBLE);
                    linWarining.setVisibility(View.VISIBLE);
                    txtStrong.setText("弱");
                }
            }

        }
    };

}
