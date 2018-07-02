package com.fly.video.downloader.layout.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fly.video.downloader.GlideApp;
import com.fly.video.downloader.R;
import com.fly.video.downloader.layout.listener.HistoryFragmentListener;
import com.fly.video.downloader.util.content.history.HistoryReadTask;
import com.fly.video.downloader.util.model.Video;

import java.util.ArrayList;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    protected Context context;
    private final HistoryFragmentListener mListener;
    private static final int PAGE_SIZE = 10;
    private ArrayList<Video> videos = new ArrayList<>();
    private HistoryReadTask readTask;

    public HistoryRecyclerViewAdapter(Context context, HistoryFragmentListener listener) {
        this.context = context;
        mListener = listener;
        readPage(1);
    }

    private HistoryReadTask.HistoryListener mHistoryListener = new HistoryReadTask.HistoryListener(){
        @Override
        public void onGot(ArrayList<Video> _videos) {
            readTask = null;
            int offset = videos.size();
            videos.addAll(_videos);
            notifyItemRangeChanged(offset, _videos.size());
        }

        @Override
        public void onCanceled() {
            readTask = null;

        }

        @Override
        public void onError(Exception e) {
            readTask = null;

        }
    };

    public void readPage(int page)
    {
        if (readTask != null)
            return;

        if (page <= 0 ) page = 1;
        readTask = new HistoryReadTask(context, mHistoryListener);
        readTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, PAGE_SIZE);
    }

    public void perpendItem(Video _video) {
        for (int i = videos.size() - 1; i >= 0; i--) {
           Video video = videos.get(i);
           if (video.getId() == _video.getId())
           {
               if (i == 0) return;
               videos.remove(i);
               notifyItemRemoved(i);
           }
        }

        this.videos.add(0, _video);
        notifyItemInserted(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position < videos.size())
        {
            Video video = videos.get(position);
            holder.setVideo(video);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(holder);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Video video;
        private final ImageView mAvatar;
        private final TextView mNickname;
        private final TextView mTitle;

        public void setVideo(Video video)
        {
            this.video = video;
            mTitle.setText(video.getTitle());
            mNickname.setText(video.getUser().getNickname());
            GlideApp.with(mView)
                    .load(video.getUser().getAvatarThumbUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.ic_notifications_black_24dp)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .into(mAvatar);

        }

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatar = view.findViewById(R.id.history_list_avatar);
            mNickname = view.findViewById(R.id.history_list_nickname);
            mTitle = view.findViewById(R.id.history_list_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}
