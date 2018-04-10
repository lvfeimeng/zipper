package com.zipper.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.activity.TransactionDefailsActivity;
import com.zipper.wallet.bean.TransactionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/4/10.
 */

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyHolder> {

    private Context mContext;
    private List<TransactionBean> mList;

    public TransactionHistoryAdapter(List<TransactionBean> item, Context context) {
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
        holder.mCurrency.setText(mList.get(position).getCurrency());
        holder.mDate.setText(mList.get(position).getDate());
        holder.mConfirmNum.setText(mList.get(position).getConfirmNum());
        holder.mFormPrice.setText(mList.get(position).getFormPrice());
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, TransactionDefailsActivity.class));
                }
            });
        }
    }
}
