package com.fly.video.downloader.layout.listener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.fly.video.downloader.R;
import com.fly.video.downloader.core.io.Storage;
import com.fly.video.downloader.core.listener.FragmentListener;
import com.fly.video.downloader.util.AnalyzerTask;
import com.fly.video.downloader.util.DownloadQueue;
import com.fly.video.downloader.util.content.Downloader;
import com.fly.video.downloader.util.content.FileStorage;
import com.fly.video.downloader.util.content.Video;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;

public class VideoFragmentListener extends FragmentListener implements AnalyzerTask.AnalyzeListener, DownloadQueue.QueueListener {

    protected Video video = null;
    protected DownloadQueue downloadQueue = new DownloadQueue();

    private Unbinder unbinder;
    @BindView(R.id.video_avatar)
    ImageView avatar;
    @BindView(R.id.video_cover)
    ImageView cover;
    @BindView(R.id.video_nickname)
    TextView nickname;
    @BindView(R.id.video_content)
    TextView content;
    @BindView(R.id.video_player)
    SurfaceView surfaceViewFrame;
    private SurfaceHolder holder;
    private MediaPlayer player;

    MediaController mediaController;

    public VideoFragmentListener(Fragment fragment, Context context) {

        super(fragment, context);
        mediaController = new MediaController(context);
        this.downloadQueue.setQueueListener(this);
    }

    @Override
    public void onCreateView(View view)
    {
        unbinder = ButterKnife.bind(this, view);

        surfaceViewFrame.setClickable(true);
        holder = surfaceViewFrame.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(mSurfaceHolderCallback);

        //mediaController.setAnchorView(surfaceViewFrame);
        //mediaController.setEnabled(false);

        player = new MediaPlayer();
        player.setOnPreparedListener(mMediaPlayerOnPreparedListener);
        player.setOnVideoSizeChangedListener(mMediaPlayerOnVideoSizeChangedListener);
        player.setScreenOnWhilePlaying(true);
        player.setOnBufferingUpdateListener(mMediaPlayerOnBufferingUpdateListener);
        //player.setOnBufferingUpdateListener();

        player.setOnCompletionListener(mMediaPlayerOnCompletionListener);
        player.setScreenOnWhilePlaying(true);

    }

    private void playVideo(final String video_uri) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    player.setDataSource(getContext(), Uri.parse(video_uri));
                    player.prepareAsync();
                } catch (Exception e) { // I can split the exceptions to get which error i need.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private MediaPlayer.OnBufferingUpdateListener mMediaPlayerOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mMediaPlayerOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener(){
        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
            mediaPlayer.start();
        }
    };


    private MediaPlayer.OnPreparedListener mMediaPlayerOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

            // Adjust the size of the video
            // so it fits on the screen
            int videoWidth = player.getVideoWidth();
            int videoHeight = player.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;
            Point size = new Point();
            ((Activity)getContext()).getWindowManager().getDefaultDisplay().getSize(size);
            float screenProportion = (float) size.x / (float) size.y;
            android.view.ViewGroup.LayoutParams lp = surfaceViewFrame.getLayoutParams();
            if (videoProportion > screenProportion) {
                lp.width = size.x;
                lp.height = (int) ((float) size.x / videoProportion);
            } else {
                lp.width = (int) (videoProportion * (float) size.y);
                lp.height = size.y;
            }
            surfaceViewFrame.setLayoutParams(lp);
            if (!player.isPlaying()) {
                player.start();
            }
            surfaceViewFrame.setClickable(true);

            mediaPlayer.start();
            mediaPlayer.setLooping(true);

        }
    };

    private MediaPlayer.OnCompletionListener mMediaPlayerOnCompletionListener = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //mediaPlayer.stop();
        }
    };

    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {



            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    @Override
    public void onDestroyView() {
        unbinder.unbind();
    }


    @Override
    public void onAnalyzed(Video video)
    {
        synchronized (VideoFragmentListener.class) {
            this.video = video;
            downloadQueue.clear();
            nickname.setText(video.getUser().getNickname());
            content.setText(video.getTitle());

            downloadQueue.add("avatar_thumb-" + video.getUser().getId(), new Downloader(video.getUser().getAvatarThumbUrl(), FileStorage.TYPE.IMAGE, "avatar_thumb-" + video.getUser().getId() ).saveToCache());
            //downloadQueue.add("cover-" + video.getId(), new Downloader(video.getCoverUrl(), FileStorage.TYPE.IMAGE, "cover-" + video.getId()).saveToCache());
            //downloadQueue.add("video-" + video.getId(), new Downloader(video.getUrl(), FileStorage.TYPE.VIDEO, "video-"+ video.getId() + ".mp4").saveToDCIM());
            //player.setVideoURI(Uri.parse(this.video.getUrl()));
            //player.setMediaController(mediaController);
            //player.requestFocus();
            playVideo(this.video.getUrl());
            try{
                downloadQueue.download();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onQueueDownloaded(DownloadQueue downloadQueue, ArrayList<String> canceledHashes) {
        Toast.makeText(this.context, "下载完毕", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQueueProgress(DownloadQueue downloadQueue, long loaded, long total) {
    }

    @Override
    public void onDownloaded(String hash, Downloader downloader) {
        String[] segments = hash.split("-");

        Bitmap bitmap;
        switch (segments[0])
        {
            case "cover":
                bitmap = BitmapFactory.decodeFile(downloader.getFile().getAbsolutePath());
                cover.setImageBitmap(bitmap);
                break;
            case "avatar_thumb":
                bitmap = BitmapFactory.decodeFile(downloader.getFile().getAbsolutePath());
                avatar.setImageBitmap(bitmap);
                break;
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


    @OnClick(R.id.video_player)
    public void onVideoClick(View v)
    {
        if (v.getId() == R.id.video_player) {
            if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                }
            }
        }
    }

    @OnClick(R.id.video_close)
    public void onClose()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
