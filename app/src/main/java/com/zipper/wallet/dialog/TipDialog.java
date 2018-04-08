package com.zipper.wallet.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseDialog;
import com.zipper.wallet.utils.RuntHTTPApi;


/**
 * Created by EDZ on 2018/1/24.
 */

public class TipDialog extends BaseDialog {
    String tip,title;
    TipType type;
    private TextView txtTip,txtTitle;
    private ImageView imgView;
    private RuntHTTPApi.ResPonse rp;
    private int img;
    public TipDialog(Context context, String title, String tip, TipType type,int img, RuntHTTPApi.ResPonse rp) {
        super(context,title,rp);
        this.tip =tip;
        this.type = type;
        this.rp = rp;
        this.title = title;
        this.img = img;
        setContentViewId(R.layout.dialog_tip);
    }
    public TipDialog(Context context, String title, String tip, TipType type, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,type,0,rp);
    }

    public TipDialog(Context context, String title, String tip, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,TipType.TIP,rp);
    }

    public TipDialog(Context context, String tip, RuntHTTPApi.ResPonse rp) {
        this(context,"提示",tip,rp);
    }

    public TipDialog(Context context, String tip) {
        this(context,tip,null);
    }

    @Override
    public void initComponent() {
        txtTip = (TextView)findViewById(R.id.txt_tip);
        txtTitle = (TextView)findViewById(R.id.txt_title);
        imgView =(ImageView) findViewById(R.id.img);
        if(img == 0){
            imgView.setVisibility(View.GONE);
        }else{
            imgView.setVisibility(View.VISIBLE);
            imgView.setImageResource(img);
        }
        super.initComponent();
        switch (type){
            case TIP:
                break;
            case WORNING:
                break;
            case ERROR:
                break;
        }
        txtTip.setText(tip);
        txtTitle.setText(title);
    }
    public enum TipType {
        WORNING,TIP,ERROR
    }


}
