package com.zipper.wallet.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.adapter.TransactionHistoryAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.bean.TransactionBean;
import com.zipper.wallet.utils.NormalDecoration;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends BaseActivity {
    private RecyclerView mRecyclehistory;
    private Toolbar mToolbar;
    private List<TransactionBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        initView();
    }

    private void initView() {

        mRecyclehistory = findViewById(R.id.recycler_history);
        mToolbar = findViewById(R.id.toolbar_transaction);
        setSupportActionBar(mToolbar);
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mList = new ArrayList<>();
        testData();
        headView();
    }

    private void testData() {

        mList.add(new TransactionBean("ETH", "3月16日", "确认次数：333", "+ 1999.999 ETH"));
        mList.add(new TransactionBean("ETH", "3月16日", "确认次数：333", "+ 1999.999 ETH"));
        mList.add(new TransactionBean("ETH", "3月16日", "确认次数：333", "+ 1999.999 ETH"));
        mList.add(new TransactionBean("SMT", "2月06日", "确认次数：222", "+ 2999.999 SMT"));
        mList.add(new TransactionBean("SMT", "2月06日", "确认次数：222", "+ 2999.999 SMT"));
        mList.add(new TransactionBean("SMT", "2月06日", "确认次数：222", "- 2999.999 SMT"));
        mList.add(new TransactionBean("BTC", "1月26日", "确认次数：111", "- 3999.999 BTC"));
        mList.add(new TransactionBean("BTC", "1月26日", "确认次数：111", "+ 3999.999 BTC"));
        mList.add(new TransactionBean("BTC", "1月26日", "确认次数：111", "+ 3999.999 BTC"));
        mList.add(new TransactionBean("SMT", "12月06日", "确认次数：000", "- 4999.999 SMT"));
        mList.add(new TransactionBean("ZIP", "11月16日", "确认次数：111", "- 5999.999 ZIP"));

    }

    private void headView() {

        NormalDecoration decoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                String date = mList.get(pos).getDate();
                String subDate = date.substring(0,date.indexOf("月"));
                return subDate + "月";
            }
        };

        decoration.setOnDecorationHeadDraw(new NormalDecoration.OnDecorationHeadDraw() {
            @Override
            public View getHeaderView(final int pos) {
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.decoration_head, null);
                TextView textHead = inflate.findViewById(R.id.text_head);
                textHead.setText(decoration.getHeaderName(pos));
                return inflate;
            }
        });

        TransactionHistoryAdapter adapter = new TransactionHistoryAdapter(mList, this);
        mRecyclehistory.setLayoutManager(new LinearLayoutManager(this));
        mRecyclehistory.addItemDecoration(decoration);
        mRecyclehistory.setAdapter(adapter);
    }

}
