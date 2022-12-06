package com.fly.video.downloader.content.analyzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.Video;
import com.fly.video.downloader.content.analyzer.app.AnyVideoV1;
import com.fly.video.downloader.content.analyzer.app.DouyinV4;
import com.fly.video.downloader.content.analyzer.app.DouyinV5;
import com.fly.video.downloader.contract.VideoParser;
import com.fly.video.downloader.core.exception.URLInvalidException;
import com.fly.video.downloader.core.os.AsyncTaskResult;
import com.fly.video.downloader.exception.VideoException;
import com.fly.video.downloader.util.Helpers;

public class AnalyzerTask extends AsyncTask<String, Integer, AsyncTaskResult<Video>>  {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private AnalyzeListener listener;

    public AnalyzerTask(Context context, AnalyzeListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected AsyncTaskResult<Video> doInBackground(String ...params) {
        String str = params[0];
        VideoParser parser = null;

        if (Helpers.containsVideoUrl(context, str)) {
            parser = DouyinV5.getInstance(this.context);
        }

        try {
            if (parser == null)
                throw new URLInvalidException(this.context.getString(R.string.exception_invalid_url));

            Video video = parser.get(str);

            if (video == null || video.isEmpty())
                throw new VideoException(this.context.getString(R.string.exception_invalid_video));

            return new AsyncTaskResult<>(video);

        } catch (Throwable e) {
            return new AsyncTaskResult<>(e);
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        context = null;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Video> result) {
        super.onPostExecute(result);

        if (isCancelled())
            listener.onAnalyzeCanceled();
        else if (result.getError() != null)
            listener.onAnalyzeError(result.getError());
        else
            listener.onAnalyzed(result.getResult());

        context = null;

    }

    public interface AnalyzeListener {
        void onAnalyzed(Video video);
        void onAnalyzeCanceled();
        void onAnalyzeError(Throwable e);
    }
}
