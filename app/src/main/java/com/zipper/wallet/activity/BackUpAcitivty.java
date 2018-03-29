package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.animations.MyAnimations;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class BackUpAcitivty extends BaseActivity {

    LinearLayout linCreate,linBtn,linBack;
    TextView txtTop,txtSkip;
    Button btnBackup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        linCreate = (LinearLayout)findViewById(R.id.lin_creating);
        linBtn  = (LinearLayout)findViewById(R.id.lin_btn);
        linBack = (LinearLayout)findViewById(R.id.lin_backup);
        txtSkip = (TextView)findViewById(R.id.txt_skip);
        txtTop = (TextView)findViewById(R.id.txt_top);
        btnBackup = (Button)findViewById(R.id.btn_backup);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyAnimations.showAnimaRightToLeft(txtTop);
                        MyAnimations.showAnimaRightToLeft(linBack);
                        MyAnimations.showAnimaRightToLeft(linBtn);
                        MyAnimations.hideAnimaLeftToRight(linCreate);
                    }
                });
            }
        },5000);

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("请输入密码","","",0,new RuntHTTPApi.ResPonse(){

                    @Override
                    public void doSuccessThing(Map<String, Object> param) {
                        showProgressDialog("正在导出。。。");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressDialog();
                                        Intent intent = new Intent(mContext,MnemonicActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        },5000);
                    }

                    @Override
                    public void doErrorThing(Map<String, Object> param) {

                    }
                });
            }
        });

    }
}
