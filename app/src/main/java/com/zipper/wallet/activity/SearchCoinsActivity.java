package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.zipper.wallet.adapter.PropertyAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.PropertyBean;
import com.zipper.wallet.utils.RuntListSeria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchCoinsActivity extends BaseActivity {

    protected EditText editSearch;
    protected SwipeMenuRecyclerView recyclerView;
    protected TextView textRight;
    protected LinearLayout layoutEmpty;
    protected FrameLayout frameLayout;

    private List<PropertyBean> items;
    private PropertyAdapter adapter;

    private boolean isShowCheckBox = false;

    private List<PropertyBean> searchList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_search_coins);
        if (getIntent() != null) {
            isShowCheckBox = getIntent().getBooleanExtra("isShowCheckBox", false);
        }
        initView();
        initData();
    }

    private void initData() {
        items = new ArrayList<>();
        adapter = new PropertyAdapter(this, isShowCheckBox, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.line_input))
                        .size(1)
                        .margin(dp2px(15), dp2px(15))
                        .build()
        );
        recyclerView.setSwipeItemClickListener((itemView, position) -> {
            if (isShowCheckBox) {
                items.get(position).setChecked(!items.get(position).isChecked());
                adapter.notifyDataSetChanged();
            } else {
                startActivity(new Intent(this, PropertyActvity.class));
            }
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
        textRight.setOnClickListener(v -> rightBtnClick());
    }

    private void rightBtnClick() {
        if (isShowCheckBox) {
            if (editSearch.length() == 0) {
                finish();
            } else {
                if (items.size() == 0) {
                    toast("未选择币种");
                } else {
                    //将数据添加到前一个页面AddPropertyActivity
                    if (searchList == null) {
                        searchList = new ArrayList<>();
                    } else {
                        searchList.clear();
                    }
                    for (PropertyBean item : items) {
                        if (item.isChecked()) {
                            searchList.add(item);
                        }
                    }
                    Intent data = new Intent();
                    data.putExtra("search_list", (Serializable) searchList );
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        } else {
            finish();
        }
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
                if (s.length() == 0) {
                    textRight.setText("取消");
                    setPageBg(0);
                    recyclerView.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.GONE);
                } else {
                    if (isShowCheckBox) {
                        textRight.setText("下一步");
                    } else {
                        textRight.setText("取消");
                    }
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
        String url = "http://img.mp.sohu.com/q_mini,c_zoom,w_640/upload/20170625/f76be47471c14f5ca6df64b94d02f648_th.jpg";
        PropertyBean bean = null;
        for (int i = 0; i < 5; i++) {
            bean = new PropertyBean();
            bean.setIcon(url);
            bean.setShortName("ETH02");
            bean.setFullName("Ethereum Foundation");
            items.add(bean);
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
