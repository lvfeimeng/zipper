package com.zipper.wallet.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.zipper.wallet.R;
import com.zipper.wallet.listenear.OnClickListenearAndDo;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EDZ on 2018/1/29.
 */

public class InputDialog {
    static AlertDialog alertDialog;
    public static AlertDialog create(LayoutInflater inflater,
                                    Context context ,
                                     String title ,
                                     final RuntHTTPApi.ResPonse cancelRp,
                                     final RuntHTTPApi.ResPonse okRp){

        View dialogView = inflater.inflate(R.layout.layout_edittext,null);
        final EditText et = (EditText)dialogView.findViewById(R.id.edit_input);
        et.setHint("请输入发送到手机的验证码");
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialog = new AlertDialog.Builder(context).setTitle(title )
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();;
                        if(cancelRp !=null) {
                            cancelRp.doErrorThing(null);
                        }
                    }
                })
                .setPositiveButton("确定", null).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListenearAndDo() {
            @Override
            public void doClick(View view) {
                Map map = new HashMap();
                map.put("view",et.getText());
                okRp.doSuccessThing(map);
            }
        });
        return alertDialog;
    }

}
