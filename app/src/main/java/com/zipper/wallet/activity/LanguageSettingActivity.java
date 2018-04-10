package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

public class LanguageSettingActivity extends BaseActivity {

    protected ImageView imgBack;
    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_language_setting);
        initView();
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("语言设置");
        imgBack.setOnClickListener(v -> finish());
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Intent data=new Intent();
            int type=0;
            switch (checkedId) {
                case R.id.radio_simple:
                    type=0;
                    break;
                case R.id.radio_traditional:
                    type=1;
                    break;
                case R.id.radio_english:
                    type=2;
                    break;
                default:
                    break;
            }
            data.putExtra("type",type);
            setResult(RESULT_OK,data);
            finish();
        });
    }
}
