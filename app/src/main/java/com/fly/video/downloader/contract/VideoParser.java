package com.fly.video.downloader.contract;

import android.content.Context;
import android.util.Log;

import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.Video;
import com.fly.video.downloader.core.contract.AbstractSingleton;
import com.fly.video.downloader.core.exception.HttpException;
import com.fly.video.downloader.util.Helpers;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    protected Pair<String, String> httpGet(String url, boolean usePhoneUa)
    {
        try {
            Request request = new Request.Builder()
                    .header("User-Agent", usePhoneUa ? Helpers.getPhoneUa() : Helpers.getPcUa())
                    .url(url)
                    .build();

            Response response = new OkHttpClient().newCall(request).execute();
            String finalUrl = response.request().url().toString();

            String html = Objects.requireNonNull(response.body()).string();

            if (html.isEmpty())
                throw new HttpException(this.getString(R.string.exception_http));

            return new MutablePair<>(finalUrl, html);
        } catch (IOException | NullPointerException e)
        {
            Log.e(TAG, e.getMessage(), e);
        }

        throw new HttpException(this.getString(R.string.exception_http));
    }

    abstract public Video get(String str) throws Throwable;
}
