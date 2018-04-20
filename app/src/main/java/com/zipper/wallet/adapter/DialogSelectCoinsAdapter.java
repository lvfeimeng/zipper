package com.zipper.wallet.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.bean.CoinsBean;

import java.util.List;

/**
 * Created by AlMn on 2018/04/12.
 */

public class DialogSelectCoinsAdapter extends CommonAdapter<CoinsBean> {


    public DialogSelectCoinsAdapter(Context context, List<CoinsBean> datas) {
        super(context, R.layout.item_dialog_select_coins, datas);
    }

    @Override
    protected void convert(ViewHolder viewHolder, CoinsBean item, int position) {
        View rootView = viewHolder.getConvertView();
        RadioButton radioButton = (RadioButton) rootView.findViewById(R.id.radio_button);
        radioButton.setChecked(item.isSelected());
        //radioButton.setText(item.getShortName());
        radioButton.setOnClickListener(v -> {
            item.setSelected(true);
            for (CoinsBean mData : mDatas) {
                if (mData != item) {
                    mData.setSelected(false);
                }
                notifyDataSetChanged();
            }
        });
    }
}
