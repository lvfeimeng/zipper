package com.zipper.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.AddPropertyActivity;
import com.zipper.wallet.activity.SearchCoinsActivity;
import com.zipper.wallet.bean.CoinsBean;
import com.zipper.wallet.utils.MyRecViewHolder;
import com.zipper.wallet.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CoinsBean> mList;
    private ConisAdapter adapter;

    public WalletAdapter(Context context, List<CoinsBean> items) {
        this.mContext = context;
        if (items == null) {
            return;
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.addAll(items);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 0:
                view = inflate(R.layout.layout_wallet_center);
                view.setLayoutParams(new LinearLayout.LayoutParams(-1, ScreenUtils.dp2px(mContext, 60)));
                viewHolder = new CenterViewHolder(view);
                break;
            case 1:
                //view = new RecyclerView(mContext);
                view = inflate(R.layout.layout_recyclerview);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                //viewHolder = new MyRecViewHolder(view);
                viewHolder = new RecViewHolder(view);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                loadCenterView((CenterViewHolder) holder);
                break;
            case 1:
                loadListData((RecViewHolder) holder);
                break;
            default:
                break;
        }
    }

    private void loadCenterView(CenterViewHolder holder) {
        holder.imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, AddPropertyActivity.class));
            }
        });
        holder.imageSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.textSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, SearchCoinsActivity.class));
            }
        });
    }

    //RecyclerView recyclerView;

    private void loadListData(RecViewHolder holder) {
//        if (recyclerView == null) {
//            recyclerView = (RecyclerView) holder.itemView;
//        }
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        holder.recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration
                        .Builder(mContext)
                        .color(mContext.getResources().getColor(R.color.color_gray9))
                        .size(1)
                        .margin(ScreenUtils.dp2px(mContext, 15), ScreenUtils.dp2px(mContext, 15))
                        .build()
        );
       // holder.recyclerView.setFocusable(false);
        holder.recyclerView.setFocusableInTouchMode(false);

        if (adapter == null) {
            adapter = new ConisAdapter(mContext, mList);
            holder.recyclerView.setAdapter(adapter);
//            holder.recyclerView.requestDisallowInterceptTouchEvent(true);
//            holder.recyclerView.smoothScrollToPosition(0);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    private View inflate(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    static class RecViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public RecViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        }
    }

    static class CenterViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAdd;
        ImageView imageSort;
        TextView textSearch;

        public CenterViewHolder(View itemView) {
            super(itemView);
            imageAdd = (ImageView) itemView.findViewById(R.id.image_add);
            imageSort = (ImageView) itemView.findViewById(R.id.image_sort);
            textSearch = (TextView) itemView.findViewById(R.id.text_search);
        }
    }
}
