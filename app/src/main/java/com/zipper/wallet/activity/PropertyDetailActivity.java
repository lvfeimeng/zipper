package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.adapter.PropertyRecordAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.PropertyRecord;
import com.zipper.wallet.definecontrol.AppBarStateChangeListener;
import com.zipper.wallet.definecontrol.TestPopupWindow;

import net.bither.bitherj.utils.Utils;

import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;
import org.litepal.parser.LitePalConfig;

import java.io.Serializable;
import java.util.ArrayList;
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
    int id=0;
//    int type = 0;
    int skip = 0;
    int step = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_property_detail);
        //Utils.validBicoinAddress()
        if (getIntent() == null) {
            return;
        }
        id=getIntent().getIntExtra("id",0);
        deciamls = getIntent().getStringExtra("deciamls");
        amount = getIntent().getStringExtra("amount");
        address = getIntent().getStringExtra("address");
        name = getIntent().getStringExtra("name");
        full_name = getIntent().getStringExtra("full_name");
        full_address = getIntent().getStringExtra("full_address");

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

//        if ("eth".equalsIgnoreCase(name)) {
//            type = HomePresenter.TYPE_ETH_HISTORY;
//        } else if ("btc".equalsIgnoreCase(name)) {
//            type = HomePresenter.TYPE_BTC_HISTORY;
//        }
        request();
    }

    private void request() {
        String json = "";
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("skip", "" + (skip * step));
        map.put("step", "" + step);
        json = new Gson().toJson(map);
        presenter.getCoinHistory(id, json);
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
                    .putExtra("full_address", full_address));
        });
        imgSwitch.setOnClickListener(v -> {
            startActivity(new Intent(this, SwitchAccountActivity.class));
        });
        setSupportActionBar(toolbar);

        View view = inflate(R.layout.layout_record);
        TextView chooseView = view.findViewById(R.id.txt_choose);
        initPop(chooseView);
        recyclerView.addHeaderView(view);
        recyclerView.setSwipeItemClickListener((itemView, position) -> {
            startActivity(new Intent(mContext, TransactionDefailsActivity.class)
                    .putExtra("currency", (Serializable) (items.get(position))));
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            skip = 0;
            request();
            refreshlayout.finishRefresh(1000);
        });
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            skip++;
            request();
            refreshlayout.finishLoadmore(1000);
        });
        imgHome.setOnClickListener(v -> finish());
        textSubTitle = (TextView) findViewById(R.id.text_sub_title);
    }

    private void initPop(TextView chooseView) {
        TestPopupWindow pop = new TestPopupWindow(mContext);
        pop.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);
        });
        chooseView.setOnClickListener(v -> {
            pop.showAsDropDown(chooseView);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.6f;
            getWindow().setAttributes(lp);
        });
        if (pop.radioGroup != null) {
            pop.radioGroup.check(R.id.radio_all);
            pop.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                pop.dismiss();
                switch (checkedId) {
                    case R.id.radio_all:
                        chooseView.setText("全部");
                        break;
                    case R.id.radio_transfer:
                        chooseView.setText("转账");
                        break;
                    case R.id.radio_receive:
                        chooseView.setText("收款");
                        break;
                    case R.id.radio_going:
                        chooseView.setText("进行中");
                        break;
                    default:
                        break;
                }
            });
        }
    }


    @Override
    public void doSuccess(int id, Object obj) {
        if (obj == null) {
            return;
        }
        List<PropertyRecord> list = (List<PropertyRecord>) obj;
        for (PropertyRecord record : list) {
            record.setName(name);
            record.setAddr(address);
            record.setDeciamls(deciamls);
        }
        DataSupport.deleteAll(PropertyRecord.class, "addr = ?", address);
        DataSupport.saveAll(list);
        loadData(list);
    }

    private void loadData(List<PropertyRecord> list) {
        if (list == null) {
            return;
        }
        items.addAll(list);
        for (PropertyRecord item : items) {
            item.setUnit("" + name.toUpperCase());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doFailure() {
        //toast("网络连接失败，已从本地获取数据");
        items.clear();
        List<PropertyRecord> list = DataSupport.findAll(PropertyRecord.class);
        List<PropertyRecord> list2 = new ArrayList<>();
        for (PropertyRecord record : list) {
            if (address.equalsIgnoreCase(record.getAddr())) {
                list2.add(record);
            }
        }
        loadData(list2);
    }
}
