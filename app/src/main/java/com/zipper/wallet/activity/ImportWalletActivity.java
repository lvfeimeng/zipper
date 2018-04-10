package com.zipper.wallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.fragment.MnemonicWordFragment;
import com.zipper.wallet.fragment.PrivateKeyFragment;
import com.zipper.wallet.utils.MyPagerAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class ImportWalletActivity extends BaseActivity {

    protected Toolbar toolbar;
    protected ViewPager viewPager;
    protected MagicIndicator magicIndicator;
    protected CollapsingToolbarLayout collapsingToolbar;

    private String[] tabs;

    private List<Fragment> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        collapsingToolbar.setTitle("导入钱包");
        initMagicIndicator();
        initViewPager();
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        findViewById(R.id.img_back).setOnClickListener(v -> finish());

        AppBarLayout.LayoutParams
        layoutParams = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//        int scrollFlags = layoutParams.getScrollFlags();
        layoutParams.setScrollFlags(0);

//        AppBarLayout.LayoutParams
//                layoutParams = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//        layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
//                | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);//SCROLL_FLAG_ENTER_ALWAYS
//        collapsingToolbar.setLayoutParams(layoutParams);
    }

    private void initViewPager() {
        list = new ArrayList<>();
        list.add(new MnemonicWordFragment());
//        list.add(new OfficialWalletFragment());
        list.add(new PrivateKeyFragment());
//        list.add(new ObserveFragment());
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
    }

    private void initMagicIndicator() {
        tabs = new String[]{"助记词", "私钥"};
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return tabs == null ? 0 : tabs.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(getColorById(R.color.black));
                colorTransitionPagerTitleView.setSelectedColor(getColorById(R.color.raido_color_checked));
                colorTransitionPagerTitleView.setText(tabs[index]);
                colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(dp2px(2));
                indicator.setColors(getColorById(R.color.raido_color_checked));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
    }

}
