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
import android.widget.EditText;
import android.widget.Toast;

import com.fly.video.downloader.MainActivity;
import com.fly.video.downloader.R;
import com.fly.video.downloader.core.Validator;
import com.fly.video.downloader.core.content.ClipboardManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoSearchFragment extends Fragment {
    @BindView(R.id.video_search_editor)
    protected EditText editor;

    private Unbinder unbinder;

    public VideoSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.video_search_menu_paste:
                ClipboardManager clip = new ClipboardManager(getActivity());

                String str = clip.getText(0);
                if (str == null || str.isEmpty())
                {
                    Toast.makeText(getActivity(), R.string.empty_clipboard, Toast.LENGTH_SHORT).show();
                } else {
                    editor.setText(str);
                    Toast.makeText(getActivity(), R.string.paste_success, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoSearchFragment.
     */
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
        View view = inflater.inflate(R.layout.fragment_video_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.video_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.video_search_btn_clear)
    public void onClear()
    {
        editor.setText("");
    }

    @OnClick(R.id.video_search_btn_enter)
    public void onSearch()
    {
        String str = editor.getText().toString();
        if (str.isEmpty() || !Validator.containsUrl(str))
        {
            Toast.makeText(getActivity(), R.string.noURL, Toast.LENGTH_SHORT).show();
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
            ((MainActivity)getActivity()).onVideoStringChange(str);
            editor.setText("");
        }

    }

}
