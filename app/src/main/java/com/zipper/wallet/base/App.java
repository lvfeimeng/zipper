package com.zipper.wallet.base;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
    }
}
