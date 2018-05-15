package com.zipper.wallet.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
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

    protected SmartRefreshLayout refreshLayout;
    private SwipeMenuRecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TransactionHistoryAdapter mAdapter;
    private List<PropertyRecord> items = null;
    private NormalDecoration mDecoration;

    private HomePresenter presenter = null;

    private int page = 1;
    private final int PAGE_SIZE = 50;

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

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setRefreshFooter(new ClassicsFooter(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            runOnUiThread(() -> showProgressDialog("加载中……"));
            page = 1;
            requestData();
            refreshlayout.finishRefresh(1000);
        });
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            runOnUiThread(() -> showProgressDialog("加载中……"));
            page++;
            requestData();
            refreshlayout.finishLoadmore(1000);
        });

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

        requestData();
//        loadData();
    }

    private void requestData() {
        new MyThread().start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgressDialog();
            if (page == 1) {
                items.clear();
            }
            List<PropertyRecord> list = (List<PropertyRecord>) msg.obj;
            if (list != null && list.size() > 0) {
                items.addAll(list);
                mAdapter.notifyDataSetChanged();
                for (PropertyRecord item : items) {
                    presenter.getBlockChainInfo(item.getCoin_id());
                }
            }
        }
    };

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            List<PropertyRecord> list = null;
//            int count = 0;
//            if (page == 1) {
//                list = DataSupport.order("timestamp desc").limit(PAGE_SIZE).find(PropertyRecord.class);
//            } else if (page > 1) {
            list = DataSupport.order("timestamp desc").offset((page - 1) * PAGE_SIZE).limit(PAGE_SIZE).find(PropertyRecord.class);
//            }
            Message msg = handler.obtainMessage();
            msg.obj = list;
            handler.sendMessage(msg);
            Looper.loop();
        }
    }

//    private void loadData() {
//        List<PropertyRecord> list = DataSupport.order("timestamp desc").find(PropertyRecord.class);
//        items.addAll(list);
////        PropertyRecord record = items.get(0);//测试数据
////        record.setTimestamp(1527782400);
////        items.add(0, record);
//        mAdapter.notifyDataSetChanged();
//        for (PropertyRecord item : items) {
//            presenter.getBlockChainInfo(item.getCoin_id());
//        }
//    }

    private void headView() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月");

        mDecoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                String name = "";
                if (items.size() > 0) {
                    name = sdf.format(items.get(pos).getTimestamp() * 1000);
                }
                return name;
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
                String blockHeight = object.optString("height");
                List<PropertyRecord> temps = null;
                int value = items.size() / PAGE_SIZE;
                if (value == 0) {
                    temps = items;
                } else {
                    temps = items.subList(value * PAGE_SIZE, items.size());
                }
                for (PropertyRecord temp : temps) {
                    if (temp.getCoin_id() == coin_id) {
                        temp.setBlockHeight(blockHeight);

                        ContentValues values = new ContentValues();
                        values.put("blockHeight", blockHeight);
                        DataSupport.updateAsync(PropertyRecord.class, values, temp.getId());
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFailure() {

    }
}
