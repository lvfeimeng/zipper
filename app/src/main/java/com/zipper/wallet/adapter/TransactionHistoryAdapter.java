package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.activity.PropertyDetailActivity;
import com.zipper.wallet.activity.TransactionActivity;
import com.zipper.wallet.database.PropertyRecord;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/4/10.
 */
public class TransactionHistoryAdapter extends CommonAdapter<PropertyRecord> {

    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");

    public TransactionHistoryAdapter(Context context, List<PropertyRecord> datas) {
        super(context, R.layout.transaction_history_item, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, PropertyRecord mItem, int position) {
        MyHolder holder = new MyHolder(viewHolder.getConvertView());

        if (!TextUtils.isEmpty(mItem.getHash())) {
            String hash = mItem.getHash().substring(0, 8) + "..." + mItem.getHash().substring(mItem.getHash().length() - 8, mItem.getHash().length());
            holder.mCurrency.setText(hash);
        } else {
            holder.mCurrency.setText("");
        }
        holder.mDate.setText(sdf.format(mItem.getTimestamp() * 1000));
        if (TextUtils.isEmpty(mItem.getBlockHeight())||"null".equalsIgnoreCase(mItem.getBlockHeight())) {
            mItem.setBlockHeight("0");
        }
        if (TextUtils.isEmpty(mItem.getHeight())||"null".equalsIgnoreCase(mItem.getHeight())) {
            mItem.setHeight("0");
        }
        int times = Integer.parseInt(mItem.getBlockHeight()) - Integer.parseInt(mItem.getHeight()) + 1;
        holder.mConfirmNum.setText("确定次数:" + times);
        //String formatData = getFormatData(item.getValue(), ((PropertyDetailActivity) mContext).deciamls);
        String symbol = "";
        if (mItem.getState() == 1) {
            symbol = "+";
        } else if (mItem.getState() == -1) {
            symbol = "-";
        }
        holder.mFormPrice.setText(symbol + getFormatData(mItem.getValue(), mItem.getDecimals()) + " " + mItem.getName());

    }

    private String getFormatData(String amount, String decimals) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals) || "null".equalsIgnoreCase(amount) || "null".equalsIgnoreCase(decimals)) {
            return "0";
        }
        return new BigDecimal(amount).divide(new BigDecimal(decimals), 8, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLinearLayout;
        private TextView mCurrency;
        private TextView mDate;
        private TextView mConfirmNum;
        private TextView mFormPrice;

        public MyHolder(View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.transaction_history);
            mCurrency = itemView.findViewById(R.id.text_currency);
            mDate = itemView.findViewById(R.id.text_date);
            mConfirmNum = itemView.findViewById(R.id.confirm_num);
            mFormPrice = itemView.findViewById(R.id.text_form);

        }
    }

}
//  class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyHolder> {
//
//    private Context mContext;
//    private List<PropertyRecord> mList;
//    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
//    private PropertyRecord mItem;
//
//    public TransactionHistoryAdapter(List<PropertyRecord> items, Context context) {
//        this.mContext = context;
//        if (items == null) {
//            return;
//        }
//        mList=items;
//    }
//
//    @Override
//    public TransactionHistoryAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.transaction_history_item, null));
//    }
//
//    @Override
//    public void onBindViewHolder(TransactionHistoryAdapter.MyHolder holder, int position) {
//        mItem = mList.get(position);
//        if (!TextUtils.isEmpty(mItem.getHash())) {
//            String hash = mItem.getHash().substring(0, 8) + "..." + mItem.getHash().substring(mItem.getHash().length() - 8, mItem.getHash().length());
//            holder.mCurrency.setText(hash);
//        }else{
//            holder.mCurrency.setText("");
//        }
//        String time = sdf.format(mItem.getTimestamp() * 1000);
//        holder.mDate.setText(time);
//        holder.mConfirmNum.setText("确定次数:" + 0);
//        //String formatData = getFormatData(item.getValue(), ((PropertyDetailActivity) mContext).deciamls);
//        String symbol = "";
//        if (mItem.getState() == 1) {
//            symbol = "+";
//        } else if (mItem.getState() == -1) {
//            symbol = "-";
//        }
//        holder.mFormPrice.setText(symbol+getFormatData(mItem.getValue(),mItem.getDecimals())+" "+mItem.getName());
//
//    }
//
//    private String getFormatData(String amount, String decimals) {
//        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(decimals) || "null".equalsIgnoreCase(amount) || "null".equalsIgnoreCase(decimals)) {
//            return "0";
//        }
//        return new BigDecimal(amount).divide(new BigDecimal(decimals),8,BigDecimal.ROUND_HALF_UP).toPlainString();
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    public class MyHolder extends RecyclerView.ViewHolder {
//
//        private final LinearLayout mLinearLayout;
//        private final TextView mCurrency;
//        private final TextView mDate;
//        private final TextView mConfirmNum;
//        private final TextView mFormPrice;
//
//        public MyHolder(View itemView) {
//            super(itemView);
//            mLinearLayout = itemView.findViewById(R.id.transaction_history);
//            mCurrency = itemView.findViewById(R.id.text_currency);
//            mDate = itemView.findViewById(R.id.text_date);
//            mConfirmNum = itemView.findViewById(R.id.confirm_num);
//            mFormPrice = itemView.findViewById(R.id.text_form);
//
//        }
//    }
//
//}
