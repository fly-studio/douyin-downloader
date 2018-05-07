package com.fly.video.downloader.layout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/5/7.
 */

public class BottomNavigationViewPagerAdapter extends FragmentPagerAdapter {
    //fragment 集合
    private List<Fragment> fragments;

    public BottomNavigationViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;

    }
    //返回 fragments 中的一个 Fragment
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    //Fragment数量
    @Override
    public int getCount() {
        return  fragments.size();
    }
}
