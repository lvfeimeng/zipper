package com.zipper.wallet.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.dialog.TipDialog;
import com.zipper.wallet.listenear.OnClickListenearAndDo;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
    }

    @Override
    public void finish() {
        ActivityManager.activityStack.remove(this);
        super.finish();
    }

    public int dp2px(int dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }

    public void toast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }


    public View inflate(int layoutId) {
        return LayoutInflater.from(this).inflate(layoutId, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 提示框
     *
     * @param tip 提示信息
     * @param rp 确定按钮执行的方法
     */
    protected void showTipDialog(final String tip, final RuntHTTPApi.ResPonse rp) {
        new TipDialog(mContext,tip,rp).show();

    }

    /**
     *  显示输入型弹框
     * @param title 标题
     * @param hint  默认提示文字
     * @param text  文本
     * @param inputType 输入类型
     * @param okRp  按钮执行的方法
     */
    protected void showInputDialog(String title, String hint, String text , int inputType, final RuntHTTPApi.ResPonse Rp){

        View dialogView = getLayoutInflater().inflate(R.layout.layout_edittext,null);
        final EditText et = (EditText)dialogView.findViewById(R.id.edit_input);
        et.setHint(hint);
        if(inputType != 0) {
            et.setInputType(inputType);
        }
        et.setText(text);
        alertDialog = new AlertDialog.Builder(mContext).setTitle(title )
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();;
                        if(Rp !=null) {
                            Rp.doErrorThing(null);
                        }
                    }
                })
                .setPositiveButton("确定", null).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListenearAndDo() {
            @Override
            public void doClick(View view) {
                Map map = new HashMap();
                map.put("view",et.getText());
                Rp.doSuccessThing(map);
            }
        });
    }

    public static boolean isNull(TextView view){
        if(view.getText().equals("")||view.getText().toString().trim().equals("")){
            return true;
        }
        return false;
    }

    ProgressDialog progressDialog ;
    protected void showProgressDialog(String tip){
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle(tip);
        progressDialog.show();
    }
    protected void hideProgressDialog(){
        progressDialog.cancel();
    }

}
