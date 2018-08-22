package com.fly.video.downloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fly.video.downloader.core.app.BaseActivity;
import com.fly.video.downloader.layout.fragment.HistoryFragment;
import com.fly.video.downloader.layout.fragment.VideoFragment;
import com.fly.video.downloader.layout.fragment.VideoSearchFragment;
import com.fly.video.downloader.util.content.Recv;
import com.fly.video.downloader.util.model.Video;
import com.github.florent37.runtimepermission.RuntimePermission;
import com.github.florent37.runtimepermission.callbacks.PermissionListener;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.github.florent37.runtimepermission.RuntimePermission.askPermission;

public class MainActivity extends BaseActivity {
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progressBar;


    private Unbinder unbinder;
    protected VideoFragment videoFragment;
    protected HistoryFragment historyFragment;
    protected VideoSearchFragment searchFragment = null;

    private Date backPressAt = null;
    private boolean fromSend = false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            if (id == R.id.navigation_history) showFragment(historyFragment); else showFragment(videoFragment);
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

        // 设置Toolbar
        setSupportActionBar(toolbar);
        //底部状态栏
 /*       bottomNavigationView.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        bottomNavigationView.setItemBackgroundResource(R.drawable.transparent);*/
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);


        videoFragment = VideoFragment.newInstance();
        historyFragment = HistoryFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.full_pager, videoFragment).add(R.id.view_pager, historyFragment).hide(historyFragment).show(videoFragment).commit();

        fromSend = this.getIntent() != null && Intent.ACTION_SEND.equals(this.getIntent().getAction());

        askPermission(this).ask(new PermissionListener() {
            @Override
            public void onAccepted(RuntimePermission runtimePermission, List<String> accepted) {
                Toast.makeText(MainActivity.this,"OK", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied) {
                Toast.makeText(MainActivity.this,"Why?", Toast.LENGTH_SHORT).show();
            }
        });

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
                com.fly.video.downloader.core.app.Process.background(this);
                //super.onBackPressed();
                return;
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fromSend = false;

        showFragment(videoFragment);
        Recv recv = new Recv(intent);
        if (recv.isActionSend() && videoFragment.isAdded()) {
            fromSend = true;
            videoFragment.Analyze(recv.getContent());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //finish();
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void setMainProgress(int progress)
    {
        if (progress >= 100) progress = 0;
        progressBar.setProgress(progress);
    }

    public void showVideoSearchFragment()
    {
        if (null == searchFragment)
            searchFragment = VideoSearchFragment.newInstance();

        if (!searchFragment.isAdded())
            getSupportFragmentManager().beginTransaction().add(R.id.no_navigation_pager, searchFragment).commit();

        showFragment(searchFragment);
        getSupportFragmentManager().beginTransaction().addToBackStack("video").commit();
    }

    public void onVideoChange(String str)
    {
        //Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        showFragment(videoFragment);
        videoFragment.Analyze(str);
    }

    public void onVideoChange(Video video)
    {
        onVideoChange(video, false);
    }

    public void onVideoChange(Video video, boolean fromHistory)
    {
        showFragment(videoFragment);
        videoFragment.Analyze(video, fromHistory);
    }

    public void onHistoryAppend(Video video)
    {
        historyFragment.perpendHistory(video);
    }

}
