package com.zipper.wallet.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.zipper.wallet.R;
import com.zipper.wallet.adapter.SelectCoinsAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.CoinsBean2;

import java.util.ArrayList;
import java.util.List;

public class SelectCoinsActivity extends BaseActivity {

    protected ImageView imgBack;
    protected ImageView imgSearch;
    protected RecyclerView recyclerView;

    private List<CoinsBean2> list;
    private SelectCoinsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_select_coins);
        initView();
        initData();
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imgBack.setOnClickListener(v -> finish());
        imgSearch.setOnClickListener(v -> {
            toast("search");
        });
    }

    private void initData() {
        list = new ArrayList<>();
        adapter = new SelectCoinsAdapter(this, list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        testData();
    }

    private void testData() {
        for (int i = 0; i < 20; i++) {
            list.add(new CoinsBean2("" + i, "ETH", "100"));
        }
        adapter.notifyDataSetChanged();
    }

}
