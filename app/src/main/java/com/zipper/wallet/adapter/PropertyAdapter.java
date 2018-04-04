package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.bean.PropertyBean;
import com.zipper.wallet.utils.ImgUtil;

import java.util.List;

public class PropertyAdapter extends CommonAdapter<PropertyBean> {


    public PropertyAdapter(Context context, List<PropertyBean> datas) {
        super(context, R.layout.item_property, datas);
    }

    @Override
    protected void convert(ViewHolder holder, PropertyBean bean, int position) {
        PropertyViewHolder vh = new PropertyViewHolder(holder.getConvertView());
        ImgUtil.loadCircleImage(bean.getIcon(), vh.imageView);
        vh.textShortName.setText(bean.getShortName());
        vh.textFullName.setText(bean.getFullName());
        vh.checkBox.setChecked(bean.isOwn());
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private CheckBox checkBox;
        private TextView textShortName;
        private TextView textFullName;

        public PropertyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            textShortName = (TextView) itemView.findViewById(R.id.text_short_name);
            textFullName = (TextView) itemView.findViewById(R.id.text_full_name);
        }
    }

}
