package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.PropertyAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.PropertyBean;

import java.util.ArrayList;
import java.util.List;

public class AddPropertyActivity extends BaseActivity implements View.OnClickListener {

    protected ImageView imgBack;
    protected ImageView imgSearch;
    protected RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private List<PropertyBean> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_property);
        initView();
        initData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        } else if (view.getId() == R.id.img_search) {
            startActivity(new Intent(this,SearchCoinsActivity.class));
        }
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(AddPropertyActivity.this);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(AddPropertyActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void initData() {
        items = new ArrayList<>();
        adapter = new PropertyAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.color_gray9))
                        .size(1)
                        .margin(dp2px(15),dp2px(15))
                        .build()
        );
        recyclerView.setAdapter(adapter);

        testData();
    }

    private void testData() {
        String url = "http://img4.imgtn.bdimg.com/it/u=1373411777,3992091759&fm=27&gp=0.jpg";
        PropertyBean bean = null;
        for (int i = 0; i < 10; i++) {
            bean = new PropertyBean();
            bean.setIcon(url);
            bean.setShortName("ETH");
            bean.setFullName("Ethereum Foundation");
            if (i % 3 == 2) {
                bean.setOwn(true);
            } else {
                bean.setOwn(false);
            }
            items.add(bean);
        }
        adapter.notifyDataSetChanged();
    }
}
