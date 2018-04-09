package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.bean.CoinsBean2;
import com.zipper.wallet.utils.ScreenUtils;

import java.util.List;

/**
 * Created by AlMn on 2018/04/08.
 */

public class SelectCoinsAdapter extends CommonAdapter<CoinsBean2> {

    public SelectCoinsAdapter(Context context, List<CoinsBean2> datas) {
        super(context, R.layout.item_select_coins, datas);
    }

    @Override
    protected void convert(ViewHolder holder, CoinsBean2 bean, int position) {
        SelectViewHolder vh = new SelectViewHolder(holder.getConvertView());
        vh.textName.setText(bean.getShortName());
        vh.textBalance.setText(bean.getCount());
        CardView.LayoutParams params = new CardView.LayoutParams(-1,
                ScreenUtils.dp2px(mContext, 80));
        if (position % 2 == 1) {
            params.leftMargin = ScreenUtils.dp2px(mContext, 10);
        }
        params.bottomMargin = ScreenUtils.dp2px(mContext, 10);
        holder.getConvertView().setLayoutParams(params);
    }

    static class SelectViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textBalance;

        public SelectViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textBalance = (TextView) itemView.findViewById(R.id.text_balance);
        }
    }
}
