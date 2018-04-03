package com.zipper.wallet.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MyRecViewHolder extends RecyclerView.ViewHolder {

    public MyRecViewHolder(View itemView) {
        super(itemView);
    }

    public View findView(int viewId) {
        return itemView.findViewById(viewId);
    }

}
