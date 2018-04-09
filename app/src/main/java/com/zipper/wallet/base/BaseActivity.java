package com.zipper.wallet.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.zipper.wallet.R;
import com.zipper.wallet.definecontrol.TitleBarView;
import com.zipper.wallet.dialog.TipDialog;
import com.zipper.wallet.listenear.OnClickListenearAndDo;
import com.zipper.wallet.utils.KeyBoardUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected Dialog alertDialog;
    protected TitleBarView titlebar;
    protected String TAG = "";
    public final String KEY_MNEN_WORDS = "mnemonicwords",
            KEY_IS_LOGIN = "islogin",
            KEY_HAND_PWD = "hand_pwd",
            KEY_WALLET_PWD = "wallet_pwd",
            KEY_WALLET_PWD_TIP = "wallet_pwd_tip",
            INPUT_TEXT = "input_text";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarSetting();
        ActivityManager.getInstance().addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        titlebar = (TitleBarView) findViewById(R.id.title_bar);
        TAG = getLocalClassName();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    /**
     * 提示框
     *
     * @param tip 提示信息
     * @param rp  确定按钮执行的方法
     */
    protected void showTipDialog(final String tip, final RuntHTTPApi.ResPonse rp) {
        new TipDialog(mContext, tip, rp).show();

    }

    /**
     * 提示框
     *
     * @param title 标题
     * @param tip   提示信息
     * @param rp    确定按钮执行的方法
     */
    protected void showTipDialog(final String title, final String tip, final RuntHTTPApi.ResPonse rp) {
        new TipDialog(mContext, title, tip, rp).show();

    }

    /**
     * 提示框
     *
     * @param title 标题
     * @param tip   提示信息
     * @param rp    确定按钮执行的方法
     */
    protected void showTipDialog(final String title, final String tip, final int image, final RuntHTTPApi.ResPonse rp) {
        new TipDialog(mContext, title, tip, TipDialog.TipType.TIP, image, rp).show();

    }

    /**
     * 显示输入型弹框
     *
     * @param title     标题
     * @param hint      默认提示文字
     * @param text      文本
     * @param inputType 输入类型
     * @param Rp        按钮执行的方法
     */
    protected void showInputDialog(String title, String hint, String text, int inputType, final RuntHTTPApi.ResPonse Rp) {

        View dialogView = getLayoutInflater().inflate(R.layout.layout_edittext, null);
        final EditText et = (EditText) dialogView.findViewById(R.id.edit_input);
        et.setHint(hint);
        if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        et.setText(text);
        alertDialog = new AlertDialog.Builder(mContext).setTitle(title)
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                        if (Rp != null) {
                            Rp.doErrorThing(null);
                        }
                    }
                })
                .setPositiveButton("确定", null).create();
        alertDialog.show();
        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListenearAndDo() {
            @Override
            public void doClick(View view) {
                Map map = new HashMap();
                map.put(INPUT_TEXT, et.getText());
                if (Rp != null) {
                    Rp.doSuccessThing(map);
                }
            }
        });
    }


    /**
     * 显示输入型弹框
     *
     * @param title 标题
     * @param tip   文本
     */
    protected void showDoubleButtonDialog(String title, String tip, final RuntHTTPApi.ResPonse Rp, String leftBtnName, String rightBtnName) {

        alertDialog = new AlertDialog.Builder(mContext).setTitle(title).setMessage(tip)
                .setCancelable(false)
                .setNegativeButton((leftBtnName.equals("") ? getString(R.string.cancel) : leftBtnName), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                        ;
                        if (Rp != null) {
                            Rp.doErrorThing(null);
                        }
                    }
                })
                .setPositiveButton(rightBtnName.equals("") ? getString(R.string.ok) : rightBtnName, null).create();
        alertDialog.show();
        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListenearAndDo() {
            @Override
            public void doClick(View view) {
                Map map = new HashMap();
                if (Rp != null) {
                    Rp.doSuccessThing(map);
                }
            }
        });
    }

    public static boolean isNull(TextView view) {
        if (view.getText().equals("") || view.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    ProgressDialog progressDialog;

    protected void showProgressDialog(String tip) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle(tip);
        progressDialog.show();
    }

    protected void hideProgressDialog() {
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
}
