package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.utils.PreferencesUtils;

public class UpdatePasActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditOldPas, mEditNewPas, mEditRepeatPas;
    private Button mBtnCommit;
    private TextView mTextImmediatelyIn;
//    private ImageView mDisplayOrHide, mDisplayOrHide2;

    TextView txtPwdReWar, txtStrong;
    ImageView imgPwdSign;
    LinearLayout linSign, linWarining;

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
//        mDisplayOrHide = findViewById(R.id.image_display);
//        mDisplayOrHide2 = findViewById(R.id.image_display2);

        linWarining = (LinearLayout) findViewById(R.id.lin_warning);
        txtStrong = (TextView) findViewById(R.id.txt_strong);
        txtPwdReWar = (TextView) findViewById(R.id.txt_pwdre_warning);
        imgPwdSign = (ImageView) findViewById(R.id.img_pwd_sign);
        linSign = (LinearLayout) findViewById(R.id.lin_sign);

        mTextImmediatelyIn.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
//        mDisplayOrHide.setOnClickListener(this);
//        mDisplayOrHide2.setOnClickListener(this);

        addTextChangedListener(mEditOldPas);
        addTextChangedListener(mEditNewPas);
        addTextChangedListener(mEditRepeatPas);


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

//            case R.id.image_display:
//                updateDisplayOrHide(mEditOldPas, mDisplayOrHide);
//                break;
//
//            case R.id.image_display2:
//                updateDisplayOrHide(mEditNewPas, mDisplayOrHide2);
//                break;


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

    private void addTextChangedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText == mEditNewPas) {
                    handlePwdStrong(editable.toString().trim());
                }
                if (isNull(mEditOldPas) || isNull(mEditNewPas) || isNull(mEditRepeatPas)) {
                    mBtnCommit.setEnabled(false);
                }
                if (!isNull(mEditOldPas) && !isNull(mEditNewPas) && !isNull(mEditRepeatPas)) {
                    mBtnCommit.setEnabled(true);
                }

            }
        });
    }

    public void handlePwdStrong(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            linSign.setVisibility(View.INVISIBLE);
            txtStrong.setText("");
            linWarining.setVisibility(View.INVISIBLE);
        } else {
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

//    private void updateDisplayOrHide(EditText editText, ImageView imageView) {
//
//        if (isBoolean) {
//            //密码 TYPE_CLASS_TEXT 和 TYPE_TEXT_VARIATION_PASSWORD 必须一起使用
//            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            imageView.setImageResource(R.mipmap.iconfont_toggleoff);
//            isBoolean = false;
//        } else {
//            //明文
//            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            imageView.setImageResource(R.mipmap.iconfont_toggleon);
//            isBoolean = true;
//        }
//
//    }

}
