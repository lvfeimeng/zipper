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

import java.util.List;

public class ConisAdapter extends CommonAdapter<CoinInfo> {

    public ConisAdapter(Context context, List<CoinInfo> datas) {
        super(context, R.layout.item_coins, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final CoinInfo bean, int position) {
        CoinsViewHolder vh = new CoinsViewHolder(holder.getConvertView());
        SelectedImage(vh.imageView, bean.getName(), bean.getFull_name());
        vh.textShortName.setText(bean.getName());
        vh.textFullName.setText(bean.getFull_name());

        if (!TextUtils.isEmpty(bean.getAmount()) && !"null".equalsIgnoreCase(bean.getAmount())) {
            vh.textCoinsCount.setText(bean.getAmount());
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

    private void SelectedImage(ImageView imageView, String name, String fullName) {

        switch (name) {
            case "BNB":
                imageView.setBackgroundResource(R.mipmap.bnb);
                break;
            case "BTC":
                imageView.setBackgroundResource(R.mipmap.btc);
                break;
            case "BCH":
                imageView.setBackgroundResource(R.mipmap.bch);
                break;
            case "BTM":
                if (fullName.equals("Bytom")) {
                    imageView.setBackgroundResource(R.mipmap.bitmark);
                } else
                    imageView.setBackgroundResource(R.mipmap.btm);
                break;
            case "BCD":
                imageView.setBackgroundResource(R.mipmap.bcd);
                break;
            case "BCX":
                imageView.setBackgroundResource(R.mipmap.bcx);
                break;
            case "DOGE":
                imageView.setBackgroundResource(R.mipmap.dogecoin);
                break;
            case "ETH":
                imageView.setBackgroundResource(R.mipmap.eth);
                break;
            case "ICX":
                imageView.setBackgroundResource(R.mipmap.icx);
                break;
            case "EOS":
                if (fullName.equals("EOS")) {
                    imageView.setBackgroundResource(R.mipmap.eos);
                } else
                    imageView.setBackgroundResource(R.mipmap.eostoken);
                break;
            case "FT":
                imageView.setBackgroundResource(R.mipmap.ft);
                break;
            case "NEO":
                imageView.setBackgroundResource(R.mipmap.neo);
                break;
            case "LTC":
                imageView.setBackgroundResource(R.mipmap.ltc);
                break;
            case "OMG":
                imageView.setBackgroundResource(R.mipmap.omg);
                break;
            case "TRX":
                imageView.setBackgroundResource(R.mipmap.trx);
                break;
            case "VEN":
                imageView.setBackgroundResource(R.mipmap.ven);
                break;
            case "ZIP":
                imageView.setBackgroundResource(R.mipmap.zip);
                break;
            default:
                imageView.setBackgroundResource(R.mipmap.defaultcurrency);
                break;
        }
    }

}
