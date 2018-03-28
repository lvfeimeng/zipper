package com.zipper.wallet.definecontrol;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by EDZ on 2018/1/5.
 * 可以直接控制所有子控件是否可点击的LinearLayout
 */
public class ChildClickableLinearLayout extends LinearLayout {
    //子控件是否可以接受点击事件
    private boolean childClickable = true;

    public ChildClickableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildClickableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChildClickableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ChildClickableLinearLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //返回true则拦截子控件所有点击事件，如果childclickable为true，则需返回false
        return !childClickable;
    }

    public void setChildClickable(boolean clickable) {
        childClickable = clickable;
    }

}
