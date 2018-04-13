package com.zipper.wallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.WrapContentHeightViewPager;
import com.zipper.wallet.fragment.ExportWalletFragment;
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

public class ExportWalletActivity extends BaseActivity {

    protected Toolbar toolbar;
    protected WrapContentHeightViewPager viewPager;
    protected MagicIndicator magicIndicator;
    protected CollapsingToolbarLayout collapsingToolbar;

    private String[] tabs;

    private List<Fragment> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_wallet);
        initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        collapsingToolbar.setTitle("导出钱包");
        initMagicIndicator();
        initViewPager();
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (WrapContentHeightViewPager) findViewById(R.id.view_pager);
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        findViewById(R.id.img_back).setOnClickListener(v -> finish());

    }

    private void initViewPager() {
        list = new ArrayList<>();
        //0-备份助记词,1-导出明文私钥，20-导出加密私钥
        list.add(ExportWalletFragment.newInstance(0));
        list.add(ExportWalletFragment.newInstance(1));
        list.add(ExportWalletFragment.newInstance(2));
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
        viewPager.setOffscreenPageLimit(3);
    }

    private void initMagicIndicator() {
        tabs = new String[]{"导出助记词", "导出明文私钥","导出加密私钥"};
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
                colorTransitionPagerTitleView.setSelectedColor(getColorById(R.color.text_link));
                colorTransitionPagerTitleView.setText(tabs[index]);
                colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(dp2px(2));
                indicator.setColors(getColorById(R.color.text_link));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
    }

}
