package com.fly.video.downloader.contract;

import android.content.Context;
import android.util.Log;

import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.Video;
import com.fly.video.downloader.core.contract.AbstractSingleton;
import com.fly.video.downloader.core.exception.HttpException;
import com.fly.video.downloader.util.Helpers;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;

abstract public class VideoParser extends AbstractSingleton {
    private static final String TAG = VideoParser.class.getSimpleName();

    protected Context context;

    public VideoParser(Context context) throws SingletonException {
        super();
        this.context = context;
    }

    protected String getString(int resID)
    {
        return this.context.getString(resID);
    }

    protected String getString(int resID, Object ...formatArgs)
    {
        return this.context.getString(resID, formatArgs);
    }

    protected String httpGet(String url)
    {
        try {
            Request request = new Request.Builder()
                    .header("User-Agent", Helpers.getPhoneUa())
                    .url(url)
                    .build();

            String html = Objects.requireNonNull(new OkHttpClient().newCall(request).execute().body()).string();

            if (html.isEmpty())
                throw new HttpException(this.getString(R.string.exception_http));

            return html;
        } catch (IOException | NullPointerException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }

        throw new HttpException(this.getString(R.string.exception_http));
    }

    abstract public Video get(String str) throws Throwable;
}