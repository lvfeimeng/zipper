package com.zipper.wallet.base;

import android.app.Application;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.LitePal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
        LitePal.initialize(this);
    }
}
