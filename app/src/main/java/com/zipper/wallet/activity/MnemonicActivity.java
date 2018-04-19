package com.zipper.wallet.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.animations.MyAnimations;
import com.zipper.wallet.base.ActivityManager;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.FlowLayout;
import com.zipper.wallet.definecontrol.MnemWordsView;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;
import com.zipper.wallet.utils.SqliteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class MnemonicActivity extends BaseActivity {
    FlowLayout flowLayout,flowLayoutBottom;
    LinearLayout linCopy,linVerify;
    TextView txtMnem;
    Button btnOk;
    List<String> words;
    List<MnemWordsView> selectWords = new ArrayList<MnemWordsView>();
    int mode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_mnemonic);
        super.onCreate(savedInstanceState);
        flowLayout = (FlowLayout)findViewById(R.id.flowlayout);
        setFlowLayoutHeight();
        flowLayoutBottom = (FlowLayout)findViewById(R.id.flowlayout_bottom);
        btnOk = (Button)findViewById(R.id.btn_ok);
        linCopy = (LinearLayout)findViewById(R.id.lin_copy);
        linVerify = (LinearLayout)findViewById(R.id.lin_verify);
        txtMnem = (TextView)findViewById(R.id.txt_mnem);
        words = (List<String>) getIntent().getSerializableExtra("list");
        mode = getIntent().getIntExtra("mode",0);
        btnOk.setEnabled(true);
        String str = "";
        for(String s : words){
            str += s+"    ";
        }
        txtMnem.setText(str);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVerify();
            }
        });

        /*Set set = new LinkedHashSet();
        set.add(((List) words));
        PreferencesUtils.putStringSet(mContext,KEY_MNEN_WORDS,set,PreferencesUtils.VISITOR);*/
        switch (mode){
            case 0:
                showTipDialog("请勿截图","如果有人获取你的助记词将直接获取你的资产！请抄写下助记词并存放在安全地方.",R.mipmap.no_photo,null);
                break;
            case 1:
                startVerify();
                break;
        }

    }


    private void startVerify(){
        linCopy.setVisibility(View.GONE);
        linVerify.setVisibility(View.VISIBLE);
        List<String> list = new LinkedList<>();
        for(String str:words){
            list.add(str);
        }
        randomCreateViews(list);
        btnOk.setEnabled(false);
        btnOk.setOnClickListener(null);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()){

                    switch (mode){
                        case 0:
                            showTipDialog("助记词验证顺序正确，进入我的钱包",  getString(R.string.ok), new RuntHTTPApi.ResPonse() {
                                @Override
                                public void doSuccessThing(Map<String, Object> param) {
                                    //delMnemCode();
                                    startNext();
                                }

                                @Override
                                public void doErrorThing(Map<String, Object> param) {
                                    startNext();
                                }
                            });
                            break;
                        case 1:
                            showTipDialog("助记词验证顺序正确","返回上一页", new RuntHTTPApi.ResPonse() {
                                @Override
                                public void doSuccessThing(Map<String, Object> param) {
                                    alertDialog.dismiss();
                                    ActivityManager.getInstance().finishActivity(ExportWalletActivity.class);
                                    finish();
                                }

                                @Override
                                public void doErrorThing(Map<String, Object> param) {
                                }
                            });
                            break;
                    }
                }else{
                    showTipDialog("备份失败，请检查你的助记词",null);
                }
            }
        });
    }

    /**
     * 验证助记词
     *
     * @return
     */
    public boolean check(){
        for(int i = 0 ; i < words.size() ; i ++ ){
            if(!words.get(i).trim().equals(selectWords.get(i).getText().trim())){
                return false;
            }
        }
        return  true;
    }


    /**
     *  //创建助记词控件
     * @param showClose
     * @param text
     * @return
     */
    public MnemWordsView createViews(boolean showClose, final String text){
        MnemWordsView mnemWordsView = new MnemWordsView(mContext);
        mnemWordsView.setTxt(text);
        if(!showClose){
            mnemWordsView.setLinCloseVisibility(View.INVISIBLE);

            mnemWordsView.setEnable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mnemWordsView.setEnable(true);
                        }
                    });
                }
            },MyAnimations.ANIMA_TIME);
        }else{
            mnemWordsView.setEnable(false);
        }

        mnemWordsView.setOnClickListener(itemClick);
        mnemWordsView.setCloseClickListener(itemCloseClick);
        return  mnemWordsView;
    }


    //选择助记词
    View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view instanceof MnemWordsView){
                flowLayout.addView(view);
                MyAnimations.showReBound(view);
                ((MnemWordsView)view).setEnable(false);
                ((MnemWordsView)view).setLinCloseVisibility(View.VISIBLE);
                selectWords.add(((MnemWordsView)view));
                ((MnemWordsView)view).setCloseClickListener(itemCloseClick);
                if(flowLayoutBottom.getChildCount() == 0){
                    btnOk.setEnabled(true);
                }

                List<String> residueWords = new LinkedList<String>();
                for(int i = 0 ; i < flowLayoutBottom.getChildCount(); i ++){
                    residueWords.add (((MnemWordsView)flowLayoutBottom.getChildAt(i)).getText());
                }
                randomCreateViews(residueWords);

                if(flowLayout.getChildCount() > 0 ){
                    setFlowLayoutWrapHeight();
                }
            }
        }
    };


    /**
     //删掉选择的助记词
     *
     */
    View.OnClickListener itemCloseClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view instanceof MnemWordsView){
                List<String> residueWords = new LinkedList<String>();
                for(int i = 0 ; i < flowLayoutBottom.getChildCount(); i ++){
                    residueWords.add (((MnemWordsView)flowLayoutBottom.getChildAt(i)).getText());
                }
                residueWords.add (((MnemWordsView)view).getText());
                randomCreateViews(residueWords);
                /*flowLayoutBottom.addView(view);
                MyAnimations.showReBound(view);
                ((MnemWordsView)view).setEnable(true);
                ((MnemWordsView)view).setLinCloseVisibility(View.INVISIBLE);
                ((MnemWordsView)view).setOnClickListener(itemClick);*/
                btnOk.setEnabled(false);
                selectWords.remove(((MnemWordsView)view));
                if(flowLayout.getChildCount() ==0 ){
                    setFlowLayoutHeight();
                }
            }
        }
    };


    public List<String> split(String words) {
        return new ArrayList<String>(Arrays.asList(words.split("\\s+")));
    }


    public void randomCreateViews(List<String> residue){
        flowLayoutBottom.removeAllViews();
        int size = residue.size();
        for(int i = 0 ; i < size; i ++){
            int a = (int)(Math.random()*100);
            int lo = a%residue.size() == 0? 0:a%residue.size()-1;
            //MyLog.i(TAG,String.format("a:%s,lo:%s",a,lo));
            final String text = residue.get(lo);
            residue.remove(lo);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MnemWordsView view = createViews(false,text);
                            flowLayoutBottom.addView(view);
                            MyAnimations.showReBound(view);
                        }
                    });
                }
            },50*i);
        }
    }


    private void setFlowLayoutHeight(){

        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) ((LinearLayout)flowLayout.getParent()).getLayoutParams();
        linearParams.height=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()));
        ((LinearLayout)flowLayout.getParent()).setLayoutParams(linearParams);
    }
    private void setFlowLayoutWrapHeight(){
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) ((LinearLayout)flowLayout.getParent()).getLayoutParams();
        linearParams.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        ((LinearLayout)flowLayout.getParent()).setLayoutParams(linearParams);
    }


    @Override
    protected boolean onBackKeyDown() {

        switch (mode) {
            case 0:
                startActivity(new Intent(mContext,
                        MyWalletActivity.class));
                ActivityManager.getInstance().finishAllActivity();
                finish();
                break;
            case 1:
                onBackPressed();
                break;
        }

        return true;
    }

    private  void delMnemCode(){
        PreferencesUtils.putBoolean(mContext, KEY_IS_LOGIN, true, PreferencesUtils.USER);
        ContentValues values = new ContentValues();
        //在values中添加内容
        values.put("mnem_seed", "");
        SqliteUtils.update("walletinfo", values, "name=?", new String[]{PreferencesUtils.getString(mContext, KEY_WALLET_NAME, PreferencesUtils.VISITOR)});

    }

    private void startNext(){

        startActivity(new Intent(mContext,
                MyWalletActivity.class));
        ActivityManager.getInstance().finishAllActivity();
        finish();
        alertDialog.dismiss();
    }

}
