package com.zipper.wallet.base;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.zipper.wallet.R;
import com.zipper.wallet.listenear.OnClickListenearAndDo;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EDZ on 2018/1/24.
 */

public class BaseDialog extends Dialog {
    public String TAG,title;
    public Context mContext;
    public Map map;
    private TextView txtTitle;
    RuntHTTPApi.ResPonse rp;
    public BaseDialog(Context context, String title, RuntHTTPApi.ResPonse rp){
        super(context, R.style.MyDialog);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉dialog标题，即使没有写标题也会占用dialog空间
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);//这样就不会出现黑色背景框了*/
        setCanceledOnTouchOutside(false);//调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用；
        setCancelable(false);// 调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用
        this.mContext = context;
        this.title = title;
        this.rp = rp;
        TAG = this.getClass().getSimpleName().toString();
        map = new HashMap();
    }

    public Button btnOk;


    public  void  setContentViewId(int ViewId ){
        Log.i(TAG,"执行setContentViewId");
        setContentView(ViewId);
        btnOk = (Button)findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new OnClickListenearAndDo() {
            @Override
            public void doClick(View v) {
                btnOkClick();
            }
        });
        txtTitle = (TextView)findViewById(R.id.txt_title);
        txtTitle.setText(title);
        initComponent();
    }

    public void initComponent(){
        Log.i(TAG,"initComponent()");
    }
    public void btnOkClick(){
        Log.i(TAG,"btnOkClick()");
        BaseDialog.this.dismiss();
        if(rp!=null)
            rp.doSuccessThing(null);
    }
}
