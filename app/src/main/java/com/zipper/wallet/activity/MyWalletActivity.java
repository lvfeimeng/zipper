package com.zipper.wallet.activity;

import android.content.ContentValues;
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

import java.io.Serializable;
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
        loadData();
    }

    private void loadData() {
        List<CoinInfo> list = DataSupport.findAll(CoinInfo.class);
        for (CoinInfo info : list) {
            Map<String, String> map = new HashMap<>();
//            if ("btc".equalsIgnoreCase(info.getAddr_algorithm())) {
//                info.setAddr("159FTr7Gjs2Qbj4Q5q29cvmchhqymQA7of");
//            } else if ("eth".equalsIgnoreCase(info.getAddr_algorithm())) {
//                info.setAddr("0xea674fdde714fd979de3edf0f56aa9716b898ec8");
//            }
            map.put("address", info.getAddr());
            presenter.getCoinBalance(info.getId(), new Gson().toJson(map));
        }
        items.addAll(list);
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
                    .putExtra("id", bean.getId())
                    .putExtra("full_address", address)
                    .putExtra("address", bean.getAddr())
                    .putExtra("deciamls", bean.getDecimals())
                    .putExtra("amount", bean.getAmount())
                    .putExtra("name", bean.getName())
                    .putExtra("full_name", bean.getFull_name()));
        });
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

        layoutTradingRecord.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionActivity.class));
            //drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    private void setWalletName() {
        try {
            List<WalletInfo> list = new ArrayList<>();
            SqliteUtils.openDataBase(this);
            List<Map> maps = SqliteUtils.selecte("walletinfo");
            for (Map map : maps) {
                list.add(new WalletInfo(map));
            }
            WalletInfo walletInfo = list.get(0);
            walletId = walletInfo.getId();
            //WalletInfo walletInfo = DataSupport.find(WalletInfo.class, walletId);
            if (!TextUtils.isEmpty(walletInfo.getName()) && !"null".equalsIgnoreCase(walletInfo.getName())) {
                textWalletName.setText(walletInfo.getName());
                textName.setText(walletInfo.getName());
            } else {
                textWalletName.setText("我的钱包");
                textName.setText("我的钱包");
            }
            address = walletInfo.getAddress();
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
                startActivity(new Intent(this, SwitchAccountActivity.class));
                break;
            case R.id.text_collect_bill:
            case R.id.img_qr_code:
                startActivity(new Intent(this, PayeeAddressActivity.class)
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
    public void doSuccess(int id, Object obj) {
        if (obj == null) {
            //getLocalData();
            return;
        }
        isResponse = true;
        CoinBalance balance = (CoinBalance) obj;
        if (balance == null) {
            return;
        }
        balance.save();
        for (CoinInfo item : items) {
            if (id == item.getId()) {
                item.setAmount(getFormatData(balance.getAmount(), item.getDecimals()));
                ContentValues values = new ContentValues();
                values.put("amount", item.getAmount());
                DataSupport.update(CoinInfo.class, values, item.getId());
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
        items.clear();
        List<CoinInfo> list = DataSupport.findAll(CoinInfo.class);
        if (list != null) {
            items.addAll(list);
        }
        adapter.notifyDataSetChanged();
    }

    private String getFormatData(String amount, String decimals) {
        try {
            if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals) || "null".equalsIgnoreCase(amount) || "null".equalsIgnoreCase(decimals)) {
                return "0";
            }
            double result = Double.parseDouble(amount) / Double.parseDouble(decimals);
            return new DecimalFormat("0.00000000").format(result);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

}
