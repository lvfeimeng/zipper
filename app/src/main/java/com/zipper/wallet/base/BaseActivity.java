package com.zipper.wallet.base;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zipper.wallet.R;
import com.zipper.wallet.definecontrol.TitleBarView;
import com.zipper.wallet.dialog.InputDialog;
import com.zipper.wallet.dialog.TipDialog;
import com.zipper.wallet.utils.KeyBoardUtils;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.ScreenUtils;
import com.zipper.wallet.utils.SqliteUtils;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.net.HttpURLConnection;

import io.reactivex.functions.Consumer;

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected Dialog alertDialog;
    protected TitleBarView titlebar;
    protected String TAG = "";
    public static final String KEY_MNEN_WORDS = "mnemonicwords",
            KEY_IS_LOGIN = "islogin",
            KEY_WALLET_NAME = "wallet_name",
            KEY_HAND_PWD = "hand_pwd",
            KEY_WALLET_PWD = "wallet_pwd",
            KEY_WALLET_PWD_TIP = "wallet_pwd_tip",
            INPUT_TEXT = "input_text";
    public static final String PARAMS_TITLE = "title";
    public static final String PARAMS_URL = "url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWritePermission();
        statusBarSetting();
        ActivityManager.getInstance().addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        SqliteUtils.openDataBase(mContext);

        titlebar = (TitleBarView) findViewById(R.id.title_bar);
        if (titlebar != null) {
            titlebar.setLeftOnclickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackKeyDown();
                }
            });
        }
        TAG = getLocalClassName();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        HttpURLConnection con = null;
        try {
            // some code
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public void putString(String key, String value) {
        PreferencesUtils.putString(mContext, key, value, PreferencesUtils.USER);
    }

    public String getString(String key) {
        return PreferencesUtils.getString(mContext, KEY_IS_LOGIN, key);
    }

    public void requestWritePermission() {
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    LitePalDB lite = new LitePalDB("ZipperOne", 2);
                    if (granted) {
                        lite.setExternalStorage(true);
                        lite.setStorage("zipper_one/database");
                    } else {
                        //toast("请授予写入权限");
                        lite.setExternalStorage(false);
                    }
                });
    }

    public int getColorById(int colorId) {
        return getResources().getColor(colorId);
    }

    @Override
    public void finish() {
        KeyBoardUtils.closeKeybord(getWindow().getDecorView(), this);
        ActivityManager.activityStack.remove(this);
        super.finish();
    }

    public int dp2px(int dpVal) {
        return ScreenUtils.dp2px(this, dpVal);
    }

    public void toast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }


    public View inflate(int layoutId) {
        return LayoutInflater.from(this).inflate(layoutId, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    public void showTipDialog(String tip, RuntHTTPApi.ResPonse rp) {
        showTipDialog(tip, "OK", rp);
    }

    public void showTipDialog(String tip, String right, RuntHTTPApi.ResPonse rp) {
        showTipDialog(tip, null, right, rp);
    }

    public void showTipDialog(String title, String tip, int img, RuntHTTPApi.ResPonse rp) {
        showTipDialog(title, tip, "", "OK", img, rp);
    }

    public void showTipDialog(String tip, String left, String right, RuntHTTPApi.ResPonse rp) {
        showTipDialog(null, tip, left, right, rp);

    }

    public void showTipDialog(String title, String tip, String left, String right, RuntHTTPApi.ResPonse rp) {
        showTipDialog(title, tip, left, right, 0, rp);
    }

    public void showTipDialog(String title, String tip, String left, String right, int img, RuntHTTPApi.ResPonse rp) {
        showTipDialog(title, tip, left, right, img, TipDialog.TipType.TIP, rp);
    }

    public void showTipDialog(String title, String tip, String left, String right, int image, TipDialog.TipType tipType, RuntHTTPApi.ResPonse rp) {
        alertDialog = new TipDialog(mContext, title, tip, left, right, image, tipType, rp);
        alertDialog.show();
    }

    /**
     * 显示输入型弹框
     *
     * @param Rp 按钮执行的方法
     */
    public void showInputDialog(String tip, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog("", tip, Rp);
    }

    public void showInputDialog(String tip, int inputType, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog("", tip, inputType, Rp);

    }

    public void showInputDialog(String title, String tip, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, "", Rp);

    }

    public void showInputDialog(String title, String tip, int inputType, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, "", inputType, Rp);

    }

    public void showInputDialog(String title, String tip, String hint, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, hint, "", Rp);

    }

    public void showInputDialog(String title, String tip, String hint, int inputType, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, hint, "", inputType, Rp);

    }

    public void showInputDialog(String title, String tip, String hint, String text, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, hint, text, "OK", Rp);

    }

    public void showInputDialog(String title, String tip, String hint, String text, int inputType, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, hint, text, "OK", inputType, Rp);
    }


    public void showInputDialog(String title, String tip, String hint, String text, String right, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, hint, text, "", right, Rp);
    }

    public void showInputDialog(String title, String tip, String hint, String text, String right, int inputType, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, hint, text, "", right, inputType, Rp);
    }

    public void showInputDialog(String title, String tip, String hint, String text, String left, String right, final RuntHTTPApi.ResPonse Rp) {
        showInputDialog(title, tip, hint, text, left, right, InputType.TYPE_TEXT_VARIATION_NORMAL, Rp);
    }

    public void showInputDialog(String title, String tip, String hint, String text, String left, String right, int inputType, final RuntHTTPApi.ResPonse Rp) {
        alertDialog = new InputDialog(mContext, title, tip, hint, text, left, right, inputType, Rp);
        alertDialog.show();
    }

    public static boolean isNull(TextView view) {
        if (view.getText().equals("") || view.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    ProgressDialog progressDialog;

    public void showProgressDialog(String tip) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle(tip);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.cancel();
    }

    private ImmersionBar mImmersionBar = null;

    public void setTransparentStatusBar() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(false)
                .keyboardEnable(true)
                .init();
    }

    public void setTransparentStatusBar(boolean fits) {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(fits)
                .statusBarColor(R.color.transparent)
                .keyboardEnable(true)
                .init();
    }

    public void setDefaultStatusBar() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(true)
                .statusBarColor(R.color.default_status_bar)
                .keyboardEnable(true)
                .init();
    }

    public void setDarkFontStatusBar() {
        if (mImmersionBar == null) {
            mImmersionBar = ImmersionBar.with(this);
        }
        mImmersionBar.fitsSystemWindows(true)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .keyboardEnable(true)
                .init();
    }

    public void statusBarSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setDarkFontStatusBar();
        } else {
            setDefaultStatusBar();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyBoardUtils.closeKeybord(mContext);
        if (alertDialog != null) {

            alertDialog.dismiss();
            alertDialog = null;
        }
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    protected boolean onBackKeyDown() {
        onBackPressed();
        return false;
    }


    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MENU:
                break;
            case KeyEvent.KEYCODE_BACK:
                return onBackKeyDown();
        }
        return super.onKeyDown(keycode, event);
    }
}
