package com.zipper.wallet.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.adapter.ConisAdapter;
import com.zipper.wallet.base.ActivityManager;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.WalletBean;
import com.zipper.wallet.database.BtcUtxo;
import com.zipper.wallet.database.CoinBalance;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.definecontrol.AppBarStateChangeListener;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.NetworkUtils;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.ScreenUtils;
import com.zipper.wallet.utils.SqliteUtils;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.UpdateOrDeleteCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWalletActivity extends BaseActivity implements View.OnClickListener, HomeContract.View {

    private static final String TAG = "MyWalletActivity";

//    public static final int REQUEST_CODE = 1000;

    protected TextView textWallet;
    protected TextView textTitle;
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
    protected SmartRefreshLayout refreshLayout;

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
    private String address = "";

    private int type = 0;

    private int walletId = 0;

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
        //presenter.getCoins();
        getWalletInfo();
        loadData();
    }

    private void loadData() {
        List<CoinInfo> list = DataSupport.findAll(CoinInfo.class);
        if (list != null && list.size() > 0) {
            items.clear();
        }
        for (CoinInfo info : list) {
            if (info.isDefault()) {
                items.add(info);
                Map<String, String> map = new HashMap<>();
                map.put("address", info.getAddr());
                if (TextUtils.isEmpty(info.getToken_addr())) {
                    presenter.getCoinBalance(info.getCoin_id(), new Gson().toJson(map));
                } else {
                    map.put("token_address", info.getToken_addr());
                    presenter.getTokenCoinBalance(info.getCoin_id(), info.getToken_type(), new Gson().toJson(map));
                }
                if ("btc".equalsIgnoreCase(info.getAddr_algorithm())) {
                    presenter.getBtcUtxo(info.getCoin_id(), new Gson().toJson(map));
                }
            }
        }
        adapter.notifyDataSetChanged();
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
        textTitle = (TextView) findViewById(R.id.text_title);
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
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.dp2px(mContext, 60)));
        recyclerView.addHeaderView(view);

        view.findViewById(R.id.image_add).setOnClickListener(v ->
                startActivityForResult(new Intent(mContext, AddPropertyActivity.class)
                        .putExtra("own_list", (Serializable) items), 111));
        view.findViewById(R.id.text_search).setOnClickListener(v ->
                startActivity(new Intent(mContext, SearchCoinsActivity.class)
                        .putExtra("isFromHomePage", true)
                        .putExtra("list", (Serializable) items)
                        .putExtra("full_address", address)));

        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.line_input))
                        .size(1)
                        .margin(dp2px(15), dp2px(15))
                        .build()
        );
        recyclerView.setSwipeItemClickListener((itemView, position) -> {
            CoinInfo bean = items.get(position);
            startActivity(new Intent(this, PropertyDetailActivity.class)
                    .putExtra("item", bean)
                    .putExtra("full_address", address));
        });
        initSwipeSetting();
        items = new ArrayList<>();
        adapter = new ConisAdapter(this, items);
        recyclerView.setAdapter(adapter);
        initNavHeaderView();
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    imgQrCode.setVisibility(View.GONE);
                    imgSwitch.setVisibility(View.GONE);
                    textTitle.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    imgQrCode.setVisibility(View.VISIBLE);
                    imgSwitch.setVisibility(View.VISIBLE);
                    textTitle.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    imgQrCode.setVisibility(View.GONE);
                    imgSwitch.setVisibility(View.GONE);
                    textTitle.setVisibility(View.GONE);
                }
            }
        });

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadData();
            refreshlayout.finishRefresh(1000);
        });
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            refreshlayout.finishLoadmore(1000);
        });
    }

    private void initSwipeSetting() {
        recyclerView.setItemViewSwipeEnabled(false);
        recyclerView.setLongPressDragEnabled(false);
//        recyclerView.setSwipeMenuCreator((swipeLeftMenu, swipeRightMenu, viewType) -> {
//            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
//            deleteItem.setBackgroundColorResource(R.color.btn_delete);
//            deleteItem.setText("移除");
//            deleteItem.setTextSize(16);
//            deleteItem.setTextColorResource(R.color.white);
//            deleteItem.setHeight(-1);
//            deleteItem.setWidth(ScreenUtils.dp2px(mContext, 80));
//            swipeRightMenu.addMenuItem(deleteItem);
//        });
//        recyclerView.setSwipeMenuItemClickListener(menuBridge -> {
//            menuBridge.closeMenu();
//            //int direction=menuBridge.getDirection();
//            int adapterPosition = menuBridge.getAdapterPosition();
//            int menuPosition = menuBridge.getPosition();
//            if (menuPosition == 0) {
//                items.remove(adapterPosition);
//                adapter.notifyDataSetChanged();
//            }
//        });
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
                Toast.makeText(mContext, getString(R.string.success_del_hand_pwd), Toast.LENGTH_SHORT).show();
                PreferencesUtils.putString(mContext, KEY_HAND_PWD, "", PreferencesUtils.USER);
            } else
                startActivity(new Intent(this, UnlockActivity.class)
                        .putExtra("mode", mode));
            //drawerLayout.closeDrawer(GravityCompat.START);
        });

//        BadgeView badge = new BadgeView(this, textBadger);
//        badge.setText("1");
//        badge.show();

        headerView
                .findViewById(R.id.img_setting)
                .setOnClickListener(
                        v -> startActivity(new Intent(this, WalletInfoActivity.class))
                );

        layoutTradingRecord.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionActivity.class));
            //drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    WalletInfo walletInfo = null;

    private void getWalletInfo() {
        List<WalletInfo> list = new ArrayList<>();
        SqliteUtils.openDataBase(this);
        List<Map> maps = SqliteUtils.selecte("walletinfo");
        for (Map map : maps) {
            list.add(new WalletInfo(map));
        }
        walletInfo = list.get(0);
        walletId = walletInfo.getId();
        address = walletInfo.getAddress();
    }

    private void setWalletName() {
        try {
            getWalletInfo();
            //WalletInfo walletInfo = DataSupport.find(WalletInfo.class, walletId);
            if (!TextUtils.isEmpty(walletInfo.getName()) && !"null".equalsIgnoreCase(walletInfo.getName())) {
                textWalletName.setText(walletInfo.getName());
                textName.setText(walletInfo.getName());
            } else {
                MyLog.d(TAG, "名字获取为空");
                textWalletName.setText("我的钱包");
                textName.setText("我的钱包");
            }

            if (!TextUtils.isEmpty(address)) {
                String result = "zp" + address.substring(0, 5) + "..." + address.substring(address.length() - 7);
                textWallet.setText(result.toUpperCase());
                textWalletAddress.setText(result.toUpperCase());
                textTitle.setText(result.toUpperCase());
            } else {
                textWallet.setText("");
                textWalletAddress.setText("");
                textTitle.setText("");
            }
            WalletBean.setWalletBean(walletInfo.toMap());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWalletName();
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

                if (NetworkUtils.getNetworkType(this, (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)) == NetworkUtils.NetworkType.NONE) {
                    showTipDialog("没有网络连接", "是否开启网络设置", "取消", "去设置", new RuntHTTPApi.ResPonse() {
                        @Override
                        public void doSuccessThing(Map<String, Object> param) {
                            NetworkUtils.setNetwork(mContext);
                        }

                        @Override
                        public void doErrorThing(Map<String, Object> param) {
                        }
                    });
                } else if (!NetworkUtils.checkNetworkState(this)) {
                    toast("连接不到互联网，请稍后再试！！！");
                } else {
                    startActivity(new Intent(this, TransferAccountActivity.class));
                }
                break;
            case R.id.text_collect_bill:
            case R.id.img_qr_code:
                startActivity(new Intent(this, PayeeAddressActivity.class)
                        .putExtra("coin_id", -1)
                        .putExtra("full_address", address));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 99) {
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

                if (!TextUtils.isEmpty(language)) {
                    textLanguage.setText(language);
                }
            } else if (requestCode == 111) {
                if (data == null) {
                    return;
                }
                List<CoinInfo> list = (List<CoinInfo>) data.getSerializableExtra("result_list");
                if (list == null) {
                    return;
                }
                items.addAll(list);
                adapter.notifyDataSetChanged();
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
    public void doSuccess(int coin_id, Object obj) {
        if (obj == null) {
            return;
        }
        isResponse = true;
        if (obj instanceof CoinBalance) {
            CoinBalance balance = (CoinBalance) obj;
            saveBalanceData(coin_id, balance);
        } else if (obj instanceof List) {
            List<BtcUtxo> utxoList = (List<BtcUtxo>) obj;
            saveUtxoData(coin_id, utxoList);
        }
    }

    private void saveUtxoData(int coin_id, List<BtcUtxo> utxoList) {
        if (utxoList == null) {
            return;
        }
        CoinInfo coinInfo = getCoinInfo(coin_id);
        for (BtcUtxo utxo : utxoList) {
            utxo.setAddr(coinInfo.getAddr());
            utxo.setCoin_id(coin_id);
        }
        String sql = "CREATE TABLE IF NOT EXISTS btcutxo (id INTEGER PRIMARY KEY ,coin_id INTEGER ,n INTEGER,value TEXT,addr TEXT,hash TEXT,script TEXT);";
        SqliteUtils.execSQL(sql);
        if (DataSupport.count(BtcUtxo.class) > 0) {
            DataSupport.deleteAll(BtcUtxo.class, "coin_id =" + coin_id);
        }
        DataSupport.saveAll(utxoList);
    }

    private CoinInfo getCoinInfo(int coin_id) {
        for (CoinInfo item : items) {
            if (item.getCoin_id() == coin_id) {
                return item;
            }
        }
        return null;
    }

    private void saveBalanceData(int coin_id, CoinBalance balance) {
        if (balance == null) {
            getLocalData();
            return;
        }
        //balance.save();
        for (CoinInfo item : items) {
            if (coin_id == item.getCoin_id()) {
                item.setAmount(balance.getAmount());
                item.setGas_price(balance.getGas_price());
                item.setNonce(balance.getNonce());
                ContentValues values = new ContentValues();
                values.put("amount", item.getAmount());
                values.put("gas_price", item.getGas_price());
                values.put("nonce", item.getNonce());
                DataSupport.updateAll(CoinInfo.class, values, "coin_id = " + item.getCoin_id());
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doFailure() {
        //toast("网络连接失败，已从本地获取数据");
        if (!isResponse) {
            getLocalData();
        }
    }

    private void getLocalData() {
        List<CoinInfo> list = DataSupport.findAll(CoinInfo.class);
        if (list != null && list.size() > 0) {
            items.clear();
        }
        for (CoinInfo coinInfo : list) {
            if (coinInfo.isDefault()) {
                items.add(coinInfo);
            }
        }

        adapter.notifyDataSetChanged();
    }

}
