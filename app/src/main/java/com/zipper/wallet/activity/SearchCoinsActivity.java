package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.SelectCoinsAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchCoinsActivity extends BaseActivity {

    protected EditText editSearch;
    protected SwipeMenuRecyclerView recyclerView;
    protected TextView textRight;
    protected LinearLayout layoutEmpty;
    protected FrameLayout frameLayout;

    private List<CoinInfo> items;
    private SelectCoinsAdapter adapter;
    private String full_address = "";

    private List<CoinInfo> temps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_search_coins);
        if (getIntent() != null) {
            full_address = getIntent().getStringExtra("full_address");
            temps = (List<CoinInfo>) getIntent().getSerializableExtra("list");
        }
        initView();
        initData();
    }

    private void initData() {
        items = new ArrayList<>();
        if (temps != null) {
            items.addAll(temps);
        }
        adapter = new SelectCoinsAdapter(this, items);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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
                    .putExtra("full_address", full_address)
                    .putExtra("address", bean.getAddr())
                    .putExtra("deciamls", bean.getDecimals())
                    .putExtra("amount", bean.getAmount())
                    .putExtra("name", bean.getName())
                    .putExtra("full_name", bean.getFull_name()));
        });
        recyclerView.setAdapter(adapter);
    }

    private void initView() {
        editSearch = (EditText) findViewById(R.id.edit_search);
        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        textRight = (TextView) findViewById(R.id.text_right);
        layoutEmpty = (LinearLayout) findViewById(R.id.layout_empty);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        setPageBg(0);
        addSearchListener();
        textRight.setOnClickListener(v -> finish());
    }

    private void addSearchListener() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textRight.setText("取消");
                if (s.length() == 0) {
                    setPageBg(0);
                    recyclerView.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.GONE);
                    items.clear();
                    items.addAll(temps);
                } else {
                    setPageBg(1);
                    requestData(s.toString());
                }
            }
        });
    }

    private List<CoinInfo> result = null;

    private void requestData(String text) {
        //根据请求结果显示
        items.clear();
        result = new ArrayList<>();
        for (CoinInfo item : temps) {
            if (item.getName().contains(text.toLowerCase())||item.getName().contains(text.toUpperCase())) {
                result.add(item);
            }
        }
        items.addAll(result);
        adapter.notifyDataSetChanged();
        if (items.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setPageBg(int type) {
        if (type == 0) {
            getWindow().setBackgroundDrawableResource(R.color.transparent);
            frameLayout.setBackgroundColor(0x76000000);
        } else {
            getWindow().setBackgroundDrawableResource(R.color.white);
            frameLayout.setBackgroundColor(0xffffff);
        }
    }

}
