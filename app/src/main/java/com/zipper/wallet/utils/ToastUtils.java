package com.zipper.wallet.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * Toast 统一管理
 * @author wa
 */
public class ToastUtils {
	
	public static boolean isShow = true;
    private static Toast toast;//在类前面声明吐司，确保在这个页面只有一个吐司
    /**
     * 短时间显示Toast
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        checkStatus(context, message,Toast.LENGTH_SHORT);
    }

    private static void checkStatus(Context context, CharSequence message,int status){
        if (toast == null) {
            toast = Toast.makeText(context, message, status);
        } else {
            toast.cancel();//关闭吐司显示
            toast = Toast.makeText(context, message, status);
        }

        toast.show();//重新显示吐司
    }


    /**
     * 长时间显示Toast
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        checkStatus(context, message,Toast.LENGTH_LONG);
    }


    /**
     * 自定义显示Toast时间
     * @param context
     * @param message
     * @param duration
     */
    public static void showCustom(Context context, CharSequence message, int duration) {
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     * @param context
     * @param message
     * @param duration
     */
    public static void showCustom(Context context, int message, int duration) {
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast的View
     * @param context
     * @param message
     * @param gravity
     */
    public static void showCustom(Context context, CharSequence message, View toastRoot, int gravity){
        Toast toast = new Toast(context);
        toast.setGravity(gravity, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }

    /**
     * 自定义TOP显示Toast
     * @param context
     * @param message
     * @param toastRoot
     */
    public static void showCustomTop(Context context, CharSequence message, View toastRoot){
        showCustom(context, message, toastRoot, Gravity.TOP);
    }

    /**
     * 自定义CENTER显示Toast
     * @param context
     * @param message
     * @param toastRoot
     */
    public static void showCustomCenter(Context context, CharSequence message, View toastRoot){
        showCustom(context,message,toastRoot,Gravity.CENTER);
    }

    /**
     * 自定义BOTTOM显示Toast
     * @param context
     * @param message
     * @param toastRoot
     */
    public static void showCustomBottom(Context context, CharSequence message, View toastRoot){
        showCustom(context,message,toastRoot,Gravity.BOTTOM);
    }
}
