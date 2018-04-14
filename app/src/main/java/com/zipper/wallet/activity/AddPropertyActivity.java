package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.PropertyAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.PropertyBean;

import java.util.ArrayList;
import java.util.List;

public class AddPropertyActivity extends BaseActivity {

    protected ImageView imgBack;
    protected SwipeMenuRecyclerView recyclerView;
    protected TextView textSubmit;
    private View headerView;

    private PropertyAdapter adapter;
    private List<PropertyBean> items;

    private boolean isHeaderViewClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_property);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headerView != null && isHeaderViewClicked) {
            headerView.setVisibility(View.VISIBLE);
            recyclerView.addHeaderView(headerView);
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (headerView != null && isHeaderViewClicked) {
            headerView.setVisibility(View.GONE);
            recyclerView.removeHeaderView(headerView);
        }
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(v -> finish());
        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        textSubmit = (TextView) findViewById(R.id.text_submit);
        textSubmit.setOnClickListener(v -> {
        });
    }

    private void initData() {
        items = new ArrayList<>();
        adapter = new PropertyAdapter(this, true, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        headerView = inflate(R.layout.layout_add_property_search);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(-1, dp2px(35)));
        recyclerView.addHeaderView(headerView);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.line_input))
                        .size(1)
                        .margin(dp2px(15), dp2px(15))
                        .build()
        );
        recyclerView.setSwipeItemClickListener((itemView, position) -> {
            items.get(position).setChecked(!items.get(position).isChecked());
            adapter.notifyDataSetChanged();
        });
        recyclerView.setAdapter(adapter);
        headerView.setOnClickListener(v -> {
            isHeaderViewClicked = true;
            startActivityForResult(new Intent(this, SearchPropertyActivity.class)
                    .putExtra("isShowCheckBox", true), 100);
        });
        testData();
    }

    private void testData() {
        String url = "http://img.mp.sohu.com/q_mini,c_zoom,w_640/upload/20170625/f76be47471c14f5ca6df64b94d02f648_th.jpg";
        PropertyBean bean = null;
        for (int i = 0; i < 10; i++) {
            bean = new PropertyBean();
            bean.setIcon(url);
            bean.setShortName("ETH");
            bean.setFullName("Ethereum Foundation");
            items.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    private List<PropertyBean> resultList = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            if (data != null) {
                resultList = (List<PropertyBean>) data.getSerializableExtra("search_list");
                if (resultList != null) {
                    List<PropertyBean> temp=new ArrayList<>();
                    for (PropertyBean item : items) {
                        if (item.isChecked()) {
                            temp.add(item);
                        }
                    }
                    resultList.addAll(temp);
                    items.removeAll(temp);
                    items.addAll(0, resultList);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
