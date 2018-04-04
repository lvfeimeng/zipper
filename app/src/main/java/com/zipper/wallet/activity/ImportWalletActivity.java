package com.zipper.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.fragment.MnemonicWordFragment;
import com.zipper.wallet.fragment.ObserveFragment;
import com.zipper.wallet.fragment.OfficialWalletFragment;
import com.zipper.wallet.fragment.PrivateKeyFragment;
import com.zipper.wallet.utils.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class ImportWalletActivity extends BaseActivity {

    public static final int REQUEST_CODE = 1000;

    protected Toolbar toolbar;
    protected TabLayout tabLayout;
    protected ViewPager viewPager;

    private String[] tabs;

    private List<Fragment> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        initView();
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initViewPager();
        tabLayout.setupWithViewPager(viewPager);
        initTabLayout();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void initTabLayout() {
        tabs = new String[]{"助记词", "官方钱包", "私钥", "观察"};
        for (int i = 0; i < tabs.length; i++) {
            tabLayout.getTabAt(i).setText(tabs[i]);
        }
        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initViewPager() {
        list = new ArrayList<>();
        list.add(new MnemonicWordFragment());
        list.add(new OfficialWalletFragment());
        list.add(new PrivateKeyFragment());
        list.add(new ObserveFragment());
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.item_scan:
                scanCode();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("CheckResult")
    private void scanCode() {
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            Intent intent = new Intent(ImportWalletActivity.this, ScanQrCodeActivity.class);
                            startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            toast("相机权限被禁止，请先开启权限");
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    toast("解析结果:" + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    toast("解析二维码失败");
                }
            }
        }
    }

    private ImmersionBar mImmersionBar;
    @Override
    public void statusBarSetting() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(true)
                .statusBarColor(R.color.colorPrimary)
                .keyboardEnable(true)
                .init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }
}
