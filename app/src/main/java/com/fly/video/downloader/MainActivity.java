package com.fly.video.downloader;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.fly.video.downloader.layout.fragment.UserFragment;
import com.fly.video.downloader.layout.fragment.VideoFragment;
import com.fly.video.downloader.layout.fragment.VideoSearchFragment;
import com.fly.video.downloader.util.Recv;


import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    private Unbinder unbinder;
    protected VideoFragment videoFragment;
    protected UserFragment userFragment;
    protected VideoSearchFragment searchFragment = null;

    private Date backPressAt = null;
    private boolean fromSend = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(userFragment).hide(videoFragment);
            if (id == R.id.navigation_user) ft.show(userFragment); else ft.show(videoFragment);
            ft.commit();

            return true;
        }
    };

    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);

        videoFragment = VideoFragment.newInstance();
        userFragment = UserFragment.newInstance(1);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.viewpager, videoFragment).add(R.id.viewpager, userFragment).hide(userFragment).show(videoFragment).commit();

        fromSend = this.getIntent() != null && Intent.ACTION_SEND.equals(this.getIntent().getAction());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        // 最后一次 并且大于2秒
        if (fm.getBackStackEntryCount() == 0 && !fromSend) {
            if (backPressAt == null || new Date().getTime() - backPressAt.getTime() > 2000) {
                Toast.makeText(this, R.string.one_more_exit, Toast.LENGTH_SHORT).show();
                backPressAt = new Date();
                return;
            } else {
                super.onBackPressed();
            }
        }

        if (fm.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry back = fm.getBackStackEntryAt(fm.getBackStackEntryCount() -1);
            switch (back.getName())
            {
                case "video":
                    showFragment(videoFragment);
                    break;
            }

            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void showVideoSearchFragment()
    {
        if (null == searchFragment)
            searchFragment = VideoSearchFragment.newInstance();

        if (!searchFragment.isAdded())
            getSupportFragmentManager().beginTransaction().add(R.id.container, searchFragment).commit();

        showFragment(searchFragment);
        getSupportFragmentManager().beginTransaction().addToBackStack("video").commit();
    }

    public MainActivity showFragment(Fragment... fragments)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);

        for (Fragment f : fm.getFragments())
            if (f.isAdded()) ft.hide(f);

        for (Fragment f : fragments)
            if (f.isAdded()) ft.show(f);

        ft.commit();
        return this;
    }

    public void onVideoStringChange(String str)
    {
        //Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        showFragment(videoFragment);
        videoFragment.onChange(str);
    }

}
