package com.fly.video.downloader.core.listener;

import android.content.Context;

public class ActivityListener {

    protected Context context;

    public ActivityListener(Context context)
    {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
