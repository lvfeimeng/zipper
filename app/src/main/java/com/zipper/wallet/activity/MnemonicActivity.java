package com.zipper.wallet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.animations.MyAnimations;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.FlowLayout;
import com.zipper.wallet.definecontrol.MnemWordsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic);
        flowLayout = (FlowLayout)findViewById(R.id.flowlayout);
        flowLayoutBottom = (FlowLayout)findViewById(R.id.flowlayout_bottom);
        btnOk = (Button)findViewById(R.id.btn_ok);
        linCopy = (LinearLayout)findViewById(R.id.lin_copy);
        linVerify = (LinearLayout)findViewById(R.id.lin_verify);
        txtMnem = (TextView)findViewById(R.id.txt_mnem);

        words = (List<String>) getIntent().getSerializableExtra("list");
        btnOk.setEnabled(true);
        String str = "";
        for(String s : words){
            str += s+"    ";
        }
        txtMnem.setText(str);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            showDoubleButtonDialog("","助记词验证数序正确，是否移除该助记词？",null,getString(R.string.not_remove),getString(R.string.remove));
                        }else{
                            showTipDialog("备份失败，请检查你的助记词",null);
                        }
                    }
                });

            }
        });

        showTipDialog("请勿截图","如果有人获取你的助记词将直接获取你的资产！请抄写下助记词并存放在安全地方.",null);
    }

    public boolean check(){
        for(int i = 0 ; i < words.size() ; i ++ ){
            if(!words.get(i).trim().equals(selectWords.get(i).getText().trim())){
                return false;
            }
        }
        return  true;
    }

    public MnemWordsView createViews(boolean showClose, final String text){
        MnemWordsView mnemWordsView = new MnemWordsView(mContext);
        mnemWordsView.setTxt(text);
        if(!showClose){
            mnemWordsView.setEnable(true);
            mnemWordsView.setLinCloseVisibility(View.INVISIBLE);
        }else{
            mnemWordsView.setEnable(false);
        }

        mnemWordsView.setOnClickListener(itemClick);
        mnemWordsView.setCloseClickListener(itemCloseClick);
        return  mnemWordsView;
    }


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
            }
        }
    };

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
            Log.i(TAG,String.format("a:%s,lo:%s",a,lo));
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

}
