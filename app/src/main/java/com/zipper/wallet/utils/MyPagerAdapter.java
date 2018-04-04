package com.zipper.wallet.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by AlMn on 2016/7/15 015.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fs;

    public MyPagerAdapter(FragmentManager fm, List<Fragment> fs) {
        super(fm);
        this.fs = fs;
    }

    @Override
    public Fragment getItem(int position) {
        return fs.get(position);
    }

    @Override
    public int getCount() {
        return fs.size();
    }


}
