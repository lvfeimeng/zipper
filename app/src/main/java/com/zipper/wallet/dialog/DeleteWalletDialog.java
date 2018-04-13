package com.zipper.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.utils.ScreenUtils;

/**
 * Created by AlMn on 2018/04/12.
 */

public class DeleteWalletDialog extends Dialog {

    protected View rootView;
    protected TextView textNo;
    protected TextView textYes;

    private Callback callback;

    public DeleteWalletDialog(@NonNull Context context) {
        super(context);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_wallet, null);
        initView(rootView);
    }

    private void initView(View rootView) {
        setContentView(rootView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
        //params.width = getContext().getResources().getDisplayMetrics().widthPixels;
        params.width = ScreenUtils.dp2px(getContext(),280);
        params.height = -2;
        rootView.setLayoutParams(params);
        setCanceledOnTouchOutside(true);
        if (getWindow()!=null) {
            getWindow().setBackgroundDrawable(null);
        }

        textNo = (TextView) rootView.findViewById(R.id.text_no);
        textYes = (TextView) rootView.findViewById(R.id.text_yes);
        textNo.setOnClickListener(v -> dismiss());
        textYes.setOnClickListener(v -> {
            if (callback != null) {
                callback.delete();
            }
            dismiss();
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void delete();
    }

}
