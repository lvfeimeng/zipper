package com.zipper.wallet.definecontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.animations.MyAnimations;

/**
 * Created by Administrator on 2018/3/29.
 */

public class TitleBarView extends LinearLayout {

    RelativeLayout rel;
    LinearLayout linLeft,linRight,linBe,linBottom;
    ImageView imgBack,imgRight;
    TextView txtLeft,txtTitle,txtRight,txtBottmTitle;
    Context context;
    public TitleBarView(Context context) {
        super(context);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.layout_title_bar,this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.titlebar);

        initWidget(typedArray);
    }

    private  void initWidget(TypedArray typedArray){

        rel = (RelativeLayout)findViewById(R.id.rel_back);
        linLeft = (LinearLayout)findViewById(R.id.lin_left);
        linRight = (LinearLayout)findViewById(R.id.lin_right);
        linBe = (LinearLayout)findViewById(R.id.lin_between);
        imgBack = (ImageView)findViewById(R.id.img_back);
        imgRight = (ImageView)findViewById(R.id.img_right);
        txtLeft = (TextView)findViewById(R.id.txt_left);
        txtRight = (TextView)findViewById(R.id.txt_right);
        txtTitle = (TextView)findViewById(R.id.txt_title);
        linBottom = (LinearLayout)findViewById(R.id.lin_bottom);
        txtBottmTitle = (TextView)findViewById(R.id.txt_bottom_title);

        int leftTextColor = typedArray.getColor(R.styleable.titlebar_left_textColor,getResources().getColor(R.color.text_link));
        setLeftTextColor(leftTextColor);
        int rightTextColor = typedArray.getColor(R.styleable.titlebar_right_textColor,getResources().getColor(R.color.text_link));
        setRightTextColor(rightTextColor);
        int titleColor = typedArray.getColor(R.styleable.titlebar_title_textColor,getResources().getColor(R.color.black));
        setTxtTitleColor(titleColor);
        int linColor = typedArray.getColor(R.styleable.titlebar_relbackgroundColor,getResources().getColor(R.color.white));
        setlinBackground(linColor);
        int leftdra = typedArray .getInt(R.styleable.titlebar_lefticon_src,R.mipmap.back_black);
        setLeftIcon(leftdra);
        int visible = typedArray.getInt(R.styleable.titlebar_title_visible,VISIBLE);
        setTitleVisible(visible);
        visible = typedArray.getInt(R.styleable.titlebar_left_visible,VISIBLE);
        setLeftVisible(visible);
        visible = typedArray.getInt(R.styleable.titlebar_right_visible,VISIBLE);
        setRightVisible(visible);
        int rightdra = typedArray .getInt(R.styleable.titlebar_righticon_src,R.mipmap.arrow_right);
        setRightIcon(rightdra);
        String title = typedArray.getString(R.styleable.titlebar_title_text);
        setTxtTitle(title);
        String left = typedArray.getString(R.styleable.titlebar_left_text);
        setLeftText(left);
        String right = typedArray.getString(R.styleable.titlebar_right_text);
        setRightText(right);
        visible = typedArray.getInt(R.styleable.titlebar_right_icon_visible,GONE);
        setRightIconVisible(visible);
        visible = typedArray.getInt(R.styleable.titlebar_left_icon_visible,GONE);
        setLeftIconVisible(visible);
        visible = typedArray.getInt(R.styleable.titlebar_bottom_visible,VISIBLE);
        setBottomVisibility(visible);


    }

    public void setRightIcon(int drawable){
        setRightIcon(drawable,VISIBLE);
    }
    public void setRightIcon(int drawable,int visible){
        imgRight.setImageResource(drawable);
        imgRight.setVisibility(visible);
        if(visible == VISIBLE){
            txtRight.setVisibility(GONE);
        }
        setRightVisible(visible);
    }
    public void setRightVisible(int visible){
        linRight.setVisibility(visible);
    }

    public void setRightIconVisible(int visible){
        imgRight.setVisibility(visible);
        if(visible == VISIBLE){
            txtRight.setVisibility(GONE);
        }
    }

    public void setRightText(String text){
        if(text != null && !text.equals(""))
            setRightText(text,VISIBLE);
    }
    public void setRightText(String text,int visible){
        if(text != null && !text.equals(""))
            txtRight.setText(text);
            txtRight.setVisibility(visible);
            if(visible == VISIBLE){
                setRightIconVisible(GONE);
            }
            setRightVisible(visible);
    }

    public void setRightTextColor(int color){
        txtRight.setTextColor(color);
    }

    public void setTxtTitle(String text){
        if(text != null && !text.equals(""))
            setTxtTitle(text,VISIBLE);
    }

    public void setTxtTitle(String text,int visible){
        if(text != null && !text.equals("")) {
            txtTitle.setText(text);
            txtBottmTitle.setText(text);
            setTitleVisible(visible);
        }else{
            txtTitle.setText("");
            txtBottmTitle.setText("");
            setTitleVisible(GONE);
        }
    }

    public void setTitleVisible(int visible){
        txtTitle.setVisibility(visible);
        setBottomVisibility(visible);

    }
    public void setTxtTitleColor(int color){
        txtTitle.setTextColor(color);
    }

    public void showTitle(){
        MyAnimations.showAnimaInSitu(txtTitle);
    }
    public void hideTitle(){
        MyAnimations.hideAnimaInSitu(txtTitle);
    }

    public void setLeftText(String text){
        if(text != null && !text.equals(""))
            setLeftText(text,VISIBLE);
    }
    public void setLeftText(String text,int visible){
        if(text != null && !text.equals(""))
            txtLeft.setText(text);
            txtLeft.setVisibility(visible);
            if(visible == VISIBLE){
                setLeftIconVisible(GONE);
            }
            setLeftVisible(visible);
    }

    public void setLeftTextColor(int color){
        txtLeft.setTextColor(color);
    }


    public void setLeftIcon(int drawable){
        setLeftIcon(drawable,VISIBLE);
    }

    public void setLeftIcon(int drawable,int visible){
        imgBack.setImageResource(drawable);
        imgBack.setVisibility(visible);
        if(visible == VISIBLE){
            txtLeft.setVisibility(GONE);
        }
        setLeftVisible(visible);
    }

    public void setLeftVisible(int visible){
        linLeft.setVisibility(visible);
    }

    public void setLeftIconVisible(int visible){
        imgBack.setVisibility(visible);
        if(visible == VISIBLE){
            txtLeft.setVisibility(GONE);
        }
    }

    public void setlinBackground(int color){
        rel.setBackgroundColor(color);
    }

    public int getLeftVisibility(){
        return linLeft.getVisibility();
    }

    public int getTitleVisibility(){
        return txtTitle.getVisibility();
    }

    public int getRightVisibility(){
        return linRight.getVisibility();
    }

    public void setLeftEnable(boolean enable){
        ((ChildClickableLinearLayout)linLeft).setChildClickable(enable);

    }

    public void setRightEnable(boolean enable){
        ((ChildClickableLinearLayout)linRight).setChildClickable(enable);
    }

    public boolean getLeftEnable(){
        return linLeft.isEnabled();
    }
    public boolean getRightEnable(){
        return linRight.isEnabled();
    }

    public void setBottomVisibility(int visible){
        linBottom.setVisibility(visible);
    }

    public void setRightOnclickListener(OnClickListener onclick){
        linRight.setOnClickListener(onclick);
    }

    public void setLeftOnclickListener(OnClickListener onclick){
        linLeft.setOnClickListener(onclick);
    }

    public void setTitleOnclickListener(OnClickListener onclick){
        linBe.setOnClickListener(onclick);
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);

    }
}
