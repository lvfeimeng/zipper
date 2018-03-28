package com.zipper.wallet;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zipper.wallet.base.BaseActivity;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Disposable disposable =
                new RxPermissions(this)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {

                                } else {

                                }
                            }
                        });
        disposable.dispose();
    }
}
