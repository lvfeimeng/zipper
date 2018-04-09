package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.utils.KeyBoardUtils;
import com.zipper.wallet.utils.PreferencesUtils;

/**
 * Created by Administrator on 2018/3/29.
 */

public class CreateAccountActivity extends BaseActivity {

    EditText edName;
    Button btnCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_account);
        super.onCreate(savedInstanceState);
        edName = (EditText)findViewById(R.id.ed_name);
        btnCreate = (Button)findViewById(R.id.btn_next);
        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = edName.getText().toString();
                if(str.equals("")){
                    btnCreate.setEnabled(false);
                }else{
                    btnCreate.setEnabled(true);
                }

            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.putString(mContext,"wallet_name",edName.getText().toString(),PreferencesUtils.VISITOR);
                WalletBean.getWalletBean().setName(edName.getText().toString());
                    Intent intent = new Intent(mContext,CreatePwdAcitivty.class);
                    startActivity(intent);
                    finish();
            }
        });

        titlebar.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                KeyBoardUtils.closeKeybord(mContext);
            }
        });

    }
}
