package com.zipper.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

//打开或关闭软键盘
public class KeyBoardUtils {

    /**
     * 打卡软键盘
     */
    public static void openKeybord(View view, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeybord( Context mContext) {
        try {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

            if(imm.isActive() && ((Activity)mContext).getWindow().getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(((Activity)mContext).getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){
            e.printStackTrace();
            MyLog.e("KeyBoardUtils",e.getMessage());
        }
    }
    /**
     * 关闭软键盘
     */
    public static void closeKeybord(View view, Context mContext) {
        try {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }catch (Exception e){
            e.printStackTrace();
            MyLog.e("KeyBoardUtils",e.getMessage());
        }
    }
}