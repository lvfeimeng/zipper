package com.zipper.wallet.base;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.listenear.OnClickListenearAndDo;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EDZ on 2018/1/24.
 */

public class BaseDialog extends Dialog {
    protected TipType type;
    public String TAG;
    String tip,title,left,right;
    public Context mContext;
    protected TextView txtLeft,txtRight,txtTitle,txtTip;
    protected final  String INPUT_TEXT = "input_text";
    private View view;
    public Map map;
    protected RuntHTTPApi.ResPonse rp;
    public BaseDialog(Context context, String title, String tip,String left,String right,RuntHTTPApi.ResPonse rp){
        super(context, R.style.MyDialog);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉dialog标题，即使没有写标题也会占用dialog空间
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);//这样就不会出现黑色背景框了*/
        setCanceledOnTouchOutside(false);//调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用；
        setCancelable(false);// 调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用
        TAG = getClass().getName();
        this.mContext = context;
        this.title = title;
        this.tip = tip;
        this.rp = rp;
        this.left = left;
        this.right = right;
        TAG = this.getClass().getSimpleName().toString();
        map = new HashMap();
        MyLog.i(TAG,String.format("title:%s, tip:%s, left:%s, right:%s, rp:%s",title, tip, left, right, rp));
    }



    public  void  setContentViewId(int ViewId ){
        MyLog.i(TAG,"执行setContentViewId");
        setContentView(ViewId);
        txtTip = (TextView)findViewById(R.id.txt_tip);
        txtTitle = (TextView)findViewById(R.id.txt_title);
        txtLeft = (TextView)findViewById(R.id.txt_left);
        txtRight = (TextView)findViewById(R.id.txt_right);
        view = findViewById(R.id.line_vertical);
        txtLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(rp!=null)
                    rp.doErrorThing(null);
            }
        });

        txtRight.setOnClickListener(new OnClickListenearAndDo() {
            @Override
            public void doClick(View v) {
                btnOkClick();
            }
        });


        if(left == null || left.equals("")){
            view.setVisibility(View.GONE);
            txtLeft.setVisibility(View.GONE);
        }else{
            txtLeft.setText(left);
        }
        txtRight.setText(right);

        if(title == null || title.equals("")){
            txtTitle.setVisibility(View.GONE);
        }else {
            txtTitle.setText(title);
        }

        if(tip == null || tip.equals("")){
            txtTip.setVisibility(View.GONE);
        }else {
            txtTip.setText(tip);
        }

        initComponent();
    }

    public void initComponent(){
        MyLog.i(TAG,"initComponent()");
    }
    public void btnOkClick(){
        MyLog.i(TAG,"btnOkClick()");
        BaseDialog.this.dismiss();
        if(rp!=null)
            rp.doSuccessThing(null);
    }


    public enum TipType {
        WORNING,TIP,ERROR
    }
}
