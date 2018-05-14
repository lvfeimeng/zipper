package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.PropertyDetailActivity;
import com.zipper.wallet.database.PropertyRecord;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by AlMn on 2018/04/21.
 */

public class PropertyRecordAdapter extends CommonAdapter<PropertyRecord> {

    public PropertyRecordAdapter(Context context, List<PropertyRecord> datas) {
        super(context, R.layout.item_deal_record, datas);
    }

    @Override
    protected void convert(ViewHolder holder, PropertyRecord item, int position) {
        RecordViewHolder vh = new RecordViewHolder(holder.getConvertView());
        if (!TextUtils.isEmpty(item.getHash())) {
            String address = item.getHash().substring(0, 8) + "..." + item.getHash().substring(item.getHash().length() - 8, item.getHash().length());
            vh.txtAddr.setText(address);
        } else {
            vh.txtAddr.setText("");
        }
        String deciamls = ((PropertyDetailActivity) mContext).deciamls;
        String symbol = "";
        if (item.getState() == 1) {
            symbol = "+";
        } else if (item.getState() == -1) {
            symbol = "-";
        }
        vh.txtCount.setText(symbol + getFormatData(item.getValue(), deciamls) + " " + item.getName());
        if (Integer.parseInt(item.getHeight()) >= 0) {
            int times = ((PropertyDetailActivity) mContext).blockHeight - Integer.parseInt(item.getHeight()) + 1;
            vh.txtDetail.setText(sdf.format(item.getTimestamp() * 1000) + " | 确认次数：" + times);
        } else {
            vh.txtDetail.setText(sdf.format(item.getTimestamp() * 1000) + " | 未确认");
        }
    }

    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");

    private static class RecordViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtAddr;
        protected TextView txtDetail;
        protected TextView txtCount;

        public RecordViewHolder(View itemView) {
            super(itemView);
            txtAddr = (TextView) itemView.findViewById(R.id.txt_addr);
            txtDetail = (TextView) itemView.findViewById(R.id.txt_detail);
            txtCount = (TextView) itemView.findViewById(R.id.txt_count);
        }
    }

    private String getFormatData(String amount, String decimals) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals) || "null".equalsIgnoreCase(amount) || "null".equalsIgnoreCase(decimals)) {
            return "0";
        }
        return new BigDecimal(amount).divide(new BigDecimal(decimals), 8, BigDecimal.ROUND_HALF_UP).toPlainString();
    }
}
