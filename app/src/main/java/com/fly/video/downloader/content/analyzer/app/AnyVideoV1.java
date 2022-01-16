package com.fly.video.downloader.content.analyzer.app;

import android.content.Context;

import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.app.AnyUser;
import com.fly.video.downloader.bean.app.AnyVideo;
import com.fly.video.downloader.contract.VideoParser;
import com.fly.video.downloader.core.contract.AbstractSingleton;
import com.fly.video.downloader.core.contract.Jsonable;
import com.fly.video.downloader.core.exception.URLInvalidException;
import com.fly.video.downloader.core.security.Encrypt;
import com.fly.video.downloader.exception.VideoException;
import com.fly.video.downloader.util.Helpers;

import org.apache.commons.lang3.tuple.Pair;

import java.net.URLEncoder;

public class AnyVideoV1 extends VideoParser {

    public AnyVideoV1(Context context) throws AbstractSingleton.SingletonException {
        super(context);
    }

    public static AnyVideoV1 getInstance(Context context) {
        try {
            return AbstractSingleton.getInstance(AnyVideoV1.class, new Class<?>[]{Context.class}, new Object[]{context});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AnyVideo get(String str) throws Throwable {
        String url = Helpers.stripUrl(str);
        if (url == null)
            throw new URLInvalidException(this.getString(R.string.exception_invalid_url));

        Pair<String, String> response = httpGet("https://tenapi.cn/video/?url=" + URLEncoder.encode(url, "utf-8"), false);

        return parseVideo(url, response.getValue());
    }

    private AnyVideo parseVideo(String url, String html) throws Throwable {

        record record = Jsonable.fromJson(record.class, html);

        if (record.code != 200) {
            throw new VideoException(record.msg);
        }
        AnyVideo video = new AnyVideo();
        video.setId(Encrypt.MD5(url));

        video.setOriginalUrl(url);
        video.setUrl(record.url);
        video.setTitle(record.title);
        video.setCoverUrl(record.cover);

        AnyUser user = new AnyUser();
        video.setUser(user);

        return video;
    }

    public static class record {
        public int code;
        public String msg;
        public String title;
        public String cover;
        public String url;
        public String music;
    }
}