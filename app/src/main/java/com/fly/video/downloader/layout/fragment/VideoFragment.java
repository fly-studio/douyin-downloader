package com.fly.video.downloader.layout.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fly.video.downloader.R;
import com.fly.video.downloader.layout.listener.VideoFragmentListener;
import com.fly.video.downloader.share.Analyzer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    protected VideoFragmentListener mFragmentListener;
    protected VideoSearchFragment searchFragment = null;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.video, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.video_menu_search == item.getItemId()){
            if (null == searchFragment)
                searchFragment = VideoSearchFragment.newInstance();

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in,
                    android.R.animator.fade_out);
            if (searchFragment.isAdded())
                ft.show(searchFragment);
            else
                ft.add(R.id.video_fragment, searchFragment);
            ft.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    public void Analyze(String text)
    {
        Analyzer analyzer = new Analyzer(this.getActivity());
        analyzer.execute(text);
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mFragmentListener = new VideoFragmentListener(this, context);
        setMenuVisibility(true);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        setMenuVisibility(false);
        mFragmentListener = null;
    }

}
