package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.ContactAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.ContactDetailsBean;
import com.zipper.wallet.utils.MyLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends BaseActivity implements View.OnClickListener {

    //    protected ImageView imgBack;
    protected TextView textNew;
    protected RecyclerView recyclerView;
    protected Toolbar toolbar;
    private TextView mTextSpace;
    protected CollapsingToolbarLayout collapsingToolbar;

    private ContactAdapter adapter;
    private List<ContactDetailsBean> mList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_contacts);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        select();
    }

    private void initView() {
        textNew = (TextView) findViewById(R.id.text_new);
        textNew.setOnClickListener(ContactsActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mTextSpace = findViewById(R.id.text_space);
    }

    private void initData() {
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitle("联系人");
        toolbar.setNavigationOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(this)
                        .color(getColorById(R.color.line_input))
                        .size(dp2px(1))
                        .margin(dp2px(15), dp2px(15))
                        .build()
        );
        select();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_new) {
            startActivity(new Intent(this, AddContactActivity.class));
        }
    }

    public void select() {
        //搜索数据库全部数据
        mList = new ArrayList<>();
        List<ContactDetailsBean> list = DataSupport.findAll(ContactDetailsBean.class);
        if (list != null) {
            mList.addAll(list);
        } else {
            mTextSpace.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        adapter = new ContactAdapter(this, mList);
        MyLog.d(TAG,mList.toString());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

}
