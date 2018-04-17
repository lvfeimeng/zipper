package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.readystatesoftware.viewbadger.BadgeView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.WalletAdapter;
import com.zipper.wallet.base.ActivityManager;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.CoinsBean;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.SqliteUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyWalletActivity extends BaseActivity implements View.OnClickListener {

//    public static final int REQUEST_CODE = 1000;

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
    protected ImageView imgSwitch;
    protected TextView textName;

    //headerview控件
    protected TextView textWalletAddress;
    protected TextView textWalletName;
    protected TextView textBadger;
    protected RelativeLayout layoutTradingRecord;
    protected TextView textContacts;
    protected TextView textLanguage;
    protected LinearLayout layoutLanguage;
    protected CheckBox checkboxGesturePassword;

    private WalletAdapter adapter;
    private List<CoinsBean> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        if (getIntent()!=null) {
            if (getIntent().getBooleanExtra("isFromImportPage",false)) {
                ActivityManager.getInstance().finishActivity(StartActivity.class);
                ActivityManager.getInstance().finishActivity(ImportWalletActivity.class);
            }
        }
        initView();
        gesturePwdSetting();
    }

    private void gesturePwdSetting() {
        String pwd = PreferencesUtils.getString(mContext, KEY_HAND_PWD, PreferencesUtils.USER);
        Log.i(TAG,"HAND_PWD:"+pwd);
        if (pwd != null && !pwd.equals("")) {
            Intent intent = new Intent(mContext, UnlockActivity.class);
            intent.putExtra("mode", 1);
            startActivity(intent);
        }
    }

    private void initView() {
        textWallet = (TextView) findViewById(R.id.text_wallet);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        textName = (TextView) findViewById(R.id.text_name);
        textSwitchAccount = (TextView) findViewById(R.id.text_switch_account);
        textSwitchAccount.setOnClickListener(MyWalletActivity.this);
        textCollectBill = (TextView) findViewById(R.id.text_collect_bill);
        textCollectBill.setOnClickListener(MyWalletActivity.this);
        imgHome = (ImageView) findViewById(R.id.img_home);
        imgHome.setOnClickListener(MyWalletActivity.this);
        imgQrCode = (ImageView) findViewById(R.id.img_qr_code);
        imgQrCode.setOnClickListener(MyWalletActivity.this);
        imgSwitch = (ImageView) findViewById(R.id.img_switch);
        imgSwitch.setOnClickListener(MyWalletActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.line_input))
                        .size(1)
                        .margin(dp2px(15), dp2px(15))
                        .build()
        );
        items = new ArrayList<>();
        testData();
        adapter = new WalletAdapter(this, items);
        recyclerView.setAdapter(adapter);//new ConisAdapter(this,items)

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
                    imgSwitch.setVisibility(View.VISIBLE);
                } else {
                    imgQrCode.setVisibility(View.GONE);
                    imgSwitch.setVisibility(View.GONE);
                }
            }
        });

        initNavHeaderView();
    }

    private void initNavHeaderView() {
        View headerView = navView.getHeaderView(0);
        if (headerView == null) {
            return;
        }
        textWalletAddress = (TextView) headerView.findViewById(R.id.text_wallet_address);
        textWalletName = (TextView) headerView.findViewById(R.id.text_wallet_name);
        textBadger = (TextView) headerView.findViewById(R.id.text_badger);
        layoutTradingRecord = (RelativeLayout) headerView.findViewById(R.id.layout_trading_record);
        textContacts = (TextView) headerView.findViewById(R.id.text_contacts);
        textLanguage = (TextView) headerView.findViewById(R.id.text_language);
        layoutLanguage = (LinearLayout) headerView.findViewById(R.id.layout_language);
        checkboxGesturePassword = (CheckBox) headerView.findViewById(R.id.checkbox_gesture_password);
        layoutTradingRecord.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        textContacts.setOnClickListener(v -> {
            startActivity(new Intent(this, ContactsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        layoutLanguage.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, LanguageSettingActivity.class), 99);
            //drawerLayout.closeDrawer(GravityCompat.START);
        });
        checkboxGesturePassword.setOnClickListener((view) -> {
            String pwd = PreferencesUtils.getString(mContext, KEY_HAND_PWD, PreferencesUtils.USER);
            int mode = 0;
            if (!TextUtils.isEmpty(pwd)) {
                mode = 3;
            }
            startActivity(new Intent(this, UnlockActivity.class)
                    .putExtra("mode", mode));
            //drawerLayout.closeDrawer(GravityCompat.START);
        });

        BadgeView badge = new BadgeView(this, textBadger);
        badge.setText("1");
        badge.show();

        headerView.findViewById(R.id.img_setting)
                .setOnClickListener(
                        v -> startActivity(new Intent(this, WalletInfoActivity.class))
                );


        List<WalletInfo> list = new ArrayList<>();
        SqliteUtils.openDataBase(this);
        List<Map> maps = SqliteUtils.selecte("walletinfo");
        for (Map map : maps) {
            list.add(new WalletInfo(map));
        }
        WalletInfo walletInfo = list.get(0);
        //if (walletInfo.getName().equals(PreferencesUtils.getString(this, KEY_WALLET_NAME, PreferencesUtils.VISITOR)))
        if (!TextUtils.isEmpty(walletInfo.getName()) && !"null".equalsIgnoreCase(walletInfo.getName())) {
            textWalletName.setText(walletInfo.getName());
            textName.setText(walletInfo.getName());
        } else {
            textWalletName.setText("我的钱包");
            textName.setText("我的钱包");
        }
        if (!TextUtils.isEmpty(walletInfo.getAddress())) {
            textWallet.setText("zp"+walletInfo.getAddress());
            textWalletAddress.setText("zp"+walletInfo.getAddress());
        } else {
            textWallet.setText("");
            textWalletAddress.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(PreferencesUtils.getString(mContext, KEY_HAND_PWD, PreferencesUtils.USER))) {
            checkboxGesturePassword.setChecked(true);
        } else {
            checkboxGesturePassword.setChecked(false);
        }
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.nav_menu, menu);
//        menu.findItem(R.id.nav_gesture_password).setVisible(false);
//        menu.findItem(R.id.nav_trading_record).setVisible(false);
//        menu.findItem(R.id.nav_contacts).setVisible(false);
//        menu.findItem(R.id.nav_language_setting).setVisible(false);
//        return true;
//    }

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
        switch (view.getId()) {
            case R.id.img_home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.text_switch_account:
            case R.id.img_switch:
                startActivity(new Intent(this, SwitchAccountActivity.class));
                break;
            case R.id.text_collect_bill:
            case R.id.img_qr_code:
                startActivity(new Intent(this, PayeeAddressActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 99) {
            int type = data.getIntExtra("type", 0);
            String language = "";
            switch (type) {
                case 0:
                    language = "简体中文";
                    break;
                case 1:
                    language = "繁體中文";
                    break;
                case 2:
                    language = "English";
                    break;
                default:
                    break;
            }

            if (language != null) {
                textLanguage.setText(language);
            }
        }
    }

    public int getScollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

}
