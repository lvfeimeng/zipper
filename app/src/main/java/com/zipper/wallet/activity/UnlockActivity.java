package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.UnlockView;
import com.zipper.wallet.listenear.OnClickListenearAndDo;
import com.zipper.wallet.utils.PreferencesUtils;

/**
 * Created by Administrator on 2018/4/4.
 */

public class UnlockActivity extends BaseActivity {
    private UnlockView mUnlockView;
    private String pwd;
    private TextView txtTitle, txtTip, txtWarning;

    private int mode = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_lock);
        super.onCreate(savedInstanceState);
        mUnlockView = (UnlockView) findViewById(R.id.unlock);
        mode = getIntent().getIntExtra("mode", 0);
        txtTitle = (TextView) findViewById(R.id.txt_hand_title);
        txtTip = (TextView) findViewById(R.id.txt_tip);
        txtWarning = (TextView) findViewById(R.id.txt_warning);
        pwd = PreferencesUtils.getString(mContext,KEY_HAND_PWD,PreferencesUtils.USER);
        if(mode ==0 && pwd !=null && !pwd.equals("")){
            mode =1;
        }
        switch (mode) {
            case 0://创建
                txtTitle.setText(getString(R.string.create_hand_pwd));
                txtTip.setText(getString(R.string.input_hand_pwd));
                mUnlockView.setMode(UnlockView.CREATE_MODE);
                break;
            case 1://验证
                titlebar.setRightText("修改");
                titlebar.setRightOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, UnlockActivity.class);
                        intent.putExtra("mode", 2);
                        startActivity(intent);
                    }
                });
            case 2://修改
            case 3://删除
            default:
                txtTitle.setText(getString(R.string.input_hand_pwd));
                mUnlockView.setMode(UnlockView.CHECK_MODE);
                txtTip.setText("");
                break;
        }
        txtWarning.setText("");
        mUnlockView.setGestureListener(new UnlockView.CreateGestureListener() {
            @Override
            public void onGestureCreated(String result) {
                pwd = result;
                Toast.makeText(mContext, "Set Gesture Succeeded!" + pwd, Toast.LENGTH_SHORT).show();
                mUnlockView.setMode(UnlockView.CHECK_MODE);
                txtTitle.setText(getString(R.string.input_hand_pwd));
                txtTip.setText(getString(R.string.re_hand_pwd));
                txtTip.setOnClickListener(new OnClickListenearAndDo() {
                    @Override
                    public void doClick(View v) {
                        pwd = "";
                        mUnlockView.setMode(UnlockView.CREATE_MODE);
                        txtTip.setText("");
                    }
                });
            }
        });
        mUnlockView.setOnUnlockListener(new UnlockView.OnUnlockListener() {
            @Override
            public boolean isUnlockSuccess(String result) {
                if (result.equals(pwd)) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onSuccess() {
                switch (mode) {
                    case 0://创建
                        Toast.makeText(mContext, getString(R.string.set_hand_pwd), Toast.LENGTH_SHORT).show();
                        PreferencesUtils.putString(mContext,KEY_HAND_PWD,pwd,PreferencesUtils.USER);
                        finish();
                        break;
                    case 1://验证
                        Toast.makeText(mContext, getString(R.string.success_hand_pwd), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 2://修改
                        Toast.makeText(mContext, getString(R.string.success_hand_pwd), Toast.LENGTH_SHORT).show();
                        txtTitle.setText(getString(R.string.create_hand_pwd));
                        txtTip.setText(getString(R.string.input_hand_pwd));
                        mUnlockView.setMode(UnlockView.CREATE_MODE);
                        mode = 0;
                        break;
                    case 3://删除
                        Toast.makeText(mContext, getString(R.string.success_hand_pwd), Toast.LENGTH_SHORT).show();
                        PreferencesUtils.removeKey(mContext,KEY_HAND_PWD,PreferencesUtils.USER);
                        finish();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure() {
                switch (mode) {
                    case 0://
                        txtTitle.setText(getString(R.string.create_hand_pwd));
                        txtTip.setText(getString(R.string.input_hand_pwd));
                        mUnlockView.setMode(UnlockView.CREATE_MODE);
                        //txtTip.setText(getString(R.string.warning_hand_pwd));
                        Toast.makeText(mContext, getString(R.string.warning_hand_pwd) + " " + getString(R.string.re_hand_pwd), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(mContext, getString(R.string.warning_hand_pwd), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


    }

    @Override
    public void statusBarSetting() {
        setTransparentStatusBar();
    }
}
