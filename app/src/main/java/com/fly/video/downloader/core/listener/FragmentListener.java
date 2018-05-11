package com.fly.video.downloader.core.listener;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

abstract public class FragmentListener extends ActivityListener {
    protected Fragment fragment;

    public FragmentListener(Fragment fragment, Context context)
    {
        super(context);
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    abstract public void onCreateView(View view);
    abstract public void onDestroyView();
}
