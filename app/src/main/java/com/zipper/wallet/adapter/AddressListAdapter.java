package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.bean.PayAddressBean;

import java.math.BigDecimal;
import java.util.List;

public class AddressListAdapter extends CommonAdapter<PayAddressBean> {


    public AddressListAdapter(Context context, List<PayAddressBean> datas) {
        super(context, R.layout.item_address_list, datas);
    }

    @Override
    protected void convert(ViewHolder holder, PayAddressBean item, int position) {
        AddressViewHolder vh=new AddressViewHolder(holder.getConvertView());
        vh.textAddress.setText(item.getAddress());
        String value=new BigDecimal(item.getValue()).divide(new BigDecimal(item.getDecimals()), 8, BigDecimal.ROUND_HALF_UP).toPlainString();
        vh.textValue.setText(value+"\n"+item.getName());
    }


    static class AddressViewHolder extends RecyclerView.ViewHolder {
        protected TextView textAddress;
        protected TextView textValue;

        public AddressViewHolder(View itemView) {
            super(itemView);
            textAddress = (TextView) itemView.findViewById(R.id.text_address);
            textValue = (TextView) itemView.findViewById(R.id.text_value);
        }
    }

}
