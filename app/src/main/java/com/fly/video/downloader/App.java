package com.fly.video.downloader;

import android.app.Application;
import android.content.Context;

import com.fly.iconify.Iconify;
import com.fly.iconify.fontawesome.module.FontAwesomeLightModule;
import com.fly.iconify.fontawesome.module.FontAwesomeModule;

public class App extends Application {

    private static Application app;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeLightModule())
                .with(new FontAwesomeModule());

        app = this;
    }

    public static Application getApp()
    {
        return app;
    }

    public static Context getAppContext() {
        return getApp().getApplicationContext();
    }

}
