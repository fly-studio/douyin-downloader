package com.fly.video.downloader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.fly.video.downloader.R;
import com.fly.video.downloader.layout.BottomNavigationViewPagerAdapter;
import com.fly.video.downloader.layout.douyin.UserFragment;
import com.fly.video.downloader.layout.douyin.VideoFragment;
import com.fly.video.downloader.share.Recv;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    Unbinder unbinder;
    VideoFragment videoFragment;
    UserFragment userFragment;
    BottomNavigationViewPagerAdapter pagerAdapter;
    List<Fragment> fragments;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            viewPager.setCurrentItem(id == R.id.navigation_user ? 1 : 0);

            return false;
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        videoFragment = VideoFragment.newInstance();
        userFragment = UserFragment.newInstance(1);
        fragments = new ArrayList<>();
        fragments.add(videoFragment);
        fragments.add(userFragment);


        pagerAdapter = new BottomNavigationViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);

        Recv recv = new Recv(this.getIntent());
        if (recv.isActionSend()) {
            videoFragment.Analyze(recv.getContent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
