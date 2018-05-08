package com.fly.video.downloader.layout.listener;


import android.content.Context;
import android.app.Fragment;

import com.fly.video.downloader.core.listener.FragmentListener;
import com.fly.video.downloader.layout.fragment.dummy.DummyContent;

public class UserFragmentListener extends FragmentListener {

    public UserFragmentListener(Fragment fragment, Context context)
    {
        super(fragment, context);
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item)
    {

    }


}
