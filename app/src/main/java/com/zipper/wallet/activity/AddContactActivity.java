package com.zipper.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

import io.reactivex.functions.Consumer;

public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1000;

    protected ImageView imageView;
    protected EditText editFirstName;
    protected EditText editLastName;
    protected EditText editWalletAddress;
    protected ImageView imageScan;
    protected EditText editPhone;
    protected EditText editEmail;
    protected EditText editRemark;
    protected ImageView imgBack;
    protected TextView textSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_contact);
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.image_view);
        editFirstName = (EditText) findViewById(R.id.edit_first_name);
        editLastName = (EditText) findViewById(R.id.edit_last_name);
        editWalletAddress = (EditText) findViewById(R.id.editWalletAddress);
        imageScan = (ImageView) findViewById(R.id.image_scan);
        imageScan.setOnClickListener(AddContactActivity.this);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editRemark = (EditText) findViewById(R.id.editRemark);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(AddContactActivity.this);
        textSave = (TextView) findViewById(R.id.text_save);
        textSave.setOnClickListener(AddContactActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        } else if (view.getId() == R.id.text_save) {
            toast("保存");
        }else  if (view.getId()==R.id.image_scan){
            scanCode();
        }
    }

    @SuppressLint("CheckResult")
    private void scanCode() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            Intent intent = new Intent(AddContactActivity.this, CaptureActivity.class);
                            startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            toast("相机权限被禁止，请先开启权限");
                        }
                    }
                });
    }
}
