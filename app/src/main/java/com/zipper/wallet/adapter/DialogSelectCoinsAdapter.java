package com.zipper.wallet.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;

import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.database.CoinInfo;

import java.util.List;

/**
 * Created by AlMn on 2018/04/12.
 */

public class DialogSelectCoinsAdapter extends CommonAdapter<CoinInfo> {


    public DialogSelectCoinsAdapter(Context context, List<CoinInfo> datas) {
        super(context, R.layout.item_dialog_select_coins, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, CoinInfo item, int position) {
        View rootView = viewHolder.getConvertView();
        RadioButton radioButton = (RadioButton) rootView.findViewById(R.id.radio_button);
        radioButton.setChecked(item.isChecked());
        String text = null;
        if ("btc".equalsIgnoreCase(item.getAddr_algorithm())) {
            text = item.getName();
        } else if ("eth".equalsIgnoreCase(item.getAddr_algorithm())) {
            text = item.getName().toUpperCase();
        }
        radioButton.setText(text);
        radioButton.setGravity(Gravity.CENTER);
        radioButton.setOnClickListener(v -> {
            item.setChecked(true);
            for (CoinInfo mData : mDatas) {
                if (mData != item) {
                    mData.setChecked(false);
                }
                notifyDataSetChanged();
            }
        });
    }
}
