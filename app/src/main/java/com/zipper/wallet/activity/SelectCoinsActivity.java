package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.widget.ImageView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.SelectCoinsAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinInfo;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SelectCoinsActivity extends BaseActivity {

    protected ImageView imgBack;
    protected ImageView imgSearch;
    protected SwipeMenuRecyclerView recyclerView;

    private List<CoinInfo> list;
    private SelectCoinsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ActivityTransparent);
        super.setContentView(R.layout.activity_select_coins);
        initView();
        initData();
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        imgBack.setOnClickListener(v -> finish());
        imgSearch.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, SearchCoinsActivity.class), 111);
        });
    }

    private void initData() {
        list = DataSupport.findAll(CoinInfo.class);
        if (list == null) {
            list=new ArrayList<>();
        }
        adapter = new SelectCoinsAdapter(this, list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setSwipeItemClickListener((itemView, position) -> {
            Intent data = new Intent();
            data.putExtra("coin_type", list.get(position));
            setResult(RESULT_OK, data);
            finish();
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 111) {
            if (data != null) {
                CoinInfo item = (CoinInfo) data.getSerializableExtra("coin_type");
                Intent data2 = new Intent();
                data2.putExtra("coin_type", item);
                setResult(RESULT_OK, data2);
                finish();
            }
        }
    }
}
