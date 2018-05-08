package com.fly.video.downloader;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());
        TypefaceProvider.registerDefaultIconSets();
    }
}
