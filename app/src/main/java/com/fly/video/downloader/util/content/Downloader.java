package com.fly.video.downloader.util.content;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fly.video.downloader.core.io.Storage;
import com.fly.video.downloader.core.security.Encrypt;
import com.fly.video.downloader.util.DownloaderTask;
import com.fly.video.downloader.util.exception.DownloadFileException;

import java.io.File;

public class Downloader {

    protected String url = null;
    protected FileStorage.TYPE type;
    protected String filename = null;
    protected boolean downloaded = false;
    protected DownloaderTask task = null;
    protected DownloaderListener listener;

    protected File file = null;

    public Downloader(@NonNull String url)
    {
        this.init(url, FileStorage.TYPE.IMAGE, null);
    }

    public Downloader(@NonNull String url, @NonNull FileStorage.TYPE type)
    {
        this.init( url, type, null);
    }

    public Downloader(@NonNull String url, @NonNull FileStorage.TYPE type, String filename)
    {
        this.init(url, type, filename);
    }


    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public File getFile() {
        return file;
    }

    public String getHash() {
        return Encrypt.MD5(url);
    }

    private void init(@NonNull String url, @NonNull FileStorage.TYPE type, String filename)
    {
        this.url = url;
        this.type = type;
        this.filename = filename == null || filename.isEmpty() ? Storage.getNowFilename() + "," + Storage.getRandomFilename(5) : filename;
        task = new DownloaderTask(this);
    }

    public Downloader setDownloadListener(DownloaderListener listener)
    {
        this.listener = listener;
        return this;
    }

    public void cancel()
    {
        if (!downloaded && task != null)
            task.cancel(true);
    }

    public void download() throws Exception
    {
        if (file == null)
            throw new DownloadFileException("Please saveToX before download, eg: new Download(..).saveToCache().download()");

        if (!downloaded)
            task.execute();
    }

    public Downloader saveToCache()
    {
        try {
            file = new FileStorage(type, filename).getCacheDir();
        } catch (Exception e) {
            this.onDownloadError(e);
        }

        return this;
    }

    public Downloader saveToDCIM()
    {
        try {
            file = new FileStorage(type, filename).getDCIMDir();
        } catch (Exception e) {
            this.onDownloadError(e);
        }

        return this;
    }

    public void onDownloaded() {
        downloaded = true;

        if (this.listener != null)
            this.listener.onDownloaded(this);
    }

    public void onDownloadCanceled() {
        if (this.listener != null)
            this.listener.onDownloadCanceled(this);
    }

    public void onDownloadError(Exception e) {
        if (this.listener != null)
            this.listener.onDownloadError(this, e);
    }

    public void onDownloadProgress(long loaded, long total)
    {
        if (this.listener != null)
            this.listener.onDownloadProgress(this, loaded, total);
    }

    public interface DownloaderListener {
        void onDownloaded(Downloader downloader);
        void onDownloadProgress(Downloader downloader, long loaded, long total);
        void onDownloadCanceled(Downloader downloader);
        void onDownloadError(Downloader downloader, Exception e);
    }
}
