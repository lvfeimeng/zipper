package com.zipper.wallet.listenear;

import android.os.Handler;
import android.view.View;

/**
 * Created by EDZ on 2018/1/25.
 */

public abstract class OnClickListenearAndDo implements View.OnClickListener {
    @Override
    public void onClick(final View view) {
        view.setEnabled(false);
        view.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
                view.setClickable(true);

            }
        },1000);
        doClick(view);
    }

    public abstract void doClick(View v);
}
