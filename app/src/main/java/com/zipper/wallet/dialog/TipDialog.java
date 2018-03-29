package com.zipper.wallet.dialog;

import android.content.Context;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseDialog;
import com.zipper.wallet.utils.RuntHTTPApi;


/**
 * Created by EDZ on 2018/1/24.
 */

public class TipDialog extends BaseDialog {
    String tip;
    TipType type;
    private TextView txtTip;
    public TipDialog(Context context, String title, String tip, TipType type, RuntHTTPApi.ResPonse rp) {
        super(context,title,rp);
        this.tip =tip;
        this.type = type;
        setContentViewId(R.layout.dialog_tip);
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
    }
    public enum TipType {
        WORNING,TIP,ERROR
    }


}
