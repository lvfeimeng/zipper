package com.zipper.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.adapter.DialogSelectCoinsAdapter;
import com.zipper.wallet.database.CoinInfo;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 收款币种选择弹框
 * Created by AlMn on 2018/04/12.
 */
public class SelectCoinsDialog extends Dialog {

    private View rootView;
    private ImageView imgClose;
    private TextView textConfirm;
    private ListView listView;

    private DialogSelectCoinsAdapter adapter;
    private List<CoinInfo> items;

    private Callback callback;


    public SelectCoinsDialog(@NonNull Context context, List<CoinInfo> list) {
        super(context, R.style.BottomDialog);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_coins, null);
        initView(rootView);
        items = new ArrayList<>();

        if (list != null) {
            items.addAll(list);
        }
        adapter = new DialogSelectCoinsAdapter(getContext(), items);//弹框适配器
        listView.setAdapter(adapter);
    }

    private void initView(View rootView) {
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
        textConfirm = (TextView) rootView.findViewById(R.id.text_confirm);
        listView = (ListView) rootView.findViewById(R.id.list_view);

        imgClose.setOnClickListener(v -> back());//取消
        textConfirm.setOnClickListener(v -> confim());//确认
    }

    private void confim() {
        CoinInfo bean = null;
        for (CoinInfo item : items) {
            if (item.isChecked()) {
                bean = item;
                break;
            }
        }
        if (callback != null) {
            callback.confirmCoinType(bean);
        }
    }

    private void back(){
        dismiss();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void confirmCoinType(CoinInfo bean);
    }

}
