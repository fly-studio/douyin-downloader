package com.fly.video.downloader.util.network;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.fly.video.downloader.core.io.Storage;
import com.fly.video.downloader.core.security.Encrypt;
import com.fly.video.downloader.exception.DownloadFileException;
import com.fly.video.downloader.util.io.FileStorage;

public class Downloader {

    public enum STATUS {
        NONE,
        DOWNLOADING,
        DONE,
        CANCELED,
        ERROR
    }

    protected String url = null;
    protected long total = 0;
    protected long loaded = 0;
    protected STATUS status = STATUS.NONE;

    protected DownloaderTask task = null;
    protected DownloaderListener listener;

    protected FileStorage file = null;

    public Downloader(@NonNull String url)
    {
        this(url, null);
    }

    public Downloader(@NonNull String url, FileStorage file)
    {
        this.url = url;
        this.file = file;
        task = new DownloaderTask(this);
    }

    public String getUrl() {
        return url;
    }

    public long getTotal() {
        return total;
    }

    public long getLoaded() {
        return loaded;
    }

    public STATUS getStatus() {
        return status;
    }

    public DownloaderTask getTask() {
        return task;
    }

    public DownloaderListener getListener() {
        return listener;
    }

    public boolean isReparative() {
        return status == STATUS.NONE ;
    }

    public boolean isDownloaded() {
        return status == STATUS.DONE ;
    }

    public boolean isCanceled() {
        return status == STATUS.CANCELED ;
    }

    public boolean isDownloading() {
        return status == STATUS.DOWNLOADING ;
    }

    public boolean isError() {
        return status == STATUS.ERROR ;
    }

    public FileStorage getFile() {
        return file;
    }

    public Downloader setFile(FileStorage file)
    {
        this.file = file;
        return this;
    }

    public Downloader setFileAsCache(@NonNull FileStorage.TYPE type, String filename)
    {
        if (filename == null || filename.isEmpty())
            filename = Storage.getNowFilename() + "," + Storage.getRandomFilename(5);

        try {
            file = new FileStorage.Builder(type, filename).setToCacheDir().build();
        } catch (Exception e) {
            this.onDownloadError(e);
        }

        return this;
    }

    public Downloader setFileAsDCIM(@NonNull FileStorage.TYPE type, String filename)
    {
        if (filename == null || filename.isEmpty())
            filename = Storage.getNowFilename() + "," + Storage.getRandomFilename(5);

        try {
            file = new FileStorage.Builder(type, filename).setToDCIMDir().build();
        } catch (Exception e) {
            this.onDownloadError(e);
        }
        return this;
    }

    public String getHash() {
        return Encrypt.MD5(url);
    }


    public Downloader setDownloadListener(DownloaderListener listener)
    {
        this.listener = listener;
        return this;
    }

    public void cancel()
    {
        if (status == STATUS.DOWNLOADING)
            task.cancel(true);
    }

    public void start() throws Exception
    {
        if (file == null)
            throw new DownloadFileException("Please set the File before 'start'");

        if (status == STATUS.NONE) {
            status = STATUS.DOWNLOADING;
            loaded = total = 0;
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    protected void done()
    {
        status = STATUS.DONE;


        loaded = total;
    }

    protected void error(Throwable e)
    {
        status = STATUS.ERROR;
        loaded = total = 0;
    }

    public void downloading(long loaded, long total)
    {
        status = STATUS.DOWNLOADING;
        this.loaded = loaded;
        this.total = total;
    }

    public void onDownloaded() {

        done();

        if (this.listener != null)
            this.listener.onDownloaded(this);
    }

    public void onDownloadCanceled() {
        cancel();

        if (this.listener != null)
            this.listener.onDownloadCanceled(this);
    }

    public void onDownloadError(Throwable e) {
        error(e);

        if (this.listener != null)
            this.listener.onDownloadError(this, e);
    }

    public void onDownloadProgress(long loaded, long total)
    {
        downloading(loaded, total);

        if (this.listener != null)
            this.listener.onDownloadProgress(this, loaded, total);
    }

    public interface DownloaderListener {
        void onDownloaded(Downloader downloader);
        void onDownloadProgress(Downloader downloader, long loaded, long total);
        void onDownloadCanceled(Downloader downloader);
        void onDownloadError(Downloader downloader, Throwable e);
    }
}
