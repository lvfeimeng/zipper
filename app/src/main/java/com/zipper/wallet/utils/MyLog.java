package com.zipper.wallet.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/12/5.
 */

public class MyLog {

    public static final int printNum =0;

    public static void i(String tag, String mes){
            i(tag, mes,0);
    }
    public static void i(String tag, String mes, int printNum){
        if(printNum == MyLog.printNum) {
            Log.i(tag, mes);
        }
    }

    public static void d(String tag, String mes){
        d(tag, mes,0);
    }
    public static void d(String tag, String mes, int printNum){
        if(printNum == MyLog.printNum) {
            Log.d(tag, mes);
        }
    }

    public static void e(String tag, String mes){
        e(tag, mes,0);
    }
    public static void e(String tag, String mes, int printNum){
        if(printNum == MyLog.printNum) {
            Log.e(tag, mes);
        }
    }
    public static void e(String tag, String mes, Exception e){
        e(tag, mes,0,e);
    }
    public static void e(String tag, String mes, int printNum, Exception e){
        if(printNum == MyLog.printNum) {
            Log.e(tag, mes,e);
        }
    }

    public static void v(String tag, String mes){
        v(tag, mes,0);
    }
    public static void v(String tag, String mes, int printNum){
        if(printNum == MyLog.printNum) {
            Log.v(tag, mes);
        }
    }

    public static void w(String tag, String mes){
        w(tag, mes,0);
    }
    public static void w(String tag, String mes, int printNum){
        if(printNum == MyLog.printNum) {
            Log.w(tag, mes);
        }
    }


}
