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
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.readystatesoftware.viewbadger.BadgeView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.adapter.ConisAdapter;
import com.zipper.wallet.base.ActivityManager;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinBalance;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.definecontrol.AppBarStateChangeListener;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.ScreenUtils;
import com.zipper.wallet.utils.SqliteUtils;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWalletActivity extends BaseActivity implements View.OnClickListener, HomeContract.View {

    private static final String TAG = "MyWalletActivity";

//    public static final int REQUEST_CODE = 1000;

    protected TextView textWallet;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected AppBarLayout appBar;
    protected NavigationView navView;
    protected DrawerLayout drawerLayout;
    protected SwipeMenuRecyclerView recyclerView;
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

    private ConisAdapter adapter;
    private List<CoinInfo> items;

    private HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("isFromImportPage", false)) {
                ActivityManager.getInstance().finishActivity(StartActivity.class);
                ActivityManager.getInstance().finishActivity(ImportWalletActivity.class);
            }
        }
        initView();
        gesturePwdSetting();

        presenter = new HomePresenter(this, this);
        presenter.getCoins();
    }

    private void gesturePwdSetting() {
        String pwd = PreferencesUtils.getString(mContext, KEY_HAND_PWD, PreferencesUtils.USER);
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
        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
//        nestedScrollView = (NestedScrollView) findViewById(R.id.nest_scroll_view);

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
        View view = inflate(R.layout.layout_wallet_center);
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.dp2px(mContext, 40)));
        recyclerView.addHeaderView(view);

        view.findViewById(R.id.image_add).setOnClickListener(v ->
                mContext.startActivity(new Intent(mContext, AddPropertyActivity.class)));
        view.findViewById(R.id.text_search).setOnClickListener(v ->
                mContext.startActivity(new Intent(mContext, SearchPropertyActivity.class)
                        .putExtra("isShowCheckBox", false)));

        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.line_input))
                        .size(1)
                        .margin(dp2px(15), dp2px(15))
                        .build()
        );
        initSwipeSetting();
        items = new ArrayList<>();
        adapter = new ConisAdapter(this, items);
        recyclerView.setAdapter(adapter);
//        addScrollListener();
        initNavHeaderView();
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    imgQrCode.setVisibility(View.GONE);
                    imgSwitch.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    imgQrCode.setVisibility(View.VISIBLE);
                    imgSwitch.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    imgQrCode.setVisibility(View.GONE);
                    imgSwitch.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int distance = getScollYDistance();
                if (distance >= 1) {
                    imgQrCode.setVisibility(View.VISIBLE);
                    imgSwitch.setVisibility(View.VISIBLE);
                } else {
                    imgQrCode.setVisibility(View.GONE);
                    imgSwitch.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initSwipeSetting() {
        recyclerView.setItemViewSwipeEnabled(false);
        recyclerView.setLongPressDragEnabled(false);
        recyclerView.setSwipeMenuCreator((swipeLeftMenu, swipeRightMenu, viewType) -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
            deleteItem.setBackgroundColorResource(R.color.btn_delete);
            deleteItem.setText("移除");
            deleteItem.setTextSize(16);
            deleteItem.setTextColorResource(R.color.white);
            deleteItem.setHeight(-1);
            deleteItem.setWidth(ScreenUtils.dp2px(mContext, 80));
            swipeRightMenu.addMenuItem(deleteItem);
        });
        recyclerView.setSwipeMenuItemClickListener(menuBridge -> {
            menuBridge.closeMenu();
            //int direction=menuBridge.getDirection();
            int adapterPosition = menuBridge.getAdapterPosition();
            int menuPosition = menuBridge.getPosition();
            if (menuPosition == 0) {
                items.remove(adapterPosition);
                adapter.notifyDataSetChanged();
            }
        });
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
        textContacts.setOnClickListener(v -> {
            startActivity(new Intent(this, ContactsActivity.class));
            //drawerLayout.closeDrawer(GravityCompat.START);
        });
        layoutLanguage.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, LanguageSettingActivity.class), 99);
            //drawerLayout.closeDrawer(GravityCompat.START);
        });
        if (!TextUtils.isEmpty(PreferencesUtils.getString(mContext, "hand_pwd", PreferencesUtils.USER))) {
            checkboxGesturePassword.setChecked(true);
        } else {
            checkboxGesturePassword.setChecked(false);
        }
        checkboxGesturePassword.setOnClickListener(v -> {
            String pwd = PreferencesUtils.getString(mContext, "hand_pwd", PreferencesUtils.USER);
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
        try {
            if (!TextUtils.isEmpty(walletInfo.getName()) && !"null".equalsIgnoreCase(walletInfo.getName())) {
                textWalletName.setText(walletInfo.getName());
                textName.setText(walletInfo.getName());
            } else {
                textWalletName.setText("我的钱包");
                textName.setText("我的钱包");
            }
            String address = walletInfo.getAddress();
            if (!TextUtils.isEmpty(address)) {
                String result = "zp" + address.substring(0, 5) + "..." + address.substring(address.length() - 7);
                textWallet.setText(result);
                textWalletAddress.setText(result);
            } else {
                textWallet.setText("");
                textWalletAddress.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        layoutTradingRecord.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionActivity.class).putExtra("address",walletInfo.getAddress()));
            //drawerLayout.closeDrawer(GravityCompat.START);
        });
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

    private boolean isResponse = false;

    @Override
    public void doSuccess(int type, Object obj) {
        if (obj == null) {
            getLocalData();
            return;
        }
        isResponse = true;
        switch (type) {
            case HomePresenter.TYPE_GET_COINS:
                List<CoinInfo> list= (List<CoinInfo>) obj;
                if (list != null) {
                    items.addAll(list);
                }

                for (CoinInfo item : items) {
                    //CoinInfo info = DataSupport.find(CoinInfo.class, item.getId());
                    //item.setAddr(info.getAddr());
                    Map<String, String> map = new HashMap<>();
                    if ("btc".equalsIgnoreCase(item.getName())) {
                        map.put("address", "159FTr7Gjs2Qbj4Q5q29cvmchhqymQA7of");//info.getAddr());
                        item.setAddr("159FTr7Gjs2Qbj4Q5q29cvmchhqymQA7of");
                        presenter.getCoinBalance(HomePresenter.TYPE_GET_BTC_BALANCE, new Gson().toJson(map));
                    } else if ("eth".equalsIgnoreCase(item.getName())) {
                        map.put("address", "0xea674fdde714fd979de3edf0f56aa9716b898ec8");
                        item.setAddr("0xea674fdde714fd979de3edf0f56aa9716b898ec8");
                        presenter.getCoinBalance(HomePresenter.TYPE_GET_ETH_BALANCE, new Gson().toJson(map));
                    }
                }
                //adapter.notifyDataSetChanged();
                break;
            case HomePresenter.TYPE_GET_BTC_BALANCE:
                CoinBalance balance = (CoinBalance) obj;
                balance.save();
                DataSupport.deleteAll(CoinInfo.class, "name = ?", "btc");
                for (CoinInfo item : items) {
                    if ("btc".equalsIgnoreCase(item.getName())) {
                        item.setAmount(getFormatData(balance.getAmount(), item.getDecimals()));
                        item.save();
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case HomePresenter.TYPE_GET_ETH_BALANCE:
                CoinBalance balance2 = (CoinBalance) obj;
                balance2.save();
                DataSupport.deleteAll(CoinInfo.class, "name = ?", "eth");
                for (CoinInfo item : items) {
                    if ("eth".equalsIgnoreCase(item.getName())) {
                        item.setAmount(getFormatData(balance2.getAmount(), item.getDecimals()));
                        item.save();
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }

    @Override
    public void doFailure() {
        //toast("网络连接失败，已从本地获取数据");
        if (!isResponse) {
            getLocalData();
        }
    }

    private void getLocalData() {
        items.clear();
        List<CoinInfo> list = DataSupport.findAll(CoinInfo.class);
        if (list != null) {
            items.addAll(list);
        }
        adapter.notifyDataSetChanged();
    }

    private String getFormatData(String amount, String decimals) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals)||"null".equalsIgnoreCase(amount)||"null".equalsIgnoreCase(decimals)) {
            return "0";
        }
        double result = Double.parseDouble(amount) / Double.parseDouble(decimals);
        return new DecimalFormat("0.00000000").format(result);
    }

}
