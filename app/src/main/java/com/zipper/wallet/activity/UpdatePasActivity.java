package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.utils.PreferencesUtils;

public class UpdatePasActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditOldPas, mEditNewPas, mEditRepeatPas;
    private Button mBtnCommit;
    private TextView mTextImmediatelyIn;
    private ImageView mDisplayOrHide, mDisplayOrHide2;

    private boolean isBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pas);
        initView();

    }

    private void initView() {

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEditOldPas = findViewById(R.id.editOldPassword);
        mEditNewPas = findViewById(R.id.editNewPassword);
        mEditRepeatPas = findViewById(R.id.editRepeatPassword);
        mBtnCommit = findViewById(R.id.btnCommit);
        mTextImmediatelyIn = findViewById(R.id.textImmediatelyIn);
        mDisplayOrHide = findViewById(R.id.image_display);
        mDisplayOrHide2 = findViewById(R.id.image_display2);

        mTextImmediatelyIn.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
        mDisplayOrHide.setOnClickListener(this);
        mDisplayOrHide2.setOnClickListener(this);

        mEditOldPas.addTextChangedListener(textWatcher);
        mEditNewPas.addTextChangedListener(textWatcher);
        mEditRepeatPas.addTextChangedListener(textWatcher);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCommit:
                comparePas();
                break;

            case R.id.textImmediatelyIn:
                startActivity(new Intent(mContext, ImportWalletActivity.class));
                break;

            case R.id.image_display:
                updateDisplayOrHide(mEditOldPas, mDisplayOrHide);
                break;

            case R.id.image_display2:
                updateDisplayOrHide(mEditNewPas, mDisplayOrHide2);
                break;


        }
    }

    private void comparePas() {

        String oldPas = mEditOldPas.getText().toString().trim();
        String newPas = mEditNewPas.getText().toString().trim();
        String repeatPas = mEditRepeatPas.getText().toString().trim();

        if (newPas.length() < 8) {
            showTipDialog("密码不少于8位字符", null);
            return;
        } else if (oldPas.equals(newPas)) {
            showTipDialog("新旧密码不能相同", null);
            return;
        } else if (!newPas.equals(repeatPas)) {
            showTipDialog("重复密码不一致", null);
            return;
        } else
            //简单条件全部符合之后所调用possInternet方法，更改密码
            possInternet(oldPas, newPas);

    }

    private void possInternet(String oldPas, String newPas) {

        String walletpwd = PreferencesUtils.getString(mContext, KEY_WALLET_PWD, PreferencesUtils.VISITOR);
        if (oldPas.equals(walletpwd)) {
            PreferencesUtils.putString(mContext, KEY_WALLET_PWD, newPas, PreferencesUtils.VISITOR);
            WalletBean.getWalletBean().setPwd(newPas);
            showTipDialog("更改成功", null);
            finish();
        } else {
            showTipDialog("原密码不正确", null);
            return;
        }

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
            if (isNull(mEditOldPas) || isNull(mEditNewPas) || isNull(mEditRepeatPas)) {
                mBtnCommit.setEnabled(false);
            }
            if (!isNull(mEditOldPas) && !isNull(mEditNewPas) && !isNull(mEditRepeatPas)) {
                mBtnCommit.setEnabled(true);
            }
        }
    };

    private void updateDisplayOrHide(EditText editText, ImageView imageView) {

        if (isBoolean) {
            //密码 TYPE_CLASS_TEXT 和 TYPE_TEXT_VARIATION_PASSWORD 必须一起使用
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.mipmap.iconfont_toggleoff);
            isBoolean = false;
        } else {
            //明文
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.mipmap.iconfont_toggleon);
            isBoolean = true;
        }

    }

}
