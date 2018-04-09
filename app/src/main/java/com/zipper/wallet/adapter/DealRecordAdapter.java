package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zipper.wallet.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/9.
 */

public class DealRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<Map> mList;


    public DealRecordAdapter(Context context, List<Map> items) {
        this.mContext = context;
        this.mList = items;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        DealViewHolder viewHolder = null;
                view = inflate(R.layout.item_deal_record);
                viewHolder = new DealViewHolder(view);

        return viewHolder;
    }

    private View inflate(int layoutId) {
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DealViewHolder dHolder = (DealViewHolder)holder;
        dHolder.txtAddr.setText(mList.get(position).get("addr")+"");
        String str = mList.get(position).get("date")+" | 确认次数："+mList.get(position).get("times");
        //Log.i("DealRecordAdapter","str:"+str);
        dHolder.txtDetail.setText(str);
        dHolder.txtCount.setText(mList.get(position).get("count")+"");
        dHolder.txtName.setText(mList.get(position).get("name")+"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class DealViewHolder extends RecyclerView.ViewHolder {
        public TextView txtAddr,txtDetail,txtCount,txtName;

        public DealViewHolder(View itemView) {
            super(itemView);
            txtAddr = (TextView)itemView.findViewById(R.id.txt_addr);
            txtName = (TextView)itemView.findViewById(R.id.txt_name);
            txtDetail = (TextView)itemView.findViewById(R.id.txt_detail);
            txtCount = (TextView)itemView.findViewById(R.id.txt_count);
        }
    }


}
