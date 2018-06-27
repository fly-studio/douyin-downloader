package com.fly.video.downloader.layout.listener;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fly.video.downloader.MainActivity;
import com.fly.video.downloader.core.listener.FragmentListener;
import com.fly.video.downloader.layout.fragment.HistoryRecyclerViewAdapter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryFragmentListener extends FragmentListener {

    private Unbinder unbinder;


    public HistoryFragmentListener(Fragment fragment, Context context)
    {
        super(fragment, context);
    }

    public void onListFragmentInteraction(HistoryRecyclerViewAdapter.ViewHolder holder)
    {
        ((MainActivity)fragment.getActivity()).onVideoChange(holder.video, true);
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
