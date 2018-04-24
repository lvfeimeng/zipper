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
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.adapter.PropertyAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.CoinInfo;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddPropertyActivity extends BaseActivity implements HomeContract.View {

    protected ImageView imgBack;
    protected SwipeMenuRecyclerView recyclerView;
    protected TextView textSubmit;
    private View headerView;

    private PropertyAdapter adapter;
    private List<CoinInfo> items;
    private List<CoinInfo> ownList;

    private boolean isHeaderViewClicked = false;

    private HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_property);
        if (getIntent() != null) {
            ownList = (List<CoinInfo>) getIntent().getSerializableExtra("own_list");
        }
        initView();
        initData();
        presenter = new HomePresenter(this, this);
        presenter.getCoins();
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
            getUsableData();
        });
    }

    private void getUsableData() {
        List<CoinInfo> resultList = new ArrayList<>();
        for (CoinInfo item : items) {
            if (item.isChecked()) {
                resultList.add(item);
            }
        }
        if (ownList != null && ownList.size() > 0) {
            resultList.removeAll(ownList);
        }
        if (resultList.isEmpty()) {
            toast("该币种已经添加过了");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("result_list", (Serializable) resultList);
        setResult(RESULT_OK, intent);
        finish();
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
//        String url = "http://img.mp.sohu.com/q_mini,c_zoom,w_640/upload/20170625/f76be47471c14f5ca6df64b94d02f648_th.jpg";
//        CoinInfo bean = null;
//        for (int i = 0; i < 10; i++) {
//            bean = new CoinInfo();
//            bean.setIcon(url);
//            bean.setShortName("ETH");
//            bean.setFullName("Ethereum Foundation");
//            items.add(bean);
//        }
//        adapter.notifyDataSetChanged();
    }

    private List<CoinInfo> resultList = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            if (data != null) {
                resultList = (List<CoinInfo>) data.getSerializableExtra("search_list");
                if (resultList != null) {
                    List<CoinInfo> temp = new ArrayList<>();
                    for (CoinInfo item : items) {
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

    @Override
    public void doSuccess(int type, Object obj) {
        if (obj == null) {
            getLocalData();
            return;
        }
        List<CoinInfo> list = (List<CoinInfo>) obj;
        if (list != null) {
            items.addAll(list);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doFailure() {
        getLocalData();
    }

    private void getLocalData() {
        items.clear();
        List<CoinInfo> list = DataSupport.findAll(CoinInfo.class);
        if (list != null) {
            items.addAll(list);
        }
        adapter.notifyDataSetChanged();
    }
}
