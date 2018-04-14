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
import com.zipper.wallet.bean.CoinsBean2;

import java.util.ArrayList;
import java.util.List;

public class SearchCoinsActivity extends BaseActivity {

    protected EditText editSearch;
    protected SwipeMenuRecyclerView recyclerView;
    protected TextView textRight;
    protected LinearLayout layoutEmpty;
    protected FrameLayout frameLayout;

    private List<CoinsBean2> items;
    private SelectCoinsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_search_coins);
        initView();
        initData();
    }

    private void initData() {
        items = new ArrayList<>();
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
            Intent data = new Intent();
            data.putExtra("coin_type", items.get(position));
            setResult(RESULT_OK, data);
            finish();
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
                } else {
                    setPageBg(1);
                    requestData();
                }
            }
        });
    }

    private void requestData() {
        //根据请求结果显示
        items.clear();
        testData();
        if (items.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void testData() {
        for (int i = 0; i < 10; i++) {
            items.add(new CoinsBean2("" + i, "ETH", "100"));
        }
        adapter.notifyDataSetChanged();
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
