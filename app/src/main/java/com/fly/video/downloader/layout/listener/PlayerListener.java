package com.fly.video.downloader.layout.listener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.solver.widgets.Rectangle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import com.fly.video.downloader.core.listener.ActivityListener;

import java.io.FileDescriptor;

public class PlayerListener extends ActivityListener {

    enum STATUS {
        NONE,
        LOADED,
        PLAYING,
        PAUSE,
        STOP,
    }

    protected TextureView textureView;
    private Surface surface;
    private MediaPlayer player;
    private STATUS status = STATUS.NONE;
    private IPlayerChangeListener iPlayerChangeListener;

    public PlayerListener(Context context, TextureView textureView) {
        super(context);
        this.textureView = textureView;

        textureView.setClickable(true);
        textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        textureView.setOnClickListener(mTextureViewOnClickListener);

        player = new MediaPlayer();
        player.setLooping(true);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(mMediaPlayerOnPreparedListener);
        player.setOnVideoSizeChangedListener(mMediaPlayerOnVideoSizeChangedListener);
        player.setOnBufferingUpdateListener(mMediaPlayerOnBufferingUpdateListener);
        player.setOnCompletionListener(mMediaPlayerOnCompletionListener);
        player.setScreenOnWhilePlaying(true); // 一直亮屏

    }

    protected void setPlayerChangeListener(IPlayerChangeListener iPlayerChangeListener)
    {
        this.iPlayerChangeListener = iPlayerChangeListener;
    }

    protected void playVideo(final Uri video_uri) {
        //new Thread(new Runnable() {
        //    public void run() {
                try {
                    resetVideo();
                    player.setDataSource(getContext(), video_uri);
                    player.prepareAsync();
                } catch (Exception e) { // I can split the exceptions to get which error i need.
                    e.printStackTrace();
                }
        //    }
        //}).start();
    }

    protected void playVideo(final String video_url) {
        //new Thread(new Runnable() {
        //    public void run() {
                try {
                    resetVideo();
                    player.setDataSource(getContext(), Uri.parse(video_url));
                    player.prepareAsync();
                } catch (Exception e) { // I can split the exceptions to get which error i need.
                    e.printStackTrace();
                }
        //    }
        //}).start();
    }

    protected void playVideo(final FileDescriptor fd) {
        //new Thread(new Runnable() {
        //    public void run() {
                try {
                    resetVideo();
                    player.setDataSource(fd);
                    player.prepareAsync();
                } catch (Exception e) { // I can split the exceptions to get which error i need.
                    e.printStackTrace();
                }
        //    }
        //}).start();
    }


    private TextureView.OnClickListener mTextureViewOnClickListener = new TextureView.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (player != null) {
                if (player.isPlaying()) {
                    pauseVideo();
                } else if (status != STATUS.NONE) { // 设置过setDataSource
                    resumeVideo();
                }
            }

        }
    };

    private MediaPlayer.OnBufferingUpdateListener mMediaPlayerOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mMediaPlayerOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener(){
        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
            playVideo();
        }
    };

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (surface == null) {
                surface = new Surface(surfaceTexture);
                player.setSurface(surface);
            }
            playVideo();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            System.out.println("onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            System.out.println("onSurfaceTextureDestroyed");
            pauseVideo();
            surface = null;
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private MediaPlayer.OnPreparedListener mMediaPlayerOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            synchronized (MediaPlayer.OnPreparedListener.class) {
                status = STATUS.LOADED;
            }

            // Adjust the size of the video
            // so it fits on the screen
            updateTextureViewSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());

            mediaPlayer.setLooping(true);
            playVideo();
        }
    };

    protected void updateTextureViewSize(int videoWidth, int videoHeight) {

        Rectangle viewRect = new Rectangle();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        Point screenSize = new Point();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRealSize(screenSize);
        //float screenProportion = (float) screenSize.x / (float) screenSize.y;

        //竖屏
        if (videoProportion < 1)
        {
            // 高度扩充到全屏
            viewRect.height = screenSize.y;
            viewRect.width = (int)(videoProportion * (float) screenSize.y);
            viewRect.y = 0;
            viewRect.x = screenSize.x - viewRect.width >> 1;

        } else { // 横屏

            // 宽度扩充到全屏
            viewRect.width = screenSize.x;
            viewRect.height = (int) ((float) screenSize.x / videoProportion);

            viewRect.y =  screenSize.y - viewRect.height >> 1;
            viewRect.x = 0;
        }
        /*float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (videoWidth > viewRect.width && videoHeight > viewRect.height) {
            scaleX = (float)videoWidth / (float)viewRect.height;
            scaleY = (float)videoHeight / (float)viewRect.height;
        } else if (videoWidth < viewRect.width && videoHeight < viewRect.height) {
            scaleX = (float)viewRect.width / (float)videoWidth;
            scaleY = (float)viewRect.height / (float)videoHeight;
        } else if (viewRect.width > videoWidth) {
            scaleY = ((float)viewRect.width / (float)videoWidth) / ((float)viewRect.height / (float)videoHeight);
        } else if (viewRect.height > videoHeight) {
            scaleX = ((float)viewRect.height / (float)videoHeight) / ((float)viewRect.width / (float)videoWidth);
        }

        // Calculate pivot points, in our case crop from center
        int pivotPointX = viewRect.width / 2;
        int pivotPointY = viewRect.height / 2;

        Matrix matrix = new Matrix();
        textureView.getTransform(matrix);
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);
        textureView.setTransform(matrix);*/

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)textureView.getLayoutParams();
        lp.width = viewRect.width;
        lp.height = viewRect.height;
        lp.leftMargin = viewRect.x;
        lp.rightMargin = viewRect.x;
        lp.topMargin = viewRect.y;
        lp.bottomMargin = viewRect.y;
        textureView.setLayoutParams(lp);
        //textureView.setX(viewRect.x);
        //textureView.setY(viewRect.y);

    }

    private MediaPlayer.OnCompletionListener mMediaPlayerOnCompletionListener = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //stopVideo();
        }
    };

    public synchronized void playVideo()
    {
        if (player != null && status != STATUS.NONE)
        {
            player.start();
            status = STATUS.PLAYING;
            if (iPlayerChangeListener != null) iPlayerChangeListener.onChange(status);
        }
    }

    public synchronized void resetVideo()
    {
        if (player != null)
        {
            player.reset();
            status = STATUS.NONE;
            if (iPlayerChangeListener != null) iPlayerChangeListener.onChange(status);
        }
    }

    public synchronized void pauseVideo()
    {
        if (player != null && status != STATUS.NONE)
        {
            if (player.isPlaying()) {
                player.pause();
                status = STATUS.PAUSE;
                if (iPlayerChangeListener != null) iPlayerChangeListener.onChange(status);
            }
        }
    }

    public synchronized void resumeVideo()
    {
        playVideo();
    }

    public synchronized void stopVideo()
    {
        if (player != null && status != STATUS.NONE)
        {
            player.stop();
            status = STATUS.STOP;
            if (iPlayerChangeListener != null) iPlayerChangeListener.onChange(status);
        }
    }

    public synchronized void destoryVideo() {
        if (player != null)
        {
            player.stop();
            player.release();
            status = STATUS.NONE;
            if (iPlayerChangeListener != null) iPlayerChangeListener.onChange(status);
        }
    }

    public interface IPlayerChangeListener {
        void onChange(STATUS status);
    }
}
