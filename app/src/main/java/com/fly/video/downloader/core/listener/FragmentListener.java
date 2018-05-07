package com.fly.video.downloader.core.listener;

import android.content.Context;
import android.support.v4.app.Fragment;

public class FragmentListener extends ActivelyListener {
    protected Fragment fragment;

    public FragmentListener(Fragment fragment, Context context)
    {
        super(context);
        this.fragment = fragment;
    }
}
