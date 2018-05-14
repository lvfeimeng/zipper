package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.activity.home.presenter.HomePresenter;
import com.zipper.wallet.adapter.TransactionHistoryAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.database.PropertyRecord;
import com.zipper.wallet.utils.NormalDecoration;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends BaseActivity implements HomeContract.View {
    private SwipeMenuRecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TransactionHistoryAdapter mAdapter;
    private List<PropertyRecord> items = null;
    private NormalDecoration mDecoration;

    private HomePresenter presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        presenter = new HomePresenter(this, this);
        initView();
    }

    private void initView() {

        mRecyclerView = findViewById(R.id.recycler_view);
        mToolbar = findViewById(R.id.toolbar_transaction);
        setSupportActionBar(mToolbar);
        findViewById(R.id.img_back).setOnClickListener(v -> finish());

        items = new ArrayList<>();
        mAdapter = new TransactionHistoryAdapter(this, items);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemViewSwipeEnabled(false);
        mRecyclerView.setLongPressDragEnabled(false);
        headView();
        mRecyclerView.setSwipeItemClickListener((itemView, position) -> {
            startActivity(new Intent(mContext, TransactionDetailsActivity.class)
                    .putExtra("coin_id", items.get(position).getCoin_id())
                    .putExtra("currency", items.get(position)));
        });
        mRecyclerView.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {
        List<PropertyRecord> list = DataSupport.order("timestamp desc").find(PropertyRecord.class);
        items.addAll(list);
//        PropertyRecord record = items.get(0);//测试数据
//        record.setTimestamp(1527782400);
//        items.add(0, record);
        mAdapter.notifyDataSetChanged();
        for (PropertyRecord item : items) {
            presenter.getBlockChainInfo(item.getCoin_id());
        }
    }

    private void headView() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月");

        mDecoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                return sdf.format(items.get(pos).getTimestamp() * 1000);
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

        mRecyclerView.addItemDecoration(mDecoration);
    }

    public int blockHeight = 0;

    @Override
    public void doSuccess(int coin_id, Object obj) {
        //{"data":{"height":41517},"errCode":0,"hash":"3c161dd82e4b4e51fc634c56267e6bd7"}
        Log.d(TAG, "doSuccess: result=" + obj);
        try {
            JSONObject jsonObject = new JSONObject(obj.toString());
            JSONObject object = jsonObject.optJSONObject("data");
            if (object.optInt("errCode") == 0) {
                blockHeight = object.optInt("height");
                putString("blockHeight", String.valueOf(blockHeight));
            }
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFailure() {
        if (!TextUtils.isEmpty(getString("blockHeight"))) {
            blockHeight = Integer.parseInt(getString("blockHeight"));
        }
    }
}
