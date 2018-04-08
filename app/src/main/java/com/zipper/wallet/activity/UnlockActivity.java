package com.zipper.wallet.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.UnlockView;

/**
 * Created by Administrator on 2018/4/4.
 */

public class UnlockActivity extends BaseActivity {
    private UnlockView mUnlockView;
    private String pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_lock);
        super.onCreate(savedInstanceState);

        mUnlockView= (UnlockView) findViewById(R.id.unlock);
        mUnlockView.setMode(UnlockView.CREATE_MODE);
        mUnlockView.setGestureListener(new UnlockView.CreateGestureListener() {
            @Override
            public void onGestureCreated(String result) {
                pwd=result;
                Toast.makeText(mContext,"Set Gesture Succeeded!",Toast.LENGTH_SHORT).show();
                mUnlockView.setMode(UnlockView.CHECK_MODE);
            }
        });
        mUnlockView.setOnUnlockListener(new UnlockView.OnUnlockListener() {
            @Override
            public boolean isUnlockSuccess(String result) {
                if(result.equals(pwd)){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void onSuccess() {
                Toast.makeText(mContext,"Check Succeeded!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(mContext,"Check Failed!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
