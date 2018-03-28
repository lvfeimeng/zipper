package com.zipper.wallet.definecontrol;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by EDZ on 2018/1/5.
 * 可以直接控制所有子控件是否可点击的RelativeLayout
 */

public class ChildClickableRelativeLayout extends RelativeLayout {
    //子控件是否可以接受点击事件
    private boolean childClickable = true;

    public ChildClickableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildClickableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChildClickableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ChildClickableRelativeLayout(Context context) {
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
