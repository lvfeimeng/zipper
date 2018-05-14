package com.zipper.wallet.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.adapter.PropertyRecordAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.PayAddressBean;
import com.zipper.wallet.database.CoinBalance;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.database.PropertyRecord;
import com.zipper.wallet.definecontrol.AppBarStateChangeListener;
import com.zipper.wallet.definecontrol.NoScrollTextView;
import com.zipper.wallet.definecontrol.TestPopupWindow;
import com.zipper.wallet.utils.NetworkUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyDetailActivity extends BaseActivity implements HomeContract.View {

    private static final String TAG = "PropertyDetailActivity";

    protected TextView txtName;
    protected TextView txtFullName;
    protected TextView txtCount;
    protected TextView txtAddr;
    protected ImageView imgHome;
    protected ImageView imgQrCode;
    protected ImageView imgSwitch;
    protected TextView textTitle;
    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected AppBarLayout appBar;
    protected SwipeMenuRecyclerView recyclerView;
    protected SmartRefreshLayout refreshLayout;
    public String deciamls = "0";
    public String amount = "0";
    public String address = "";
    protected TextView textSubTitle;
    private String name = "";
    private String full_name = "";
    private String full_address = "";

    private List<PropertyRecord> items = null;
    private PropertyRecordAdapter adapter;

    private HomePresenter presenter;
    int coin_id = 0;
    //    int type = 0;
    int start = 0;
    int count = 20;

    private CoinInfo coinInfo;

    public static final int TYPE_ALL = 0;
    public static final int TYPE_TRANSFER = 1;
    public static final int TYPE_RECEIVE = 2;
    public static final int TYPE_GOIND = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_property_detail);
        //Utils.validBicoinAddress()
        if (getIntent() == null) {
            return;
        }
        coinInfo = (CoinInfo) getIntent().getSerializableExtra("item");
        coin_id = coinInfo.getCoin_id();
        deciamls = coinInfo.getDecimals();
        address = coinInfo.getAddr();
        name = coinInfo.getName();
        full_name = coinInfo.getFull_name();
        full_address = getIntent().getStringExtra("full_address");
        amount = coinInfo.getAmount();
        if (TextUtils.isEmpty(amount)) {
            amount = "0.00000000";
        } else {
            BigDecimal decimal = new BigDecimal(amount).divide(new BigDecimal(deciamls), 8, BigDecimal.ROUND_HALF_UP);
            amount = decimal.toPlainString();
        }

        initView();
        try {
            if (!TextUtils.isEmpty(name)) {
                txtName.setText(name.toUpperCase());
                textTitle.setText(name.toUpperCase());
            }
            if (!TextUtils.isEmpty(full_name)) {
                txtFullName.setText(full_name);
                textSubTitle.setText(full_name);
            }
            if (!TextUtils.isEmpty(address)) {
                String addr = address.substring(0, 8) + "..." + address.substring(address.length() - 8, address.length());
                txtAddr.setText(addr);
            }
            if (!TextUtils.isEmpty(amount)) {
                txtCount.setText(amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        presenter = new HomePresenter(this, this);
        request();

//        if ("eth".equalsIgnoreCase(name)) {
//            type = HomePresenter.TYPE_ETH_HISTORY;
//        } else if ("btc".equalsIgnoreCase(name)) {
//            type = HomePresenter.TYPE_BTC_HISTORY;
//        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        request();
//    }

    private void request() {
        String json = "";
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("start", "" + (start * count));
        map.put("count", "" + count);
        json = new Gson().toJson(map);
//        runOnUiThread(()->showProgressDialog("加载中……"));
        presenter.getCoinHistory(coin_id, json);

        refreshCoinBalance();
    }

    private void refreshCoinBalance() {
        Map<String, String> map = new HashMap<>();
        map.put("address", coinInfo.getAddr());
        if (TextUtils.isEmpty(coinInfo.getToken_addr())) {
            presenter.getCoinBalance(coinInfo.getCoin_id(), new Gson().toJson(map));
        } else {
            map.put("token_address", coinInfo.getToken_addr());
            presenter.getTokenCoinBalance(coinInfo.getCoin_id(), coinInfo.getToken_type(), new Gson().toJson(map));
        }
    }

    private void initView() {
        txtName = (TextView) findViewById(R.id.txt_name);
        txtFullName = (TextView) findViewById(R.id.txt_full_name);
        txtCount = (TextView) findViewById(R.id.txt_count);
        txtAddr = (TextView) findViewById(R.id.txt_addr);
        imgHome = (ImageView) findViewById(R.id.img_home);
        imgQrCode = (ImageView) findViewById(R.id.img_qr_code);
        imgSwitch = (ImageView) findViewById(R.id.img_switch);
        textTitle = (TextView) findViewById(R.id.text_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        imgQrCode.setOnClickListener(v -> {
            startActivity(new Intent(this, PayeeAddressActivity.class)
                    .putExtra("coin_id", coin_id)
                    .putExtra("full_address", full_address));
        });
        imgSwitch.setOnClickListener(v -> {

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
                startActivity(new Intent(this, TransferAccountActivity.class)
                        .putExtra("item", coinInfo));
            }
        });
        setSupportActionBar(toolbar);

        View view = inflate(R.layout.layout_record);
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, dp2px(40)));
        NoScrollTextView chooseView = view.findViewById(R.id.txt_choose);
        initPop(chooseView);
        recyclerView.addHeaderView(view);
        recyclerView.setSwipeItemClickListener((itemView, position) -> {
            startActivity(new Intent(mContext, TransactionDetailsActivity.class)
                    .putExtra("coin_id", coin_id)
                    .putExtra("currency", items.get(position)));
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(getColorById(R.color.line_input))
                        .size(dp2px(1))
                        .margin(dp2px(24), dp2px(24))
                        .build()
        );
        items = new ArrayList<>();
        adapter = new PropertyRecordAdapter(this, items);
        recyclerView.setAdapter(adapter);

//      collapsingToolbar.setTitle("SMT");
//      collapsingToolbar.setExpandedTitleTextColor(ColorStateList.valueOf(getColorById(R.color.black)));
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    collapsingToolbar.setTitleEnabled(false);
                    textTitle.setVisibility(View.GONE);
                    textSubTitle.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    collapsingToolbar.setTitleEnabled(false);
                    textTitle.setVisibility(View.VISIBLE);
                    textSubTitle.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    collapsingToolbar.setTitleEnabled(false);
                    textTitle.setVisibility(View.VISIBLE);
                    textSubTitle.setVisibility(View.VISIBLE);
                }
            }
        });
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
//            runOnUiThread(()->showProgressDialog("加载中……"));
            items.clear();
            start = 0;
            request();
            refreshlayout.finishRefresh(2000);
        });
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
//            runOnUiThread(()->showProgressDialog("加载中……"));
            start++;
            request();
            refreshlayout.finishLoadmore(2000);
        });
        imgHome.setOnClickListener(v -> finish());
        textSubTitle = (TextView) findViewById(R.id.text_sub_title);
    }

    private void initPop(NoScrollTextView chooseView) {
        TestPopupWindow pop = new TestPopupWindow(mContext);
        pop.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);
        });
        chooseView.setOnClickListener(v -> {
            pop.showAsDropDown(chooseView, 0, dp2px(-8));
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.6f;
            getWindow().setAttributes(lp);
        });
        if (pop.radioGroup != null) {
            pop.radioGroup.check(R.id.radio_all);
            pop.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                pop.dismiss();
                which = TYPE_ALL;
                switch (checkedId) {
                    case R.id.radio_all:
                        which = TYPE_ALL;
                        chooseView.setText("全部");
                        break;
                    case R.id.radio_transfer:
                        which = TYPE_TRANSFER;
                        chooseView.setText("转账");
                        break;
                    case R.id.radio_receive:
                        which = TYPE_RECEIVE;
                        chooseView.setText("收款");
                        break;
                    case R.id.radio_going:
                        which = TYPE_GOIND;
                        chooseView.setText("进行中");
                        break;
                    default:
                        break;
                }
                items.clear();
                startThread();
            });
        }
    }

    public int blockHeight = 0;

    /**
     * timestamp : 1524204129
     * hash : 0xca62017fbab765506880ab41a54af2e4c8c0ccea8eee21a6b52c41aed103db19
     * from : 0xea674fdde714fd979de3edf0f56aa9716b898ec8
     * to : 0x19776d3be75e08de504a7f5b4f7c02f90d210aea
     * value : 50072814947893670
     * fee : 0
     * height : 5472635
     */
    @Override
    public void doSuccess(int coin_id, Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof List) {
            List<PropertyRecord> list = (List<PropertyRecord>) obj;
            if (list.size()>=50) {
                list=list.subList(0,50);
            }
            accountStateSetting(list);
            //Collections.sort();
//            items.clear();
//            adapter.notifyDataSetChanged();
            List<PropertyRecord> unsavedList = new ArrayList<>();
            for (PropertyRecord record : list) {
                record.setName(name);
                record.setAddr(address);
                record.setDecimals(deciamls);
                record.setCoin_id(coin_id);
                //record.saveOrUpdateAsync("")
                //String conditions = "hash = " + record.getHash();
//                DataSupport.isExist(PropertyRecord.class, conditions)
                long id = getRecordId(record.getHash());
                if (id != -1) {
                    ContentValues values = new ContentValues();
                    values.put("height", record.getHeight());
                    values.put("fee", record.getFee());
                    values.put("value", record.getValue());
                    values.put("timestamp", record.getTimestamp());
                    values.put("from", record.getFrom());
                    values.put("to", record.getTo());
                    values.put("name", record.getName());
                    values.put("addr", record.getAddr());
                    values.put("decimals", record.getDecimals());
                    values.put("coin_id", coin_id);
                    DataSupport.update(PropertyRecord.class, values, id);

                } else {
                    unsavedList.add(record);
                }
            }
            //DataSupport.deleteAll(PropertyRecord.class, "addr = ?", address);
            if (unsavedList != null && unsavedList.size() > 0) {
                DataSupport.saveAll(unsavedList);
            }
            startThread();
        } else if (obj instanceof String) {
            //{"data":{"height":41517},"errCode":0,"hash":"3c161dd82e4b4e51fc634c56267e6bd7"}
            Log.d(TAG, "doSuccess: result=" + obj);
            try {
                isRequestBlockChainInfo = false;
                JSONObject jsonObject = new JSONObject(obj.toString());
                JSONObject object = jsonObject.optJSONObject("data");
                if (object.optInt("errCode") == 0) {
                    blockHeight = object.optInt("height");
                    putString("blockHeight", String.valueOf(blockHeight));
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (obj instanceof CoinBalance) {
            CoinBalance balance = (CoinBalance) obj;
            amount = balance.getAmount();
            if (!TextUtils.isEmpty(amount) && !"null".equalsIgnoreCase(amount)) {
                BigDecimal decimal = new BigDecimal(amount).divide(new BigDecimal(deciamls), 8, BigDecimal.ROUND_HALF_UP);
                amount = decimal.toPlainString();
            }
        }
    }

    private long getRecordId(String hash) {
        if (TextUtils.isEmpty(hash)) {
            return -1;
        }
        List<PropertyRecord> list = DataSupport.findAll(PropertyRecord.class);
        for (PropertyRecord item : list) {
            if (hash.equalsIgnoreCase(item.getHash())) {
                return item.getId();
            } else {
                return -1;
            }
        }
        return -1;
    }

    private void accountStateSetting(List<PropertyRecord> list) {
        for (PropertyRecord item : list) {
            int state = getAccountState(item.getTo());
            item.setState(state);
        }
        //adapter.notifyDataSetChanged();
    }

    //转入1  转出-1
    private int getAccountState(String json) {
        int state = 0;
        List<PayAddressBean> list = new Gson().fromJson(json, new TypeToken<List<PayAddressBean>>() {
        }.getType());
        int times = 1;
        for (PayAddressBean bean : list) {
            if (address.equalsIgnoreCase(bean.getAddress())) {
                if (times == 1) {
                    state = 1;
                    times++;
                } else {
                    state = -1;
                }
            } else {
                state = -1;
            }
        }
        return state;
    }

    private void startThread() {
        loadData();
//        new MyThread().start();
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            loadData();
            Looper.loop();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgressDialog();
            adapter.notifyDataSetChanged();
            isRequestBlockChainInfo = true;
            presenter.getBlockChainInfo(coin_id);
        }
    };

    private int which = TYPE_ALL;

    private void loadData() {
        //this.which = which;
        String where = "addr" + " = " + "\'" + address + "\'" +
                " AND " +
                "name" + " = " + "\'" + name + "\'";
        List<PropertyRecord> list = DataSupport.where(where).order("timestamp desc").find(PropertyRecord.class);
        List<PropertyRecord> temps = new ArrayList<>();
        for (PropertyRecord record : list) {
            if (address.equalsIgnoreCase(record.getAddr())) {
                switch (which) {
                    case TYPE_ALL:
                        temps.add(record);
                        break;
                    case TYPE_TRANSFER:
                        if (record.getState() == -1) {
                            temps.add(record);
                        }
                        break;
                    case TYPE_RECEIVE:
                        if (record.getState() == 1) {
                            temps.add(record);
                        }
                        break;
                    case TYPE_GOIND:
                        if (TextUtils.isEmpty(record.getHeight()) || Integer.parseInt(record.getHeight()) < 0) {
                            temps.add(record);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (start == 0) {
            items.clear();
        }
        items.addAll(temps);
        handler.sendEmptyMessage(0);
    }

    private boolean isRequestBlockChainInfo = false;

    @Override
    public void doFailure() {
        //toast("网络连接失败，已从本地获取数据");
        if (isRequestBlockChainInfo) {
            isRequestBlockChainInfo = false;
            if (!TextUtils.isEmpty(getString("blockHeight"))) {
                blockHeight = Integer.parseInt(getString("blockHeight"));
            }
            return;
        }
        items.clear();
//        List<PropertyRecord> list = DataSupport.findAll(PropertyRecord.class);
        startThread();
    }
}
