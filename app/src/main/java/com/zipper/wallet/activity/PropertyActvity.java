package com.zipper.wallet.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.adapter.DealRecordAdapter;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.TestPopupWindow;
import com.zipper.wallet.utils.DeviceUtil;
import com.zipper.wallet.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/8.
 */

public class PropertyActvity extends BaseActivity {


    private TextView txtName,txtNameMore,txtCount,txtAddr,txtChoose;

    SmartRefreshLayout srf;
    protected CollapsingToolbarLayout collapsingToolbar;
    private DealRecordAdapter adapter;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private LinearLayout linTop;

    private  float downY=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_property);
        super.onCreate(savedInstanceState);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        txtAddr = (TextView)findViewById(R.id.txt_addr);
        txtName = (TextView)findViewById(R.id.txt_name);
        txtNameMore = (TextView)findViewById(R.id.txt_name_more);
        txtCount = (TextView)findViewById(R.id.txt_count);
        txtChoose = (TextView)findViewById(R.id.txt_choose);
        srf = (SmartRefreshLayout)findViewById(R.id.smartRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linTop = (LinearLayout)findViewById(R.id.lin_top);


        srf.setEnableRefresh(false);
        srf.setEnableLoadmore(true);
        srf.setRefreshHeader(new ClassicsHeader(mContext).setSpinnerStyle(SpinnerStyle.Translate));

        srf.setRefreshFooter(new ClassicsFooter(mContext).setSpinnerStyle(SpinnerStyle.Translate));
        srf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                Log.i("MoneyExpenseFragment","onRefresh");
                refreshlayout.finishRefresh(1000);
                refreshlayout.finishLoadmore(1000);
            }
        });
        srf.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                Log.i("MoneyExpenseFragment","onLoadmore");
                refreshlayout.finishRefresh(1000);
                refreshlayout.finishLoadmore(1000);
            }
        });
        adapter = new DealRecordAdapter(mContext,testData());
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .size(ScreenUtils.dp2px(mContext,1))
                .color(mContext.getResources().getColor(R.color.color_gray))
                .build());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //Log.i("MoneyExpenseFragment","dy:"+dy+" scrollView ScrollY:"+scrollView.getScrollY()+" "+(scrollView.getHeight() - srf.getHeight()));
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dy > 0 && scrollView.getScrollY() == 0) {
                    //大于0表示正在向右滚动
                } else {
                    //小于等于0表示停止或向左滚动

                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(downY<motionEvent.getY()){

                        }else if(scrollView.getScrollY() < ((View)srf.getParent()).getHeight() - srf.getHeight()){
                            float scrolly = scrollView.getScrollY();
                            scrollView.setScrollY(scrollView.getScrollY()+(int) (downY-motionEvent.getY()));
                            if(scrolly == scrollView.getScrollY()){
                                return false;
                            }
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        });

        final ViewTreeObserver vto = titlebar.getViewTreeObserver();

        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                int height = titlebar.getMeasuredHeight();
                int width = titlebar.getMeasuredWidth();

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(titlebar.getLayoutParams());
                lp.height = DeviceUtil.getDisplayPixel(mContext).y-height;
                lp.width = width;
                srf.setLayoutParams(lp);
                scrollView.setScrollY(0);
                titlebar.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        //srf.autoRefresh();

        txtChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestPopupWindow mWindow = new TestPopupWindow(mContext);
                //根据指定View定位
                //PopupWindowCompat.showAsDropDown(mWindow, mButtom, 0, 0, Gravity.START);
                //或者
                mWindow.showAsDropDown(txtChoose);
                //又或者使用showAtLocation根据屏幕来定位
               // mWindow.showAtLocation(...);
            }
        });

    }



    public List<Map> testData(){
        List<Map> list = new ArrayList<>();
        for(int i = 0 ; i < 50 ; i++){
            Map map = new HashMap();
            map.put("addr","0xla;skdfjkaskdf");
            map.put("name","SMT");
            map.put("count","+194912");
            map.put("date","1月20日");
            map.put("times","4");
            list.add(map);
        }
        return list;
    }

}
