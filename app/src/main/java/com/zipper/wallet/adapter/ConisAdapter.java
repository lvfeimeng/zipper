package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.number.BigNumber;
import com.zipper.wallet.utils.CoinsUtil;

import java.util.List;

public class ConisAdapter extends CommonAdapter<CoinInfo> {

    public ConisAdapter(Context context, List<CoinInfo> datas) {
        super(context, R.layout.item_coins, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final CoinInfo bean, int position) {
        CoinsViewHolder vh = new CoinsViewHolder(holder.getConvertView());
        CoinsUtil.addCoinIcon(vh.imageView, bean.getName(), bean.getFull_name());
        vh.textShortName.setText(bean.getName());
        vh.textFullName.setText(bean.getFull_name());

        if (!TextUtils.isEmpty(bean.getAmount()) && !"null".equalsIgnoreCase(bean.getAmount())) {
            String amount=new BigNumber(bean.getAmount()).divide(new BigNumber(bean.getDecimals())).toString();
            vh.textCoinsCount.setText(amount);
        } else {
            vh.textCoinsCount.setText("0.00000000");
        }

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
