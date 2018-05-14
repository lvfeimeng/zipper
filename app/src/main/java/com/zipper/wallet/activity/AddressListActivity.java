package com.zipper.wallet.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.AddressListAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.PayAddressBean;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends BaseActivity {

    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected AppBarLayout appBar;
    protected SwipeMenuRecyclerView recyclerView;

    private int type = 0;
    private String title = null;
    private String json = null;
    private String decimals = null;
    private String name = null;

    private AddressListAdapter adapter = null;
    private List<PayAddressBean> items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_address_list);
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 0);
            json = getIntent().getStringExtra("json");
            decimals = getIntent().getStringExtra("decimals");
            name = getIntent().getStringExtra("name");
            if (type == 0) {
                title = "发款方";
            } else if (type == 1) {
                title = "收款方";
            }
        }
        initView();
        initData();
    }

    private void initData() {
        collapsingToolbar.setTitle(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .margin(dp2px(15), dp2px(15))
                        .size(dp2px(1))
                        .color(getColorById(R.color.bg_card))
                        .build()
        );

        items = new ArrayList<>();
        if (TextUtils.isEmpty(json)) {
            toast("未获取到地址信息");
            return;
        }
        List<PayAddressBean> list = new Gson().fromJson(json, new TypeToken<List<PayAddressBean>>() {
        }.getType());
        if (list == null) {
            return;
        }
        for (PayAddressBean bean : list) {
            bean.setDecimals(decimals);
            bean.setName(name);
        }
        items.addAll(list);
        adapter = new AddressListAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);

        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
