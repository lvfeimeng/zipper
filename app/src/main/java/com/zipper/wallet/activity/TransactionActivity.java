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
import com.zipper.wallet.database.PropertyRecord;
import com.zipper.wallet.utils.NormalDecoration;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends BaseActivity {
    private RecyclerView mRecyclehistory;
    private Toolbar mToolbar;
    private TransactionHistoryAdapter mAdapter;
    private List<PropertyRecord> items = null;
    private NormalDecoration mDecoration;
    public String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        mAddress = getIntent().getStringExtra("address");
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

        headView();
        testData();
    }

    private void testData() {

        List<PropertyRecord> list = DataSupport.findAll(PropertyRecord.class);
        List<PropertyRecord> list2 = new ArrayList<>();
        if(list != null){
            for (PropertyRecord record : list) {
                list2.add(record);
            }
            loadData(list2);
        }else{
            mRecyclehistory.setVisibility(View.GONE);
        }

    }

    private void loadData(List<PropertyRecord> list) {
        if (list == null) {
            return;
        }
        items = new ArrayList<>();
        items.addAll(list);
        for (PropertyRecord item : items) {
            if(!item.getFrom().equals(((TransactionActivity) mContext).mAddress) || !item.getTo().equals(((TransactionActivity) mContext).mAddress)) {
                mRecyclehistory.setVisibility(View.GONE);
            }
            item.setUnit("");
        }
        isAdapter();
    }

    private void isAdapter(){
        mAdapter = new TransactionHistoryAdapter(items, this);
        mRecyclehistory.setLayoutManager(new LinearLayoutManager(this));
        mRecyclehistory.setNestedScrollingEnabled(false);
        mRecyclehistory.addItemDecoration(mDecoration);
        mRecyclehistory.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void headView() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");

        mDecoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                if(mAddress.equals(items.get(pos).getFrom())||mAddress.equals(items.get(pos).getTo())){
                    String date = sdf.format(items.get(pos).getTimestamp()*1000);
                    String subDate = date.substring(0,date.indexOf("月"));
                    return subDate + "月";
                }
                return null;
            }
        };

        mDecoration.setOnDecorationHeadDraw(new NormalDecoration.OnDecorationHeadDraw() {
            @Override
            public View getHeaderView(final int pos) {
                View inflate = LayoutInflater.from(mContext).inflate(R.layout.decoration_head, null);
                TextView textHead = inflate.findViewById(R.id.text_head);
                textHead.setText(mDecoration.getHeaderName(pos));
                return inflate;
            }
        });


    }

}
