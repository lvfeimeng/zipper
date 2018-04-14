package com.zipper.wallet;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zipper.wallet.animations.MyAnimations;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.listenear.OnClickListenearAndDo;


/**
 * Created by EDZ on 2018/2/2.
 */

public class WebBrowserActivity extends BaseActivity {

    private WebView browser;
    private String url;
    private ProgressBar progressbar;
    private ImageView imgClose;
    private LinearLayout linProgress;
    private View viewPrgress;
    private  int linProgressWidth;
    long exitTime=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setContentView(R.layout.activity_web_browser);
        super.onCreate(savedInstanceState);
        progressbar = (ProgressBar) findViewById(R.id.myProgressBar);

        View imgLeft = findViewById(R.id.imgLeft);
        Log.i(TAG,"imgLeft:"+imgLeft);
        if(imgLeft!=null) {
            imgLeft.setOnClickListener(new OnClickListenearAndDo() {
                @Override
                public void doClick(View view) {onBackKeyDown();}
            });
        }
        url = getIntent().getSerializableExtra(PARAMS_URL)+"";
        ((TextView)findViewById(R.id.txtTitle)).setText(getIntent().getSerializableExtra(PARAMS_TITLE)+"");
        linProgress = (LinearLayout)findViewById(R.id.lin_progressbar);
        viewPrgress = (View)findViewById(R.id.view_progressbar);

        initCompent();
    }
    int count = 100;
    int index = 100;
    private void initCompent(){
        browser = (WebView)findViewById(R.id.browser);
        imgClose = (ImageView)findViewById(R.id.imgClose);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //跳转至拼接好的地址
        Log.i("Runt", "正在跳转页面"+url);
        showProgressDialog("正在跳转页面");
        //mBaseHandler.sendMessage(msg);//http://192.168.5.156:8080/MyFinance/gd16/1.html
        browser.loadUrl(url);
        browser.setWebViewClient(new myWebViewClient());
        browser.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view,final int newProgress) {
                Log.i("onProgressChanged","--newProgress:--"+newProgress);
                Log.i("onProgressChanged","--viewPrgress:--"+viewPrgress.getWidth());
                final LayoutAnimationController.AnimationParameters animation= new LayoutAnimationController.AnimationParameters();   //得到一个LayoutAnimationController对象；
                animation.index =index++ ;
                animation.count = count++ ;
                if (newProgress == 100) {
                    MyAnimations.hideAnimaInSitu(linProgress);
                    MyAnimations.makeViewMove(viewPrgress.getTranslationX(),0,0,0,viewPrgress);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linProgress.setVisibility(View.GONE);
                        }
                    },MyAnimations.ANIMA_TIME);
                } else {

                    if (View.VISIBLE != linProgress.getVisibility()) {
                        MyAnimations.showAnimaInSitu(linProgress);
                        if(linProgressWidth==0){
                            final ViewTreeObserver vto = linProgress.getViewTreeObserver();
                            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                public boolean onPreDraw() {
                                    linProgressWidth = linProgress.getMeasuredWidth();
                                    viewPrgress.setTranslationX(0-linProgressWidth);
                                    linProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                                    return true;
                                }
                            });
                        }else{
                            viewPrgress.setTranslationX(0-linProgressWidth);
                        }

                    }
                    if(linProgressWidth!=0){
                        MyAnimations.makeViewMove(viewPrgress.getTranslationX(),0-linProgressWidth+linProgressWidth/100*newProgress,0,0,viewPrgress,MyAnimations.ANIMA_TIME*3);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyAnimations.makeViewMove(viewPrgress.getTranslationX(),viewPrgress.getTranslationX()+300,0,0,viewPrgress,MyAnimations.ANIMA_TIME*10);
                            }
                        },MyAnimations.ANIMA_TIME*3);
                    }

                }
                super.onProgressChanged(view, newProgress);
            }

        });

        browser.getSettings().setSavePassword(false);
        //Toast.makeText(mContext,"进入浏览器",Toast.LENGTH_SHORT).show();
        String Scale = String.valueOf(browser.getScale());
        Log.i("Runt","--Scale:--"+Scale);
        int screenDensity=getResources().getDisplayMetrics().densityDpi;
        Log.i("Runt", "--screenDensity:--"+String.valueOf(screenDensity));  //60-160-240
    }


    /**
     * 按返回键执行的方法，默认为界面finish
     */
    @Override
    public boolean onBackKeyDown(){
        if(exitTime!=0 && (System.currentTimeMillis()-exitTime) < 1000){
            Toast.makeText(mContext,"哎呦，点慢点！让我缓一会儿。。。",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(browser.canGoBack() && imgClose.getVisibility() == View.VISIBLE){
            browser.goBack();
        }else if(!browser.canGoBack() || browser.canGoBack()&& imgClose.getVisibility() != View.VISIBLE){
            finish();
        }
        if(!browser.canGoBack() && imgClose.getVisibility() == View.VISIBLE){
            MyAnimations.hideReBound(imgClose,mContext);
        }
        exitTime = System.currentTimeMillis();
        return true;
    }


    private class myWebViewClient extends WebViewClient {

        /**
         * 每加载一张图片资源执行一次
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onLoadResource(view, url);
            hideProgressDialog();
            //Log.i("WebView", "onLoadResource "+url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideProgressDialog();
            hideProgressBar();
            //Log.i("WebView", "onPageFinished "+url);
        }

        /**
         * 获取页面跳转的链接
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            //Log.i("UrlLoading", "UrlLoading 正在跳转页面"+url);
            view.loadUrl(url);
            if(browser.canGoBack() && imgClose.getVisibility() != View.VISIBLE){
                MyAnimations.showReBound(imgClose);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
            hideProgressDialog();
            Toast.makeText(mContext, "加载失败，请稍候再试", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        }
    }

    private void hideProgressBar(){
        MyAnimations.hideAnimaInSitu(linProgress);
        MyAnimations.makeViewMove(viewPrgress.getTranslationX(),0,0,0,viewPrgress);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linProgress.setVisibility(View.GONE);
            }
        },MyAnimations.ANIMA_TIME*2);
    }
}
