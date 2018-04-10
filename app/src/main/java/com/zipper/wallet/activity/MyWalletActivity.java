package com.zipper.wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.WalletAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.CoinsBean;
import com.zipper.wallet.utils.SqliteUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MyWalletActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final int REQUEST_CODE = 1000;

    protected TextView textWallet;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected AppBarLayout appBar;
    protected NavigationView navView;
    protected DrawerLayout drawerLayout;
    protected RecyclerView recyclerView;
    protected TextView textSwitchAccount;
    protected TextView textCollectBill;
    protected ImageView imgHome;
    protected ImageView imgQrCode;
    protected ImageView imgScan;

    private WalletAdapter adapter;
    private List<CoinsBean> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        /*String pwd = PreferencesUtils.getString(mContext,KEY_HAND_PWD,PreferencesUtils.PROJECT);
        if(pwd !=null && !pwd.equals("")){
            Intent intent = new Intent(mContext, UnlockActivity.class);
            intent.putExtra("mode",1);
            startActivity(intent);
        }*/
        //PreferencesUtils.putBoolean(mContext,KEY_IS_LOGIN,false,PreferencesUtils.USER);
        initView();
    }

    private void testSqlite(){
        SqliteUtils.openDataBase(mContext);
    }

    private void initView() {
        textWallet = (TextView) findViewById(R.id.text_wallet);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        textSwitchAccount = (TextView) findViewById(R.id.text_switch_account);
        textSwitchAccount.setOnClickListener(MyWalletActivity.this);
        textCollectBill = (TextView) findViewById(R.id.text_collect_bill);
        textCollectBill.setOnClickListener(MyWalletActivity.this);
        imgHome = (ImageView) findViewById(R.id.img_home);
        imgHome.setOnClickListener(MyWalletActivity.this);
        imgQrCode = (ImageView) findViewById(R.id.img_qr_code);
        imgQrCode.setOnClickListener(MyWalletActivity.this);
        imgScan = (ImageView) findViewById(R.id.img_scan);
        imgScan.setOnClickListener(MyWalletActivity.this);

        navView.setNavigationItemSelectedListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.color_gray9))
                        .size(1)
                        .margin(dp2px(15), dp2px(15))
                        .build()
        );
        items = new ArrayList<>();
        testData();
        adapter = new WalletAdapter(this, items);
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setFocusable(false);
        recyclerView.smoothScrollToPosition(0);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int distance = getScollYDistance();
                if (distance >= dp2px(140)) {
                    imgQrCode.setVisibility(View.VISIBLE);
                    imgScan.setVisibility(View.VISIBLE);
                } else {
                    imgQrCode.setVisibility(View.GONE);
                    imgScan.setVisibility(View.GONE);
                }
            }
        });
    }

    private void testData() {
        String icon = "http://upload.news.cecb2b.com/2013/1211/1386729783853.jpg";
        items.add(new CoinsBean("1", "ETH", "AAAAAAAAAAA", icon, "9999.9999"));
        items.add(new CoinsBean("2", "BTC", "BBBBBBBBBB", icon, "20.3678"));
        items.add(new CoinsBean("3", "ZIP", "CCCCCCCCCC", icon, "1200000"));
        items.add(new CoinsBean("4", "ETH", "DDDDDDDDDD", icon, "789000"));
        items.add(new CoinsBean("5", "ZIP", "FFFFFFFFFF", icon, "2312"));
        items.add(new CoinsBean("6", "ETH", "GGGGGGGGGG", icon, "456"));
        items.add(new CoinsBean("7", "BTC", "HHHHHHHHHHH", icon, "90"));
        items.add(new CoinsBean("8", "ETH", "IIIIIIIIIIII", icon, "1234"));
        items.add(new CoinsBean("9", "ZIP", "JJJJJJJJJJ", icon, "567.890"));
        //adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wallet_manage) {

        } else if (id == R.id.nav_trading_record) {

        } else if (id == R.id.nav_contacts) {
            startActivity(new Intent(this, ContactsActivity.class));
        } else if (id == R.id.nav_setting) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        menu.findItem(R.id.nav_wallet_manage).setVisible(false);
        menu.findItem(R.id.nav_trading_record).setVisible(false);
        menu.findItem(R.id.nav_contacts).setVisible(false);
        menu.findItem(R.id.nav_setting).setVisible(false);
        return true;
    }

    private ImmersionBar mImmersionBar;

    @Override
    public void statusBarSetting() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(false)
                .statusBarColor(android.R.color.transparent)
                .keyboardEnable(true)
                .init();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        } else if (view.getId() == R.id.text_switch_account) {
            startActivity(new Intent(this, SwitchAccountActivity.class));
        } else if (view.getId() == R.id.text_collect_bill) {
            startActivity(new Intent(this, PayeeAddressActivity.class));
        } else if (view.getId() == R.id.img_qr_code) {
            startActivity(new Intent(this, PayeeAddressActivity.class));
        } else if (view.getId() == R.id.img_scan) {
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
                            //Intent intent = new Intent(MyWalletActivity.this, CaptureActivity.class);
                            Intent intent = new Intent(MyWalletActivity.this, ScanQrCodeActivity.class);
                            startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            toast("相机权限被禁止，请先开启权限");
                        }
                    }
                });
    }

    public int getScollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }
}
