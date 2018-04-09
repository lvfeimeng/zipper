package com.zipper.wallet.definecontrol;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zipper.wallet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/9.
 */

public class TestPopupWindow extends PopupWindow {
    public List<TextView> list= new ArrayList<>();
    public TestPopupWindow(Context context) {
        super(context);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_test,
                null, false);
        setContentView(contentView);
        for(int i = 0 ; i < ((LinearLayout)contentView).getChildCount(); i ++){
            list.add((TextView) ((LinearLayout)contentView).getChildAt(i));
        }

    }

}
