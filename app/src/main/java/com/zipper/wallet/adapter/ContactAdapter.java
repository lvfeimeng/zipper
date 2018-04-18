package com.zipper.wallet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zipper.wallet.R;
import com.zipper.wallet.bean.ContactBean;

import java.util.List;

public class ContactAdapter extends CommonAdapter<ContactBean> {

    public ContactAdapter(Context context, List<ContactBean> datas) {
        super(context, R.layout.item_contact, datas);
    }

    @Override
    protected void convert(ViewHolder holder, ContactBean bean, int position) {
        ContactViewHolder vh = new ContactViewHolder(holder.getConvertView());
//        ImgUtil.loadCircleImage(bean.getPhoto(), vh.imageView);
        vh.textName.setText(bean.getName());
        vh.textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击跳转" + vh.textName.getText() + "的联系详情", Toast.LENGTH_SHORT).show();
            }
        });
//        vh.textKey.setText(bean.getKey());
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        //        private ImageView imageView;
        private TextView textName;
//        private TextView textKey;

        public ContactViewHolder(View itemView) {
            super(itemView);
//            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            textName = (TextView) itemView.findViewById(R.id.text_name);
//            textKey = (TextView) itemView.findViewById(R.id.text_key);
        }
    }

}
