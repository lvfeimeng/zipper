package com.zipper.wallet.definecontrol;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/9.
 */

public class TestPopupWindow extends PopupWindow {

    public RadioGroup radioGroup = null;

    public TestPopupWindow(Context context) {
        super(context);
        //popup_test
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_property,
                null, false);
        int width = ScreenUtils.dp2px(context, 150);
        setContentView(contentView);
        setHeight(-2);
        setWidth(width);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radioGroup = contentView.findViewById(R.id.radio_group);
    }

}
