package com.zipper.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.PropertyActvity;
import com.zipper.wallet.bean.CoinsBean;
import com.zipper.wallet.utils.ImgUtil;

import java.util.List;

public class ConisAdapter extends CommonAdapter<CoinsBean> {

    public ConisAdapter(Context context, List<CoinsBean> datas) {
        super(context, R.layout.item_coins, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final CoinsBean bean, int position) {
        CoinsViewHolder vh = new CoinsViewHolder(holder.getConvertView());
        ImgUtil.loadCircleImage(bean.getIcon(), vh.imageView);
        vh.textShortName.setText(bean.getName());
        vh.textFullName.setText(bean.getFull_name());
        vh.textCoinsCount.setText("0");
        holder.getConvertView()
                .setOnClickListener(v ->
                        mContext.startActivity(new Intent(mContext, PropertyActvity.class)
                                .putExtra("id", bean.getId())));
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
