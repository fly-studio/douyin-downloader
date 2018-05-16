package com.fly.video.downloader.util;

import android.os.AsyncTask;

import com.fly.video.downloader.core.os.AsyncTaskResult;
import com.fly.video.downloader.util.content.Downloader;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by Administrator on 2018/5/4.
 */

public class DownloaderTask extends AsyncTask<Void, Long, AsyncTaskResult<Downloader>> {

    private Downloader downloader;
    public static final int DOWNLOAD_CHUNK_SIZE = 2048; //Same as Okio Segment.SIZE

    public DownloaderTask(Downloader downloader)
    {
        this.downloader = downloader;
    }

    @Override
    protected AsyncTaskResult<Downloader> doInBackground(Void ...params) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();

        File tmpFile = null;
        try {
            tmpFile = downloader.getFile().createTempFile();
            Request request = new Request.Builder()
                    .url(downloader.getUrl())
                    .build();
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            long contentLength =  body.contentLength();
            long totalRead = 0;
            long read;

            BufferedSource source = body.source();
            BufferedSink sink = Okio.buffer(Okio.sink(tmpFile));

            while ((read = source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE)) != -1) {
                totalRead += read;
                publishProgress(totalRead, contentLength);
            }
            sink.writeAll(source);
            sink.flush();
            sink.close();
            body.close();
            response.close();
            tmpFile.renameTo(downloader.getFile());
            publishProgress(contentLength, contentLength);

        } catch (Exception e) {
            if (tmpFile != null) tmpFile.delete();

            publishProgress(0l, 0l);
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
