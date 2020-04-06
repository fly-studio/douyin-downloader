package com.fly.video.downloader.layout.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.Video;
import com.fly.video.downloader.content.history.History;
import com.fly.video.downloader.layout.listener.HistoryFragmentListener;

public class HistoryFragment extends Fragment {

    protected HistoryFragmentListener mFragmentListener;
    protected HistoryRecyclerViewAdapter adapter;

    public HistoryFragment() {

    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    public void perpendHistory(Video video)
    {
        if (adapter == null)
            throw new RuntimeException("HistoryRecyclerViewAdapter is not initialization.");

        History.put(video);
        adapter.perpendItem(video);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context)); // 1 cols

            //recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setAdapter(adapter);
            adapter.setRecyclerView(recyclerView);
            adapter.addOnScroll();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentListener = new HistoryFragmentListener(this, context);
        adapter = new HistoryRecyclerViewAdapter(getActivity(), mFragmentListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

}
