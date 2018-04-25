package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.zipper.wallet.utils.AlgorithmUtils;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.PreferencesUtils;

public class CreatePwdAcitivty extends CreateActvity {

    EditText edPwd,edPwdRe,edTip;
    CheckBox checkBox;
    Button btnCreate;
    TextView txtPwdReWar,txtStrong,txtAgree;
    ImageView imgPwdSign;
    LinearLayout linWarining;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_pwd);
        super.onCreate(savedInstanceState);
        edPwd = (EditText)findViewById(R.id.ed_pwd);
        edPwdRe = (EditText)findViewById(R.id.ed_repeat);
        edTip = (EditText)findViewById(R.id.ed_pwd_tip);
        btnCreate = (Button)findViewById(R.id.btn_create);
        checkBox = (CheckBox)findViewById(R.id.check);

        edPwd.addTextChangedListener(pwdWatcher);
        edPwdRe.addTextChangedListener(textWatcher);

        linWarining = (LinearLayout) findViewById(R.id.lin_warning);
        txtStrong = (TextView)findViewById(R.id.txt_strong);
        txtPwdReWar = (TextView)findViewById(R.id.txt_pwdre_warning);
        imgPwdSign = (ImageView)findViewById(R.id.img_pwd_sign);
        txtAgree = (TextView)findViewById(R.id.txt_agreement);
        txtAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext , WebBrowserActivity.class);
                intent.putExtra(PARAMS_TITLE,"服务协议");
                intent.putExtra(PARAMS_URL,"file:///android_asset/agreement.html");
                startActivity(intent);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && !edPwd.getText().toString().trim().equals("") && !edPwd.getText().toString().trim().equals("")){
                    btnCreate.setEnabled(true);
                }else{
                    btnCreate.setEnabled(false);
                }
            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNull(edPwd)){
                    showTipDialog("请填写密码",null);
                    return;
                }
                if(isNull(edPwdRe)){
                    showTipDialog("请再填写一次密码",null);
                    return;
                }
                if(edPwd.getText().toString().trim().length()<8){
                    showTipDialog("密码不能小于8位数",null);
                    return;
                }
                if(!edPwd.getText().toString().trim().equals(edPwdRe.getText().toString().trim())){
                    showTipDialog("密码不一致",null);
                    return;
                }

                if(checkBox.isChecked()){
                    showProgressDialog(getString(R.string.creating));
                    PreferencesUtils.putString(mContext,KEY_WALLET_PWD,edPwd.getText().toString(),PreferencesUtils.VISITOR);
                    PreferencesUtils.putString(mContext,KEY_WALLET_PWD_TIP,edTip.getText().toString(),PreferencesUtils.VISITOR);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Intent intent = new Intent(mContext,BackUpAcitivty.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    },2000);
                    /*Intent intent = new Intent(mContext,BackUpAcitivty.class);
                    startActivity(intent);
                    finish();*/
                }else{
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
            setBtnCreateEnable();

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
            MyLog.i(TAG,"pwdWatcher "+edPwd.getText());

            String pwd = edPwd.getText().toString();
            switch (AlgorithmUtils.pwdLevel(pwd)){
                case -1:
                    txtStrong.setText("");
                    imgPwdSign.setImageResource(R.mipmap.pwd_n);
                    linWarining.setVisibility(View.INVISIBLE);
                    break;
                case 0 :
                case 1:
                    imgPwdSign.setImageResource(R.mipmap.pwd_low);
                    txtStrong.setTextColor(getResources().getColor(R.color.btn_delete));
                    ((View)txtStrong.getParent()).setVisibility(View.VISIBLE);
                    linWarining.setVisibility(View.VISIBLE);
                    txtStrong.setText("弱");
                    break;
                case 2:
                    imgPwdSign.setImageResource(R.mipmap.pwd_well);
                    txtStrong.setTextColor(getResources().getColor(R.color.text_link));
                    txtStrong.setText("一般");
                    linWarining.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    imgPwdSign.setImageResource(R.mipmap.pwd_good);
                    txtStrong.setTextColor(getResources().getColor(R.color.text_link));
                    txtStrong.setText("很好");
                    linWarining.setVisibility(View.INVISIBLE);
                    break;
            }

            setBtnCreateEnable();

        }
    };

    private void setBtnCreateEnable(){
        MyLog.i(TAG,String.format("edPwd:%s,edPwdRe:%s,check:%s",edPwd.getText(),edPwdRe.getText(),checkBox.isChecked()));
        if(edPwd.getText().toString().trim().equals("") || edPwdRe.getText().toString().trim().equals("") ||edPwd.getText().toString().trim().length()<8){
            btnCreate.setEnabled(false);
        }else{
            if(checkBox.isChecked()) {
                btnCreate.setEnabled(true);
            }
        }
    }

}
