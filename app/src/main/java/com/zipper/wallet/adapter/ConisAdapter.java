package com.zipper.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.PropertyActvity;
import com.zipper.wallet.activity.PropertyDetailActivity;
import com.zipper.wallet.bean.CoinsBean;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.utils.ImgUtil;

import java.util.List;

public class ConisAdapter extends CommonAdapter<CoinInfo> {

    public ConisAdapter(Context context, List<CoinInfo> datas) {
        super(context, R.layout.item_coins, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final CoinInfo bean, int position) {
        CoinsViewHolder vh = new CoinsViewHolder(holder.getConvertView());
        ImgUtil.loadCircleImage(bean.getIcon(), vh.imageView);
        vh.textShortName.setText(bean.getName());
        vh.textFullName.setText(bean.getFull_name());
        if (!TextUtils.isEmpty(bean.getAmount())) {
            vh.textCoinsCount.setText(bean.getAmount());
        }else{
            vh.textCoinsCount.setText("0");
        }
        //PropertyDetailActivity
        holder.getConvertView()
                .setOnClickListener(v ->
                        mContext.startActivity(new Intent(mContext, PropertyDetailActivity.class)
                                .putExtra("address", bean.getAddr())
                                .putExtra("deciamls", bean.getDecimals())
                                .putExtra("amount", bean.getAmount())
                                .putExtra("name", bean.getName())
                                .putExtra("full_name", bean.getFull_name())));
    }

    static class CoinsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textShortName;
        private TextView textFullName;
        private TextView textCoinsCount;

        public CoinsViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            textShortName = (TextView) itemView.findViewById(R.id.text_short_name);
            textFullName = (TextView) itemView.findViewById(R.id.text_full_name);
            textCoinsCount = (TextView) itemView.findViewById(R.id.text_coins_count);
        }
    }

}
