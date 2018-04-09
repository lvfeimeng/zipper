package com.zipper.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

import io.reactivex.functions.Consumer;

public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1000;

    protected ImageView imageScan;
    protected TextView textSave;
    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected EditText editName;
    protected EditText editWalletAddress;
    protected EditText editPhone;
    protected EditText editEmail;
    protected EditText editRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_contact);
        initView();
    }

    private void initView() {
        imageScan = (ImageView) findViewById(R.id.image_scan);
        imageScan.setOnClickListener(AddContactActivity.this);
        textSave = (TextView) findViewById(R.id.text_save);
        textSave.setOnClickListener(AddContactActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        editName = (EditText) findViewById(R.id.edit_name);
        editWalletAddress = (EditText) findViewById(R.id.edit_wallet_address);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editRemark = (EditText) findViewById(R.id.edit_remark);

        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("新建联系人");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_save) {
            toast("保存");
        } else if (view.getId() == R.id.image_scan) {
            scanCode();
        }
    }

    @SuppressLint("CheckResult")
    private void scanCode() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent = new Intent(AddContactActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        toast("相机权限被禁止，请先开启权限");
                    }
                });
    }
}
