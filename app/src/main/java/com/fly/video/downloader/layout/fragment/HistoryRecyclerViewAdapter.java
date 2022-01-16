package com.fly.video.downloader.layout.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.fly.video.downloader.GlideApp;
import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.Video;
import com.fly.video.downloader.content.history.HistoryReadTask;
import com.fly.video.downloader.layout.listener.HistoryFragmentListener;

import java.util.ArrayList;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = HistoryRecyclerViewAdapter.class.getSimpleName();

    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_REFRESH = 2;
    protected Context context;
    private final HistoryFragmentListener mListener;
    private static final int PAGE_SIZE = 10;
    private ArrayList<Video> videos = new ArrayList<>();
    private HistoryReadTask readTask;
    private int page = 1;
    private RecyclerView recyclerView;
    private boolean mAlreadyRefreshed = false;
    private boolean mBottomRefreshing = false;
    private boolean mLastPage = false;

    public HistoryRecyclerViewAdapter(Context context, HistoryFragmentListener listener) {
        this.context = context;
        mListener = listener;
        readPage(page);
    }

    private HistoryReadTask.HistoryListener mHistoryListener = new HistoryReadTask.HistoryListener(){
        @Override
        public void onGot(ArrayList<Video> _videos) {
            readTask = null;
            if (_videos.isEmpty())
            {
                mLastPage = true;
                notifyDataSetChanged();
            } else {
                int offset = videos.size();
                videos.addAll(_videos);
                notifyItemRangeInserted(offset, _videos.size());
                notifyItemChanged(offset);
                Log.d(TAG, "History page: " + page);
                //Toast.makeText(context, String.valueOf(page), Toast.LENGTH_SHORT).show();
                //notifyItemRangeChanged(offset, _videos.size() + 1);
                //notifyDataSetChanged();
            }

            mBottomRefreshing = false;
        }

        @Override
        public void onCanceled() {
            readTask = null;

        }

        @Override
        public void onError(Throwable e) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == TYPE_NORMAL)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_history_item, parent, false);
            return new ViewHolder(view);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_history_refresh, parent, false);

            return new RefreshHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RefreshHolder)
        {

        } else {
            if (position <= videos.size() - 1)
            {
                final ViewHolder viewHolder = (ViewHolder) holder;
                Video video = videos.get(position);
                viewHolder.setVideo(video);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            mListener.onListFragmentInteraction(viewHolder);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < videos.size() )
            return TYPE_NORMAL;
        else
            return TYPE_REFRESH;
    }

    @Override
    public int getItemCount() {
        return videos.size() + 1;
    }

    public void readMore() {
        readPage(++page);
    }

    private int getLastVisibleItemPosition() {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

        if (manager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) manager).findLastVisibleItemPosition();
        }

        return -1;
    }


    public boolean isBottomViewVisible() {

        int lastVisibleItem = getLastVisibleItemPosition();
        return lastVisibleItem != -1 && lastVisibleItem == getItemCount() - 1;
    }

    public void setRecyclerView(RecyclerView recyclerView) {

        this.recyclerView = recyclerView;
    }

    public void addOnScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (mLastPage) return;

                /*if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mAlreadyRefreshed) {
                        mAlreadyRefreshed = false;
                    }
                }*/
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mLastPage) return;

                if (mBottomRefreshing) {
                    return;
                }

                if (isBottomViewVisible())
                {
                    mAlreadyRefreshed = true;
                    mBottomRefreshing = true;
                    readMore();
                }
            }
        });

    }

    public class RefreshHolder extends RecyclerView.ViewHolder {
        public RefreshHolder(@NonNull View view) {
            super(view);
        }
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
            mNickname.setText(video.getUser().getNickname().isEmpty() ? "未知" : video.getUser().getNickname());
            if (!video.getUser().getAvatarUrl().isEmpty()) {
                GlideApp.with(mView)
                        .load(video.getUser().getAvatarUrl())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.drawable.ic_notifications_black_24dp)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(mAvatar);
            } else {
                mAvatar.setImageResource(R.drawable.ic_launcher_foreground);
            }
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
