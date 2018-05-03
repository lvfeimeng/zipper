package com.zipper.wallet.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.provider.Settings;

/**
 * Created by Administrator on 2018/4/28.
 */

public class NetworkUtils {
    /**
     * 检测网络是否连接
     *
     * @return true CONNECTING
     */
    public static boolean checkNetworkState(Context context) {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        MyLog.i("NetworkUtils", " checkNetworkState " + manager.getActiveNetworkInfo() + "");
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
            MyLog.i("NetworkUtils", " checkNetworkState " + manager.getActiveNetworkInfo().isAvailable() + "");
        }
        return flag;
    }


    /**
     * 网络未连接时，调用设置方法
     */
    public static void setNetwork(Context context) {

        Intent intent = null;
        /**
         * 判断手机系统的版本！如果API大于10 就是3.0+
         * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
         */
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(Settings.ACTION_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName(
                    "com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }

    /**
     * 网络已经连接，然后去判断是wifi连接还是GPRS连接
     * 设置一些自己的逻辑调用
     */
    public static NetworkType getNetworkType(Context context, ConnectivityManager manager) {
        if (manager == null) {
            return null;
        }
        State gprs = null, wifi = null, bluetooth = null;
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
            gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        }
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) {
            wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        }
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH) != null) {
            bluetooth = manager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).getState();
        }
        if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
            return NetworkType.GPRS;
        }
        //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            return NetworkType.WIFI;
        }
        //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
        if (bluetooth == State.CONNECTED || bluetooth == State.CONNECTING) {
            return NetworkType.BLUETOOTH;
        }
        return NetworkType.NONE;
    }

    public static enum NetworkType {
        WIFI, GPRS, BLUETOOTH, NONE
    }

}
