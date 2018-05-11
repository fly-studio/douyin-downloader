package com.fly.video.downloader.layout.listener;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fly.video.downloader.core.listener.FragmentListener;
import com.fly.video.downloader.layout.fragment.dummy.DummyContent;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserFragmentListener extends FragmentListener {

    private Unbinder unbinder;


    public UserFragmentListener(Fragment fragment, Context context)
    {
        super(fragment, context);
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item)
    {

    }

    @Override
    public void onCreateView(View view)
    {
        unbinder = ButterKnife.bind(this.context, view);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
    }
}
