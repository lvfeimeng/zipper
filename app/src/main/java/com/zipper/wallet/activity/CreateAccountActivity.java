package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

/**
 * Created by Administrator on 2018/3/29.
 */

public class CreateAccountActivity extends BaseActivity {

    EditText edName,edPwd,edPwdRe,edTip;
    CheckBox checkBox;
    Button btnCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        edName = (EditText)findViewById(R.id.ed_name);
        edPwd = (EditText)findViewById(R.id.ed_pwd);
        edPwdRe = (EditText)findViewById(R.id.ed_repeat);
        edTip = (EditText)findViewById(R.id.ed_pwd_tip);
        btnCreate = (Button)findViewById(R.id.btn_create);
        checkBox = (CheckBox)findViewById(R.id.check);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNull(edName)){
                    showTipDialog("请填写钱包名称",null);
                    return;
                }
                if(isNull(edPwd)){
                    showTipDialog("请填写密码",null);
                    return;
                }
                if(isNull(edPwdRe)){
                    showTipDialog("请再填写一次密码",null);
                    return;
                }
                if(!edPwd.getText().toString().trim().equals(edPwdRe.getText().toString().trim())){
                    showTipDialog("密码不一致",null);
                    return;
                }

                if(checkBox.isChecked()){
                    Intent intent = new Intent(CreateAccountActivity.this,BackUpAcitivty.class);
                    startActivity(intent);
                    finish();
                }else{
                    toast("需要同意用户协议哦！！！");
                }
            }
        });
    }
}
