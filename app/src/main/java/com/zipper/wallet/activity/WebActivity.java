package com.zipper.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.NestedWebView;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.Map;

public class WebActivity extends BaseActivity {

    protected ImageView imgBack;
    protected TextView textRight;
    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected NestedWebView webView;
    protected Button button;
    protected RelativeLayout layout;

    private int type = 0;
    private String title = "";
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_web);
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", 0);
        }
        initView();
        initData();
    }

    private void initData() {
        showProgressDialog("正在跳转页面");
        switch (type) {
            case 1:
                title = "用户服务协议";
                url = "file:///android_asset/agreement.html";
                break;
            case 2:
                title = "常见问题说明";
                url = "file:///android_asset/description.html";
                layout.setVisibility(View.VISIBLE);
                textRight.setText("跳过");
                textRight.setVisibility(View.VISIBLE);
                break;
            case 3:
                title = "什么是矿工费用";
                url = "file:///android_asset/expense.html";
                break;
            default:
                break;
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    hideProgressDialog();
                }
            }
        });
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        collapsingToolbar.setTitle(title);
    }

    private void initView() {
        imgBack = (ImageView) findViewById(R.id.img_back);
        textRight = (TextView) findViewById(R.id.text_right);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        webView = (NestedWebView) findViewById(R.id.web_view);
        button = (Button) findViewById(R.id.button);

        setSupportActionBar(toolbar);
        imgBack.setOnClickListener(v -> {
            if (type == 2) {
                showTipDialog("放弃创建钱包", "钱包还未创建成功，是否放弃？", "继续", "放弃", new RuntHTTPApi.ResPonse() {
                    @Override
                    public void doSuccessThing(Map<String, Object> param) {
                        PreferencesUtils.clearData(mContext, PreferencesUtils.VISITOR);
                        finish();
                    }

                    @Override
                    public void doErrorThing(Map<String, Object> param) {
                        alertDialog.dismiss();
                    }
                });
            } else {
                finish();
            }
        });
        button.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CreateAccountActivity.class);
            startActivity(intent);
            finish();
        });
        textRight.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CreateAccountActivity.class));
            finish();
        });
        layout = (RelativeLayout) findViewById(R.id.layout);
    }
}
