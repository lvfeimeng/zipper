package com.zipper.wallet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.zipper.wallet.R;
import com.zipper.wallet.utils.MyLog;

/**
 * Created by Administrator on 2018/4/14.
 */

public class AgreementDialog extends Dialog {

    Context mContext;
    WebView webView;
    String path;

    public AgreementDialog(Context context,String path) {
        super(context, R.style.MyDialog);
        MyLog.i("AgreementDialog","AgreementDialog path:"+path);
        setContentView(R.layout.layout_web);
        mContext = context;
        webView = (WebView)findViewById(R.id.web_view);
        webView.loadUrl(path);
        WebSettings wSet = webView.getSettings();
        wSet.setJavaScriptEnabled(true);
        // 打开本包内asset目录下的index.html文件
    }

}
