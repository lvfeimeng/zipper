package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.PropertyDetailActivity;
import com.zipper.wallet.database.PropertyRecord;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.zhy.adapter.recyclerview.base.ViewHolder;

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
        String address=item.getHash().substring(0,8)+"..."+item.getHash().substring(item.getHash().length()-8,item.getHash().length());
        vh.txtAddr.setText(address);
        String deciamls = ((PropertyDetailActivity) mContext).deciamls;
        vh.txtCount.setText(getFormatData(item.getValue(), deciamls));
        //vh.txtName.setText(item.getUnit());
        vh.txtDetail.setText(sdf.format(item.getTimestamp()*1000) + " | 确认次数：" + 0);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");

    private static class RecordViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtAddr;
        protected TextView txtDetail;
        protected TextView txtCount;
        protected TextView txtName;

        public RecordViewHolder(View itemView) {
            super(itemView);
            txtAddr = (TextView) itemView.findViewById(R.id.txt_addr);
            txtDetail = (TextView) itemView.findViewById(R.id.txt_detail);
            txtCount = (TextView) itemView.findViewById(R.id.txt_count);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
        }
    }

    private String getFormatData(String amount, String decimals) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals)||"null".equalsIgnoreCase(amount)||"null".equalsIgnoreCase(decimals)) {
            return "0";
        }
        double result = Double.parseDouble(amount) / Double.parseDouble(decimals);
        return new DecimalFormat("0.00000000").format(result);
    }
}
