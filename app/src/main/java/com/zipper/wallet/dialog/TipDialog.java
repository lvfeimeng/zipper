package com.zipper.wallet.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseDialog;
import com.zipper.wallet.utils.RuntHTTPApi;


/**
 * Created by EDZ on 2018/1/24.
 */

public class TipDialog extends BaseDialog {
    private ImageView imgView;
    private int img;

    /**
     *
     * @param context
     * @param title
     * @param tip
     * @param type
     * @param left
     * @param right
     * @param img
     * @param rp
     */
    public TipDialog(Context context, String title, String tip, String left,String right,int img,TipType type, RuntHTTPApi.ResPonse rp) {
        super(context,title,tip,left,right,rp);
        this.type = type;
        this.img = img;
        setContentViewId(R.layout.dialog_tip);
    }
    public TipDialog(Context context, String title, String tip,String left,String right,int img, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,left,right,img,TipType.TIP,rp);
    }
    public TipDialog(Context context, String title, String tip,String left,String right, TipType type, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,left,right,0,type,rp);
    }
    public TipDialog(Context context, String title, String tip,String right, TipType type, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,null,right,type,rp);
    }
    public TipDialog(Context context, String tip,String right, TipType type, RuntHTTPApi.ResPonse rp) {
        this(context,null,tip,null,right,type,rp);
    }

    public TipDialog(Context context, String title, String tip,String right, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,right,TipType.TIP,rp);
    }


    public TipDialog(Context context, String tip,String right, RuntHTTPApi.ResPonse rp) {
        this(context,null,tip,right,rp);
    }

    public TipDialog(Context context, String tip,String right) {
        this(context,tip,right,null);
    }

    @Override
    public void initComponent() {
        super.initComponent();
        imgView =(ImageView) findViewById(R.id.img);

        if(img == 0){
            imgView.setVisibility(View.GONE);
        }else{
            imgView.setVisibility(View.VISIBLE);
            imgView.setImageResource(img);
        }
        switch (type){
            case TIP:
                break;
            case WORNING:
                break;
            case ERROR:
                break;
        }
    }


}
