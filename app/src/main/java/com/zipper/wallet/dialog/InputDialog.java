package com.zipper.wallet.dialog;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseDialog;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EDZ on 2018/1/29.
 */

public class InputDialog extends BaseDialog {
    EditText  editText;
    private String hint,text;
    private int input;


    /**
     *
     * @param context
     * @param title
     * @param tip
     * @param hint
     * @param text
     * @param left
     * @param right
     * @param inputType
     * @param backdown
     * @param rp
     */
    public InputDialog(Context context, String title, String tip,String hint,String text, String left, String right,int inputType,boolean backdown, RuntHTTPApi.ResPonse rp) {
        super(context, title, tip, left, right, rp);
        setCanceledOnTouchOutside(backdown);//调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用；
        setCancelable(backdown);// 调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用
        this.hint = hint;
        this.text = text;
        this.input = inputType;
        setContentViewId(R.layout.dialog_input);
    }

    public InputDialog(Context context, String title, String tip,String hint,String text, String left, String right,int inputType, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,text,left,right,inputType,true,rp);
    }

    public InputDialog(Context context, String title, String tip,String hint,String text,String right, int inputType,RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,text,"",right,inputType,rp);
    }

    public InputDialog(Context context, String title, String tip,String hint,String right, int inputType,RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,"",right,inputType,rp);
    }

    public InputDialog(Context context, String tip,String hint,String right,int inputType, RuntHTTPApi.ResPonse rp) {
        this(context,null,tip,hint,right,inputType,rp);
    }

    public InputDialog(Context context, String tip, String hint,String right, int inputType) {
        this(context,tip,hint,right,inputType,null);
    }

    public InputDialog(Context context, String title, String tip,String hint,String text,String left,String right, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,text,left,right,InputType.TYPE_TEXT_VARIATION_NORMAL,rp);
    }
    public InputDialog(Context context, String title, String tip,String hint,String text,String right, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,text,"",right,rp);
    }
    public InputDialog(Context context, String title, String tip, String hint, String right, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,"",right,rp);
    }
    public InputDialog(Context context, String tip,String hint,String right, RuntHTTPApi.ResPonse rp) {
        this(context,null,tip,hint,right,rp);
    }
    public InputDialog(Context context, String tip,String hint,String right) {
        this(context,tip,hint,right,null);
    }


    public InputDialog(Context context, String title, String tip,String hint,String text,String left,String right,boolean backdown, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,text,left,right,InputType.TYPE_TEXT_VARIATION_NORMAL,backdown,rp);
    }
    public InputDialog(Context context, String title, String tip,String hint,String text,String right,boolean backdown, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,text,"",right,backdown,rp);
    }
    public InputDialog(Context context, String title, String tip, String hint, String right,boolean backdown, RuntHTTPApi.ResPonse rp) {
        this(context,title,tip,hint,"",right,backdown,rp);
    }
    public InputDialog(Context context, String tip,String hint,String right,boolean backdown, RuntHTTPApi.ResPonse rp) {
        this(context,null,tip,hint,right,backdown,rp);
    }
    public InputDialog(Context context, String tip,String hint,String right,boolean backdown) {
        this(context,tip,hint,right,backdown,null);
    }



    @Override
    public void initComponent() {
        super.initComponent();
        editText = (EditText)findViewById(R.id.edit_input);
        editText.setHint(hint);
        editText.setText(text);
        if (input == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
            editText.setInputType(input);
        }
    }

    @Override
    public void btnOkClick() {
        MyLog.i(TAG,"btnOkClick()");
        dismiss();
        Map map = new HashMap();
        map.put(INPUT_TEXT, editText.getText());
        if(editText.getText() == null || editText.getText().toString().length() == 0 || editText.getText().equals("")){
            if (rp != null) {
                Toast.makeText(mContext,"输入的文字不能为空",Toast.LENGTH_SHORT).show();
                rp.doErrorThing(map);
            }
        } else if (rp != null) {
            rp.doSuccessThing(map);
        }
    }
}
