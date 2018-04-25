package com.zipper.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.ContactDetailsBean;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.RegularUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

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

    private TextView mTextViewDel;
    private String mName;
    private String mAddress;
    private String mPhone;
    private String mEmail;
    private String mRemark;
    private String mNameIntent;

    private List<EditText> mEditTextList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_contact);
        if (getIntent() != null) {
            mNameIntent = getIntent().getStringExtra("name");
        } else {
            return;
        }
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
        mTextViewDel = (TextView) findViewById(R.id.text_del);

        mEditTextList = new ArrayList<>();
        mEditTextList.add(editName);
        mEditTextList.add(editWalletAddress);
        mEditTextList.add(editPhone);
        mEditTextList.add(editEmail);
        mEditTextList.add(editRemark);


        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("新建联系人");
        toolbar.setNavigationOnClickListener(v -> finish());
        mTextViewDel.setOnClickListener(this);

        if (!TextUtils.isEmpty(mNameIntent)) {
            select(mNameIntent);
            isNoSelect(mEditTextList);
            imageScan.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_save) {
            if (textSave.getText().equals("修改")) {
                textSave.setText("保存");
                imageScan.setVisibility(View.VISIBLE);
                isSelect(mEditTextList);
//                update(mNameIntent);
            } else if (textSave.getText().equals("保存")) {
                if (!TextUtils.isEmpty(mNameIntent)) {
                    update(mNameIntent);
                } else
                    insert();
            }
        } else if (view.getId() == R.id.image_scan) {
            scanCode();
        } else if (view.getId() == R.id.text_del) {
            delete(mNameIntent);
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

    private void insert() {
        //插入数据
        if (isNull() == true) {
            MyLog.d(TAG, mName + "\n" + mAddress + "\n" + mPhone + "\n" + mEmail + "\n" + mRemark);
            ContactDetailsBean bean = new ContactDetailsBean();
            bean.setName(mName);
            bean.setAddress(mAddress);
            bean.setPhone(mPhone);
            bean.setEmail(mEmail);
            bean.setRemarks(mRemark);
            bean.save();
            if (bean.save()) {
                finish();
                toast("保存成功");
            } else {
                toast("保存失败");
            }
        }
    }

    private void delete(String name) {
        //删除数据
        DataSupport.deleteAll(ContactDetailsBean.class, "name = ? ", name);
        finish();
        toast("删除成功");
    }

    private void select(String name) {
        //根据联系人名字显示数据库中对应的内容
        List<ContactDetailsBean> list = DataSupport.where("name = ?", name).find(ContactDetailsBean.class);
        ContactDetailsBean bean = list.get(0);
        String name2 = bean.getName();
        String address = bean.getAddress();
        String phone = bean.getPhone();
        String email = bean.getEmail();
        String remarks = bean.getRemarks();

        editName.setText(name2);
        editWalletAddress.setText(address);
        editPhone.setText(phone);
        editEmail.setText(email);
        editRemark.setText(remarks);

        textSave.setText("修改");

        mTextViewDel.setVisibility(View.VISIBLE);
    }

    private void update(String name) {
        //根据联系人名字修改其内容
        if (isNull() == true) {
            ContentValues values = new ContentValues();
            values.put("name", mName);
            values.put("address", mAddress);
            values.put("phone", mPhone);
            values.put("email", mEmail);
            values.put("remarks", mRemark);
            DataSupport.updateAll(ContactDetailsBean.class, values, "name = ?", name);
            finish();
            toast("修改成功");
        }

    }

    //判断输入框是否为空
    public boolean isNull() {
        mName = editName.getText().toString().trim();
        mAddress = editWalletAddress.getText().toString().trim();
        mPhone = editPhone.getText().toString().trim();
        mEmail = editEmail.getText().toString().trim();
        mRemark = editRemark.getText().toString().trim();

        if (mName.equals("")) {
            toast("姓名不得为空");
            return false;
        }
        if (mAddress.equals("")) {
            toast("地址不得为空");
            return false;
        }
        if (mPhone.equals("")) {
            toast("号码不得为空");
            return false;
        }
        if (mEmail.equals("")) {
            toast("邮箱不得为空");
            return false;
        }
        if (mRemark.equals("")) {
            toast("备注不得为空");
            return false;
        }
        if (!RegularUtils.idEmail(mEmail)) {
            toast("邮箱错误，请重新输入！");
            return false;
        }

        return true;
    }

    //设置edittext不可编辑内容
    public void isNoSelect(List<EditText> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setFocusable(false);
            list.get(i).setFocusableInTouchMode(false);
            list.get(i).setCursorVisible(false);
        }
    }

    //设置edittext可以编辑内容
    public void isSelect(List<EditText> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setFocusable(true);
            list.get(i).setFocusableInTouchMode(true);
            list.get(i).requestFocus();
            list.get(i).setSelection(list.get(i).length());
            list.get(i).setCursorVisible(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE:
                //处理扫描结果（在界面上显示）
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    editWalletAddress.setText(result);
                    //toast("解析结果:" + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    toast("解析二维码失败");
                }
                break;
        }
    }
}
