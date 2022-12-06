package com.fly.video.downloader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fly.video.downloader.bean.Video;
import com.fly.video.downloader.content.Recv;
import com.fly.video.downloader.core.app.BaseActivity;
import com.fly.video.downloader.core.content.ClipboardManager;
import com.fly.video.downloader.layout.fragment.HistoryFragment;
import com.fly.video.downloader.layout.fragment.VideoFragment;
import com.fly.video.downloader.layout.fragment.VideoSearchFragment;
import com.fly.video.downloader.util.Helpers;


import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.navigation)
    protected BottomNavigationView bottomNavigationView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.progress)
    protected ProgressBar progressBar;


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

        askPermissions();

    }

    @AfterPermissionGranted(101)
    private void askPermissions() {
        String[] perms = {
                Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION,
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.d(TAG, "Accepted Permissions: " + StringUtils.join(perms, ", "));
        } else {
            // 没有权限，进行权限请求
            EasyPermissions.requestPermissions(this, "请求读写权限",101, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "Accepted Permissions: " + StringUtils.join(perms, ", "));
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + StringUtils.join(perms, ", "));

        // 可选 检查用户是否拒绝了任何权限并选中了“永不再次询问”。
        // 这将显示一个对话框，指导他们启用应用程序设置中的权限。
        // if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // new AppSettingsDialog.Builder(this).build().show();
        // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //用户从应用设置屏幕返回后，执行一些操作，例如显示Toast.
            Toast.makeText(this, "拒绝了权限", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private String lastClip = "";

    @Override
    protected void onResume() {
        super.onResume();
        //Android Q以上 必须要在view渲染完毕之后获得剪贴板内容
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                ClipboardManager clip = new ClipboardManager(MainActivity.this);
                String str = clip.getText(0);

                if (!lastClip.equals(str))
                {
                    if (Helpers.containsVideoUrl(MainActivity.this, str)) {
                        lastClip = str;
                        showVideoSearchFragment();
                        searchFragment.setText(str);
                        Toast.makeText(MainActivity.this, R.string.clip_valid, Toast.LENGTH_LONG).show();
                    }
                }
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
