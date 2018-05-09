package com.fly.video.downloader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.fly.video.downloader.layout.fragment.UserFragment;
import com.fly.video.downloader.layout.fragment.VideoFragment;
import com.fly.video.downloader.layout.fragment.VideoSearchFragment;
import com.fly.video.downloader.util.Recv;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        videoFragment = VideoFragment.newInstance();
        userFragment = UserFragment.newInstance(1);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.viewpager, videoFragment).add(R.id.viewpager, userFragment).hide(userFragment).show(videoFragment).commit();


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

    public void showVideoSearchFragment()
    {
        if (null == searchFragment)
            searchFragment = VideoSearchFragment.newInstance();

        if (!searchFragment.isAdded())
            getSupportFragmentManager().beginTransaction().add(R.id.container, searchFragment).commit();

        showFragment(searchFragment);

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
