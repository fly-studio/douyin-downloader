package com.fly.video.downloader.layout.listener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fly.iconify.widget.IconTextView;
import com.fly.video.downloader.GlideApp;
import com.fly.video.downloader.MainActivity;
import com.fly.video.downloader.R;
import com.fly.video.downloader.core.io.Storage;
import com.fly.video.downloader.core.listener.FragmentListener;
import com.fly.video.downloader.util.content.analyzer.AnalyzerTask;
import com.fly.video.downloader.util.io.FileStorage;
import com.fly.video.downloader.util.model.Video;
import com.fly.video.downloader.util.network.DownloadQueue;
import com.fly.video.downloader.util.network.Downloader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class VideoFragmentListener extends FragmentListener implements AnalyzerTask.AnalyzeListener, DownloadQueue.QueueListener {

    public Video video = null;
    protected DownloadQueue downloadQueue = new DownloadQueue();

    private Unbinder unbinder;
    @BindView(R.id.video_avatar)
    ImageView avatar;
/*    @BindView(R.id.video_cover)
    ImageView cover;*/
    @BindView(R.id.video_nickname)
    TextView nickname;
    @BindView(R.id.video_content)
    TextView content;
    @BindView(R.id.video_player)
    TextureView textureView;
    @BindView(R.id.video_downloaded)
    LinearLayout textDownloaded;
    @BindView(R.id.video_pause)
    IconTextView iconVideoPause;

    PlayerListener playerListener;


    public VideoFragmentListener(Fragment fragment, Context context) {

        super(fragment, context);
        this.downloadQueue.setQueueListener(this);
    }

    @Override
    public void onCreateView(View view)
    {
        unbinder = ButterKnife.bind(this, view);
        textDownloaded.setVisibility(View.INVISIBLE);
        iconVideoPause.setVisibility(View.INVISIBLE);

        playerListener = new PlayerListener(context, textureView);
        playerListener.setPlayerChangeListener(mPlayerChangeListener);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (playerListener != null)
            playerListener.destoryVideo();
    }

    public void pause()
    {
        if (playerListener != null)
            playerListener.pauseVideo();
    }

    public void resume()
    {
        if (playerListener != null)
            playerListener.resumeVideo();
    }

    public void reset() {
        if (playerListener != null)
            playerListener.resetVideo();
    }

    private PlayerListener.IPlayerChangeListener mPlayerChangeListener = new PlayerListener.IPlayerChangeListener() {
        @Override
        public void onChange(PlayerListener.STATUS status) {
            if (iconVideoPause != null)
                iconVideoPause.setVisibility(status == PlayerListener.STATUS.PAUSE ? View.VISIBLE : View.INVISIBLE);
        }
    };

    @Override
    public void onAnalyzed(Video video)
    {
        synchronized (VideoFragmentListener.class) {
            if (this.video == video)
                return;

            this.video = video;
            reset();

            ((MainActivity)fragment.getActivity()).onHistoryAppend(video);


            downloadQueue.clear();
            nickname.setText(video.getUser().getNickname());
            content.setText(video.getTitle());

            //downloadQueue.add("avatar_thumb-" + video.getUser().getId(), new Downloader(video.getUser().getAvatarThumbUrl()).setFileAsCache(FileStorage.TYPE.IMAGE, "avatar_thumb-" + video.getUser().getId()));
            //downloadQueue.add("cover-" + video.getId(), new Downloader(video.getCoverUrl(), FileStorage.TYPE.IMAGE, "cover-" + video.getId()).saveToCache());
            textDownloaded.setVisibility(View.INVISIBLE);

            GlideApp.with(fragment)
                    .load(video.getUser().getAvatarThumbUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.ic_notifications_black_24dp)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .into(avatar);

            Downloader videoDownloader = new Downloader(video.getUrl()).setFileAsDCIM(FileStorage.TYPE.VIDEO, "video-"+ video.getId() + ".mp4");

            if (videoDownloader.getFile().exists())
                playerListener.playVideo(Uri.fromFile(videoDownloader.getFile()));
            else {
                downloadQueue.add("video-" + video.getId(), videoDownloader);
                playerListener.playVideo(video.getUrl());
            }

            try{
                downloadQueue.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onQueueDownloaded(DownloadQueue downloadQueue, ArrayList<String> canceledHashes) {
        Toast.makeText(this.context, R.string.download_complete, Toast.LENGTH_SHORT).show();
        textDownloaded.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQueueProgress(DownloadQueue downloadQueue, long loaded, long total) {
        if (total <= 0)
            ((MainActivity) context).setMainProgress(0);
        else
            ((MainActivity) context).setMainProgress((int)(loaded * 100 / total));

        textDownloaded.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDownloaded(String hash, Downloader downloader) {
        String[] segments = hash.split("-");

        Bitmap bitmap;
        switch (segments[0])
        {
/*            case "cover":
                bitmap = BitmapFactory.decodeFile(downloader.getFile().getAbsolutePath());
                cover.setImageBitmap(bitmap);
                break;*/
            case "video":
                Storage.rescanGallery(this.context, downloader.getFile());
                break;
        }
    }

    @Override
    public void onDownloadProgress(String hash, Downloader downloader, long loaded, long total) {

    }

    @Override
    public void onDownloadCanceled(String hash, Downloader downloader) {

    }

    @Override
    public void onDownloadError(String hash, Downloader downloader, Exception e) {

    }

    @Override
    public void onAnalyzeError(Exception e) {
        Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAnalyzeCanceled() {

    }


    @OnClick(R.id.video_close)
    public void onClose()
    {
        com.fly.video.downloader.core.app.Process.background((Activity) context);

    }


}
