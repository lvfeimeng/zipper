package com.zipper.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.activity.TransactionDefailsActivity;
import com.zipper.wallet.database.PropertyRecord;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/4/10.
 */

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyHolder> {

    private Context mContext;
    private List<PropertyRecord> mList;
    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
    private PropertyRecord mItem;

    public TransactionHistoryAdapter(List<PropertyRecord> item, Context context) {
        this.mContext = context;
        if (item == null) {
            return;
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.addAll(item);
    }

    @Override
    public TransactionHistoryAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.transaction_history_item, null));
    }

    @Override
    public void onBindViewHolder(TransactionHistoryAdapter.MyHolder holder, int position) {
        mItem = mList.get(position);
        if (mItem.getFrom().equals(mItem.getAddr()) || mItem.getTo().equals(mItem.getAddr())) {
            String address = mItem.getFrom().substring(0, 8) + "..." + mItem.getFrom().substring(mItem.getFrom().length() - 8, mItem.getFrom().length());
            holder.mCurrency.setText(address);
            String time = sdf.format(mItem.getTimestamp() * 1000);
            holder.mDate.setText(time);
            holder.mConfirmNum.setText("确定次数:" + mItem.getFee());
            //String formatData = getFormatData(item.getValue(), ((PropertyDetailActivity) mContext).deciamls);
            holder.mFormPrice.setText(mItem.getValue());
            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TransactionDefailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("transaction", mItem);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return isAddress();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private final LinearLayout mLinearLayout;
        private final TextView mCurrency;
        private final TextView mDate;
        private final TextView mConfirmNum;
        private final TextView mFormPrice;

        public MyHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.transaction_history);
            mCurrency = itemView.findViewById(R.id.text_currency);
            mDate = itemView.findViewById(R.id.text_date);
            mConfirmNum = itemView.findViewById(R.id.confirm_num);
            mFormPrice = itemView.findViewById(R.id.text_form);

        }
    }

    private String getFormatData(String amount, String decimals) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals) || "null".equalsIgnoreCase(amount) || "null".equalsIgnoreCase(decimals)) {
            return "0";
        }
        double result = Double.parseDouble(amount) / Double.parseDouble(decimals);
        return new DecimalFormat("0.00000000").format(result);
    }

    private int num = 0;

    private int isAddress() {

        for (PropertyRecord item : mList) {
            if (item.getFrom().equals("item.getAddr()") || item.getTo().equals("item.getAddr()")) {
                num++;
            }
        }

        return num;
    }

}
