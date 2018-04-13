package com.zipper.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.utils.ScreenUtils;

/**
 * Created by AlMn on 2018/04/12.
 */

public class MinerCostTypeDialog extends Dialog {

    private View rootView;
    private ImageView imgClose;
    private TextView textConfirm;
    private RadioGroup radioGroup;

    private Callback callback;

    public MinerCostTypeDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_miner_cost, null);
        initView(rootView);
    }

    private int checkedId = R.id.radio_balance;

    private void initView(View rootView) {
        setContentView(rootView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
        params.width = getContext().getResources().getDisplayMetrics().widthPixels;
        params.height = -2;
        params.bottomMargin = 0;
        rootView.setLayoutParams(params);
        setCanceledOnTouchOutside(true);
        if (getWindow() != null) {
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        }

        imgClose = (ImageView) rootView.findViewById(R.id.img_close);
        textConfirm = (TextView) rootView.findViewById(R.id.text_confirm);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);
        imgClose.setOnClickListener(v -> dismiss());
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            this.checkedId = checkedId;
            radioGroup.check(checkedId);
        });
        textConfirm.setOnClickListener(v -> {
            if (callback == null) {
                return;
            }
            switch (checkedId) {
                case R.id.radio_balance:
                    callback.minerCostType(0, "从余额中扣除");
                    break;
                case R.id.radio_switch:
                    callback.minerCostType(1, "从转账金额中扣除");
                    break;
                default:
                    break;
            }
            dismiss();
        });

    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void minerCostType(int type, String text);
    }

}
