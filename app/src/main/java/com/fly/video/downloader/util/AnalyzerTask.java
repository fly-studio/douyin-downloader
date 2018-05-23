package com.fly.video.downloader.util;

import android.content.Context;
import android.os.AsyncTask;

import com.fly.video.downloader.R;
import com.fly.video.downloader.core.exception.URLInvalidException;
import com.fly.video.downloader.core.os.AsyncTaskResult;
import com.fly.video.downloader.util.app.DouyinV2;
import com.fly.video.downloader.util.content.Video;
import com.fly.video.downloader.util.contract.VideoParser;
import com.fly.video.downloader.util.exception.VideoException;

public class AnalyzerTask extends AsyncTask<String, Integer, AsyncTaskResult<Video>>  {

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

        if (str.contains(this.context.getString(R.string.url_douyin))) {
            parser = DouyinV2.getInstance(this.context);
        }

        try {
            if (parser == null)
                throw new URLInvalidException(this.context.getString(R.string.exception_invalid_url));
            Video video = parser.get(str);

            if (video == null || video.isEmpty())
                throw new VideoException(this.context.getString(R.string.exception_invalid_video));

            return new AsyncTaskResult<>(video);

        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }

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

    }

    public interface AnalyzeListener {
        void onAnalyzed(Video video);
        void onAnalyzeCanceled();
        void onAnalyzeError(Exception e);
    }
}
