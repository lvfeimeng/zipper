package com.zipper.wallet.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;

public class SearchCoinsActivity extends BaseActivity implements View.OnClickListener {

    protected ImageView imgBack;
    protected ImageView imgSearch;
    protected EditText editSearch;
    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_search_coins);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        } else if (view.getId() == R.id.img_search) {
            toast("搜索");
        }
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(SearchCoinsActivity.this);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(SearchCoinsActivity.this);
        editSearch = (EditText) findViewById(R.id.edit_search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }
}
