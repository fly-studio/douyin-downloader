package com.fly.video.downloader.layout.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fly.video.downloader.MainActivity;
import com.fly.video.downloader.R;
import com.fly.video.downloader.layout.listener.VideoFragmentListener;
import com.fly.video.downloader.util.content.Recv;
import com.fly.video.downloader.util.content.analyzer.AnalyzerTask;
import com.fly.video.downloader.util.model.Video;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    protected VideoFragmentListener mFragmentListener;

    public VideoFragment() {

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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (mFragmentListener != null)
        {
            if (hidden)  mFragmentListener.pause();
            else mFragmentListener.resume();
        }
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
            ((MainActivity)getActivity()).showVideoSearchFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_video, container, false);

        mFragmentListener.onCreateView(view);

        Recv recv = new Recv(this.getActivity().getIntent());
        if (recv.isActionSend() && isAdded())
            Analyze(recv.getContent());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentListener.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
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

    @Override
    public void onPause() {
        super.onPause();

        mFragmentListener.pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        mFragmentListener.resume();
    }

    public void Analyze(String text)
    {
        mFragmentListener.reset();

        Toast.makeText(getActivity(), R.string.start_analyzing, Toast.LENGTH_SHORT).show();

        AnalyzerTask analyzerTask = new AnalyzerTask(getActivity(), mFragmentListener);
        analyzerTask.execute(text);
    }

    public void Analyze(Video video)
    {
        Analyze(video, false);
    }

    public void Analyze(Video video, boolean fromHistory)
    {
        mFragmentListener.onAnalyzed(video, fromHistory);
    }

}
