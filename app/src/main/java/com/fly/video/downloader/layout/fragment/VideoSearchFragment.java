package com.fly.video.downloader.layout.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fly.video.downloader.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoSearchFragment extends Fragment {


    public VideoSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        getParentFragment().setMenuVisibility(false);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        getParentFragment().setMenuVisibility(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoSearchFragment newInstance() {
        VideoSearchFragment fragment = new VideoSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_search, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (getParentFragment() != null)
            getParentFragment().setMenuVisibility(hidden);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.video_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
