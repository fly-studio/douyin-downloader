package com.fly.video.downloader.layout.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.fly.video.downloader.util.Analyzer;
import com.fly.video.downloader.util.Recv;
import com.fly.video.downloader.util.content.Video;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment implements Analyzer.AnalyzeListener {

    protected VideoFragmentListener mFragmentListener;
    protected Video video = null;

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

    public void onChange(String str)
    {
        Analyze(str);
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
        return inflater.inflate(R.layout.fragment_video, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentListener = new VideoFragmentListener(this, context);
        setMenuVisibility(true);

        Recv recv = new Recv(this.getActivity().getIntent());
        if (recv.isActionSend() && isAdded()) {

            Analyze(recv.getContent());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        setMenuVisibility(false);
        mFragmentListener = null;
    }

    public void Analyze(String text)
    {
        Toast.makeText(getActivity(), R.string.start_analyzing, Toast.LENGTH_SHORT).show();
        Analyzer analyzer = new Analyzer(getActivity(), this);
        analyzer.execute(text);
    }

    @Override
    public void onAnalyzed(Video video) {
        this.video = video;
    }

    @Override
    public void onAnalyzeError(Exception e) {
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAnalyzeCanceled() {

    }
}
