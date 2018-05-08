package com.fly.video.downloader.core.listener;

import android.content.Context;
import android.app.Fragment;

public class FragmentListener extends ActivityListener {
    protected Fragment fragment;

    public FragmentListener(Fragment fragment, Context context)
    {
        super(context);
        this.fragment = fragment;
    }
}
