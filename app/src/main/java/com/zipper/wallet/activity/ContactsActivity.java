package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.ContactAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.ContactBean;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends BaseActivity implements View.OnClickListener {

    protected ImageView imgBack;
    protected ImageView imgAdd;
    protected RecyclerView recyclerView;

    private ContactAdapter adapter;
    private List<ContactBean> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_contacts);
        initView();
        initData();
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(ContactsActivity.this);
        imgAdd = (ImageView) findViewById(R.id.img_add);
        imgAdd.setOnClickListener(ContactsActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    private void initData() {
        items = new ArrayList<>();
        adapter = new ContactAdapter(this, items);
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
        for (int i = 0; i < 10; i++) {
            items.add(new ContactBean(url, "AAA", "aaaaaaaaaaaaaaaa"));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        } else if (view.getId() == R.id.img_add) {
            startActivity(new Intent(this, AddContactActivity.class));
        }
    }
}
