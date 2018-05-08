package com.fly.video.downloader;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.fly.video.downloader.layout.fragment.UserFragment;
import com.fly.video.downloader.layout.fragment.VideoFragment;
import com.fly.video.downloader.share.Recv;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
     @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    Unbinder unbinder;
    VideoFragment videoFragment;
    UserFragment userFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.hide(userFragment).hide(videoFragment);
            if (id == R.id.navigation_user) ft.show(userFragment); else ft.show(videoFragment);
            ft.commit();

            return false;
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
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

}
