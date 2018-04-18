package com.zipper.wallet.definecontrol;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.animations.MyAnimations;
import com.zipper.wallet.utils.MyLog;

public class MnemWordsView extends RelativeLayout {
    Context mContext;
    LinearLayout linClose;
    TextView txtContent;
    MnemWordsView mnemWordsView ;
    public MnemWordsView(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.item_mnem_words,this);

        initWidget();
    }

    private void initWidget(){
        mnemWordsView =  this;
        linClose = (LinearLayout)findViewById(R.id.lin_close);
        txtContent = (TextView)findViewById(R.id.txt_content);


    }

    public void setCloseClickListener(final OnClickListener onClickListener){
        linClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                linClose.setOnClickListener(null);
                removeThis(onClickListener);
            }
        });

    }

    public void setTxt(String text){
        txtContent.setText(text);
    }

    public void setLinCloseVisibility(int visible){
        linClose.setVisibility(visible);
    }

    public void setEnable(boolean flag){
        txtContent.setEnabled(flag);
    }

    public void setOnClickListener(final OnClickListener onClickListener){
        txtContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                txtContent.setOnClickListener(null);
                //removeThis(onClickListener);

                if(getParent()!=null && mnemWordsView!=null) {
                    ((ViewGroup) getParent()).removeView(mnemWordsView);
                }
                onClickListener.onClick(mnemWordsView);
            }
        });
    }

    public boolean removeThis(final OnClickListener onClickListener){
        MyLog.i("MnemWordsView","removeThis onClickListener:" +onClickListener);
        if(getParent() instanceof ViewGroup){
            MyAnimations.hideReBound(mnemWordsView,mContext);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getParent()!=null && mnemWordsView!=null) {
                                ((ViewGroup) getParent()).removeView(mnemWordsView);
                            }
                            onClickListener.onClick(mnemWordsView);
                        }
                    });
                }
            },MyAnimations.ANIMA_TIME*2);
            return  true;
        }
        return  false;
    }

    public String getText(){
        return txtContent.getText()+"";
    }


}
