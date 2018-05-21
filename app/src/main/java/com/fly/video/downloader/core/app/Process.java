package com.fly.video.downloader.core.app;

import android.app.Activity;

public class Process {

    public static void exit()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public static void background(Activity activity)
    {
        // background
        activity.moveTaskToBack(true);

        /*
        background
        activity.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        */
    }
}
