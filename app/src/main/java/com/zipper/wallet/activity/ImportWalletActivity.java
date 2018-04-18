package com.zipper.wallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.zipper.wallet.R;
import com.zipper.wallet.base.ActivityManager;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.definecontrol.WrapContentHeightViewPager;
import com.zipper.wallet.fragment.MnemonicWordFragment;
import com.zipper.wallet.fragment.PrivateKeyFragment;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.MyLog;
import com.zipper.wallet.utils.MyPagerAdapter;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.utils.Utils;
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
import java.util.Map;

public class ImportWalletActivity extends BaseActivity {

    protected Toolbar toolbar;
    protected WrapContentHeightViewPager viewPager;
    protected MagicIndicator magicIndicator;
    protected CollapsingToolbarLayout collapsingToolbar;

    private String[] tabs;

    private List<Fragment> list;

    final int SAVE_PRIVATE = 100;
    final int TRANSMIT_WORDS = 101;
    final int TRANSMIT_PWD = 103;
    final int ERROR = 404;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object obj = msg.obj;
            hideProgressDialog();
            switch (msg.what) {
                case TRANSMIT_PWD:
                    toast("密码修改成功");
                    PreferencesUtils.putBoolean(mContext, KEY_IS_LOGIN, true, PreferencesUtils.USER);
                    PreferencesUtils.clearData(mContext, PreferencesUtils.VISITOR);
                    finish();
                    ActivityManager.getInstance().finishActivity(UpdatePasActivity.class);
                    break;
                case ERROR:

                    if (obj != null) {

                        toast(obj.toString());
                    } else {
                        toast("生成数据错误");
                    }


                    break;

                case TRANSMIT_WORDS:
                    PreferencesUtils.putBoolean(mContext, KEY_IS_LOGIN, true, PreferencesUtils.USER);
                    PreferencesUtils.clearData(mContext, PreferencesUtils.VISITOR);
                    startActivity(new Intent(mContext, MyWalletActivity.class)
                            .putExtra("isFromImportPage", true));
                    break;

            }
        }
    };

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
        viewPager = (WrapContentHeightViewPager) findViewById(R.id.view_pager);
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        findViewById(R.id.img_back).setOnClickListener(v -> finish());

    }

    private void initViewPager() {
        list = new ArrayList<>();
        list.add(new MnemonicWordFragment());
//        list.add(new OfficialWalletFragment());
//        list.add(new PrivateKeyFragment());
//        list.add(new ObserveFragment());
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), list));
    }

    private void initMagicIndicator() {
        tabs = new String[]{"助记词"};//, "私钥"
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


    /**
     * @param mnemSeed
     */
    public void generateWalletAddress(String randomSeed,String mnemSeed) {
        showProgressDialog("正在导入。。。");
        new Thread() {
            @Override
            public void run() {
                CreateAcountUtils.instance(mContext);

                DeterministicKey master = CreateAcountUtils.CreateRootKey(Utils.hexStringToByteArray(mnemSeed));//生成根公私钥对象

                CreateAcountUtils.saveCoins(master, new RuntHTTPApi.ResPonse() {
                    @Override
                    public void doSuccessThing(Map<String, Object> param) {
                        String firstAddr = param.get("firstAddr").toString();
                        CreateAcountUtils.saveWallet(randomSeed, mnemSeed, firstAddr, new RuntHTTPApi.ResPonse() {
                            @Override
                            public void doSuccessThing(Map<String, Object> param) {


                                MyLog.i(TAG, "mnemonicSeed :" + mnemSeed);
                                MyLog.i(TAG, "512PrivateKey:" + Utils.bytesToHexString(master.getPrivKeyBytes()));
                                MyLog.i(TAG, "512publicKey:" + Utils.bytesToHexString(master.getPubKey()));
                                MyLog.i(TAG, "512PrivateKey33:" + Utils.bytesToHexString(master.getPrivKeyBytes33()));
                                MyLog.i(TAG, "512publicKeyhash:" + Utils.bytesToHexString(master.getPubKeyHash()));
                                MyLog.i(TAG, "512publicKeyExtended:" + Utils.bytesToHexString(master.getPubKeyExtended()));
                                MyLog.i(TAG, "firstAddr:" + firstAddr);
                                Message msg = new Message();
                                if(param.get("success")!=null) {
                                    msg.what = TRANSMIT_PWD;
                                }else{
                                    msg.what = TRANSMIT_WORDS;
                                }
                                mHandler.sendMessage(msg);
                            }

                            @Override
                            public void doErrorThing(Map<String, Object> param) {

                                Message msg = new Message();
                                msg.what = ERROR;
                                msg.obj = param.get("error");
                                mHandler.sendMessage(msg);
                            }
                        });
                    }

                    @Override
                    public void doErrorThing(Map<String, Object> param) {
                        Message msg = new Message();
                        msg.what = ERROR;
                        msg.obj = param.get("error");
                        mHandler.sendMessage(msg);
                    }
                });
            }
        }.start();
    }


}
