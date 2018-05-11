package com.fly.video.downloader.util;

import android.os.AsyncTask;

import com.fly.video.downloader.core.network.Http;
import com.fly.video.downloader.core.os.AsyncTaskResult;
import com.fly.video.downloader.util.content.Downloader;

/**
 * Created by Administrator on 2018/5/4.
 */

public class DownloaderTask extends AsyncTask<Void, Long, AsyncTaskResult<Downloader>> {

    private Downloader downloader;

    public DownloaderTask(Downloader downloader)
    {
        this.downloader = downloader;
    }

    @Override
    protected AsyncTaskResult<Downloader> doInBackground(Void ...params) {

        try {
            Http.sendGetAndSaveFile(downloader.getUrl(), null, downloader.getFile().getAbsolutePath());
        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }

        return new AsyncTaskResult<>(downloader);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        downloader.onDownloadProgress(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Downloader> result) {
        super.onPostExecute(result);

        if (isCancelled())
            downloader.onDownloadCanceled();
        else if (result.getError() != null)
            downloader.onDownloadError(result.getError());
        else
            downloader.onDownloaded();
    }

}
