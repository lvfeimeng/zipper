package com.zipper.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.bean.SwitchAccountBean;
import com.zipper.wallet.utils.ScreenUtils;

/**
 * Created by AlMn on 2018/04/11.
 */

public class ConfirmSwitchAccountDialog extends Dialog {

    private View rootView;
    private ImageView imgClose;
    private TextView textTotalAmount;
    private TextView textUnit;
    private TextView textPayerAddress;
    private TextView textPayeeAddress;
    private TextView textMinerCost;
    private TextView textUnit2;
    private TextView textRemark;
    private TextView textRealAmount;
    private TextView textUnit3;
    private Button btnConfirm;
    private ConfirmCallback callback;
    private SwitchAccountBean bean;

    public ConfirmSwitchAccountDialog(@NonNull Context context, SwitchAccountBean bean) {
        super(context, R.style.BottomDialog);
        this.bean = bean;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_switch, null);
        setContentView(rootView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
        params.width = getContext().getResources().getDisplayMetrics().widthPixels;
        params.height = ScreenUtils.getScreenHeight(getContext()) - ScreenUtils.dp2px(getContext(), 150);
        params.bottomMargin = 0;
        rootView.setLayoutParams(params);
        setCanceledOnTouchOutside(true);
        if (getWindow() != null) {
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        }
        imgClose = (ImageView) rootView.findViewById(R.id.img_close);
        textTotalAmount = (TextView) rootView.findViewById(R.id.text_total_amount);
        textUnit = (TextView) rootView.findViewById(R.id.text_unit);
        textPayerAddress = (TextView) rootView.findViewById(R.id.text_payer_address);
        textPayeeAddress = (TextView) rootView.findViewById(R.id.text_payee_address);
        textMinerCost = (TextView) rootView.findViewById(R.id.text_miner_cost);
        textUnit2 = (TextView) rootView.findViewById(R.id.text_unit2);
        textRemark = (TextView) rootView.findViewById(R.id.text_remark);
        textRealAmount = (TextView) rootView.findViewById(R.id.text_real_amount);
        textUnit3 = (TextView) rootView.findViewById(R.id.text_unit3);
        btnConfirm = (Button) rootView.findViewById(R.id.btn_confirm);
        imgClose.setOnClickListener(v -> dismiss());
        btnConfirm.setOnClickListener(v -> submit());
        textTotalAmount.setText(bean.getTotalAmount());
        textRealAmount.setText(bean.getRealAmount());
        textUnit.setText(bean.getType());
        textUnit2.setText(bean.getType());
        textUnit3.setText(bean.getType());
        textPayerAddress.setText(bean.getPayerAddress());
        String name = bean.getPayeeName();
        if(name == null || name.equals("") || name.equals("null")) {
            textPayeeAddress.setText(bean.getPayeeAddress());
        }else{
            textPayeeAddress.setText("【" + bean.getPayeeName() + "】" + bean.getPayeeAddress());
        }
        textRemark.setText(bean.getRemark());
        textMinerCost.setText(bean.getMinerCost());
    }

    private void submit() {
        if (callback != null) {
            callback.confirmInfo();
            dismiss();
        }
    }

    public void handleResult(ConfirmCallback callback) {
        this.callback = callback;
    }

    public interface ConfirmCallback {
        void confirmInfo();
    }
}
