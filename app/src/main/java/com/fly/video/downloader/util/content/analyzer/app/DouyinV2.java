package com.fly.video.downloader.util.content.analyzer.app;

import android.content.Context;
import android.util.Patterns;

import com.fly.video.downloader.R;
import com.fly.video.downloader.core.contract.AbstractSingleton;
import com.fly.video.downloader.core.exception.HttpException;
import com.fly.video.downloader.core.exception.URLInvalidException;
import com.fly.video.downloader.util.contract.VideoParser;
import com.fly.video.downloader.util.exception.VideoException;
import com.fly.video.downloader.util.model.app.DouyinUser;
import com.fly.video.downloader.util.model.app.DouyinVideo;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DouyinV2 extends VideoParser {

    public DouyinV2(Context context) throws SingletonException {
        super(context);
    }

    public static DouyinV2 getInstance(Context context){
        try {
            return AbstractSingleton.getInstance(DouyinV2.class, new Class<?>[]{Context.class}, new Object[]{context});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DouyinVideo get(String str) throws Exception {
        String url = this.stripUrl(str);
        if (url == null)
            throw new URLInvalidException(this.getString(R.string.exception_invalid_url));

        String html = new OkHttpClient().newCall(new Request.Builder().url(url).build()).execute().body().string();
        if (html == null || html.isEmpty())
            throw new HttpException(this.getString(R.string.exception_http));

        if (!html.contains("?video_id="))
            throw new VideoException(this.getString(R.string.exception_douyin_url));

        return parseVideo(html);
    }

    protected JSONObject parseJSON(String html) throws JSONException {
        Pattern pattern = Pattern.compile("\\.create\\(\\{(.*?)\\}\\);", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String jsonStr = "{" + matcher.group(1) + "}";
            return new JSONObject(jsonStr);
        } else {
            throw new VideoException(this.getString(R.string.exception_douyin_url));
        }
    }

    protected String parseVID(String html) {
        Pattern pattern = Pattern.compile("\\?video_id=(.*?)(&|\")", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new VideoException(this.getString(R.string.exception_douyin_url));
        }
    }

    private DouyinVideo parseVideo(String html) throws Exception {
        JSONObject json = parseJSON(html);
        Document dom = Jsoup.parse(html);

        DouyinVideo video = new DouyinVideo();
        video.setId(parseVID(html));
        video.setCoverUrl(json.getString("cover"));
        video.setTitle(dom.select(".video-info > .desc").text());

        try {
            video.setWidth(json.getInt("videoWidth"));
            video.setHeight(json.getInt("videoHeight"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        video.setUser(parseUser(dom));

        return video;
    }

    private DouyinUser parseUser(Document dom)
    {
        DouyinUser user = new DouyinUser();
        user.setAvatarThumbUrl(dom.select(".user-info > .avatar > img").attr("src"));
        user.setNickname(dom.select(".user-info > .info").text());
        return user;
    }

    private String stripUrl(String shareUrl)
    {
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(shareUrl);
        if (matcher.find())
            return matcher.group(0);
        return null;
    }
}
