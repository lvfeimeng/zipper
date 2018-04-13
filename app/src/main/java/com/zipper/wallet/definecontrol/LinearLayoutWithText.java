package com.zipper.wallet.definecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.listenear.OnClickListenearAndDo;
import com.zipper.wallet.utils.KeyBoardUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EDZ on 2018/1/24.
 */
public class LinearLayoutWithText extends LinearLayout {
    Context context;
    TextView title,right,redPoint,rightRedPoint;
    LinearLayout lin,father;
    View viewDivider;
    public LinearLayoutWithText(Context context) {
        super(context);
    }

    public LinearLayoutWithText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.item_btn_text_with_text,this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearLayoutWithText);

        initWidget(typedArray);
    }

    private void initWidget(TypedArray typedArray)
    {
        title = (TextView) findViewById(R.id.txt_left);
        right = (TextView) findViewById(R.id.txt_right);
        redPoint = (TextView) findViewById(R.id.txt_red_point);
        rightRedPoint = (TextView) findViewById(R.id.txt_right_red_point);
        lin = (LinearLayout)findViewById(R.id.lin_item_text);
        viewDivider = findViewById(R.id.view_divider);
        father = (LinearLayout)lin.getParent();
        lin.setId(((LinearLayout)father.getParent()).getId());
        String title = typedArray.getString(R.styleable.LinearLayoutWithText_text_title);
        setTitle(title);
        String hinttitle = typedArray.getString(R.styleable.LinearLayoutWithText_hint_title);
        setTitleHint(hinttitle);
        String hintRight = typedArray.getString(R.styleable.LinearLayoutWithText_hint_right);
        setRightHint(hintRight);

        int hintTitleColor = typedArray.getColor(R.styleable.LinearLayoutWithText_hint_titleColor,getResources().getColor(R.color.text_major));
        setTitleHintColor(hintTitleColor);
        int hintRightColor = typedArray.getColor(R.styleable.LinearLayoutWithText_hint_rightColor,getResources().getColor(R.color.text_major));
        setRightHintColor(hintRightColor);
        int redPointCount = typedArray.getInt(R.styleable.LinearLayoutWithText_red_poit_count,-1);
        if(redPointCount == 0){
            rightRedPoint.setVisibility(VISIBLE);
        }else if(redPointCount > 0 &&redPointCount<10){
            redPoint.setVisibility(VISIBLE);
            redPoint.setText(redPointCount+"");
        }else if(redPointCount > 9){
            redPoint.setVisibility(VISIBLE);
            redPoint.setText("9+");
        }
        float rightSize = typedArray.getInt(R.styleable.LinearLayoutWithText_text_rightSize,17);
        //Log.i("LinearLayoutWithText","rightSize:"+rightSize);
        setRightSize(rightSize);
        float titleSize = typedArray.getInt(R.styleable.LinearLayoutWithText_text_titleSize,17);
        //Log.i("LinearLayoutWithText","titleSize:"+titleSize);
        setTitleSize(titleSize);
        int  visible = typedArray.getInt(R.styleable.LinearLayoutWithText_divider_visible,VISIBLE);
        setDividerVisibility(visible);
        String str = typedArray.getString(R.styleable.LinearLayoutWithText_text_right);
        setRightText(str);
        int bgColor = typedArray.getColor(R.styleable.LinearLayoutWithText_linbackgroundColor,getResources().getColor(R.color.white));
        setBackGround(bgColor);
        int titleColor = typedArray.getColor(R.styleable.LinearLayoutWithText_text_titleColor,getResources().getColor(R.color.text_major));
        setTitleColor(titleColor);
        int rightColor = typedArray.getColor(R.styleable.LinearLayoutWithText_text_rightColor,getResources().getColor(R.color.text_major));
        setRightColor(rightColor);
        boolean enable = typedArray .getBoolean(R.styleable.LinearLayoutWithText_enable,true);
        setEnable(enable);
        Drawable leftdra = typedArray .getDrawable(R.styleable.LinearLayoutWithText_leftCompoundDrawable);
        int  leftdraSize = typedArray.getInt(R.styleable.LinearLayoutWithText_leftCompoundDrawableSize,1);
        setLeftCompoundDrawables(leftdra,leftdraSize);
        Drawable rightdra = typedArray .getDrawable(R.styleable.LinearLayoutWithText_rightCompoundDrawable);
        setRightCompoundDrawables(rightdra);
    }

    /**
     * 设置左侧文字 对应属性 text_title
     * @param text
     */
    public void setTitle(String text){ title.setText(text); }

    /**
     * 设置右侧文字 对应属性 text_right
     * @param text
     */
    public void setRightText(String text){
        right.setText(text);
    }
    public void setRightHint(String text){
        right.setHint(text);
    }
    public void setTitleHint(String text){
        title.setHint(text);
    }
    public void setRightHintColor(int color){
        right.setHintTextColor(color);
    }
    public void setTitleHintColor(int color){title.setHintTextColor(color); }

    public String getTitleText(){
        return title.getText().toString();
    }
    public String getRightText(){ return right.getText().toString(); }
    public TextView getRightTextView(){ return right; }
    public void setBackGround(int color){
        father.setBackgroundColor(color);
    }

    /**
     *
     * 设置 textview 左标签icon  对应属性leftCompoundDrawable
     */
    public void setLeftCompoundDrawables(final Drawable left){
        setLeftCompoundDrawables(left,1);

    }

    /**
     *
     * 设置 textview 左标签icon  对应属性leftCompoundDrawable
     *
     * @param left
     * @param drawablesize 设置图片是正常的几倍
     */
    public void setLeftCompoundDrawables(final Drawable left ,final double drawablesize){
        if(left!=null){
            final int w = left.getIntrinsicWidth();
            final int h = left.getIntrinsicHeight();

            final ViewTreeObserver vto = title.getViewTreeObserver();

            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    int height = title.getMeasuredHeight();
                    int width = title.getMeasuredWidth();

                    //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(linNoreal.getLayoutParams());
                    double size = w/(double)h;
                    int ww = (int) (height*drawablesize*size);
                    left.setBounds(0, 0, ww, (int)(height*drawablesize));
                    title.setCompoundDrawablePadding(15);
                    Log.i("LinearLayoutWithText","title.Height:"+height+" Drawable.height"+ww+" "+(int)(height*drawablesize));

                    title.getViewTreeObserver().removeOnPreDrawListener(this);
                    title.setCompoundDrawables(left,null,null,null);
                    return true;
                }
            });
        }

    }

    /**
     *
     * 设置Textview右侧标签icon  对应属性  rightCompoundDrawable
     */
    public void setRightCompoundDrawables(Drawable drawable){
        Log.i("LinearLayoutWithText","setRightCompoundDrawables rightdra:"+drawable);
        if(drawable!=null) {
            final int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            if(rightRedPoint.getVisibility() == VISIBLE){
                this.right.setCompoundDrawablePadding(50);
            }
            drawable.setBounds(0, 0, w, h);
        }
        this.right.setCompoundDrawables(null, null, drawable, null);
    }

    /**
     *
     * 设置 是否可点击  对应属性  enable
     */
    public void setEnable(boolean flag){
        lin.setEnabled(flag);
        lin.setClickable(flag);
    }


    /**
     *
     * 设置 点击事件
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        Log.i("LinearLayoutWithText","setOnClickListener "+l+" father"+father);
        lin.setOnClickListener(l);
    }

    /**
     *
     * 设置左侧文字颜色 对应属性 text_titleColor
     * @param color
     */
    public void setTitleColor(int color){
        title.setTextColor(color);
    }

    /**
     *
     * 设置右侧文字颜色 对应属性 text_rightColor
     * @param color
     */
    public void setRightColor(int color){
        right.setTextColor(color);
    }


    /**
     *
     * 设置 右侧Textview文字变更  对应属性
     */
    public void addTextChangedListener(final RuntHTTPApi.ResPonse rp){
        right.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("LinearLayoutWithText","addTextChangedListener text"+right.getText()+" editable"+editable);
                Map map = new HashMap();
                map.put("text",right.getText());
                rp.doSuccessThing(map);
            }
        });
    }


    /**
     *
     * 设置弹出输入框，输入文字事件
     */
    public void setInputOnclickListener(OnClickListenearAndDo onclickListener){
        lin.setOnClickListener(onclickListener);
    }

    public void setRightSize(float size){
        right.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }
    public void setTitleSize(float size){
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }
    public void setDividerVisibility(int visible){
        viewDivider.setVisibility(visible);
    }

}
