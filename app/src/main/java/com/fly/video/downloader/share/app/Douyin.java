package com.fly.video.downloader.share.app;

import android.util.Patterns;

import com.fly.video.downloader.network.Http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/5/4.
 */

public class Douyin {

    public String[] get(String shareUrl)
    {
        String url = this.stripUrl(shareUrl);
        if (url == null)
            return null;
        String html = Http.sendGet2(url, null, "utf-8");
        if (html == null)
            return null;

        String videoID = this.parseVideoID(html);
        if (videoID == null) return null;

        return new String[]{
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + videoID + "&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://api.amemv.com/aweme/v1/play/?video_id=" + videoID + "&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + videoID + "&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://api.amemv.com/aweme/v1/play/?video_id=" + videoID + "&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
        };
    }

    private String stripUrl(String shareUrl)
    {
        if (!shareUrl.contains("www.iesdouyin.com"))
            return null;

        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(shareUrl);
        if (matcher.find())
            return matcher.group(0);
        return null;
    }


    public String parseVideoID(String html)
    {
        Pattern pattern = Pattern.compile("\\?video_id=([0-9a-z]*?)\\\\u0026line=", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }


}
