package com.fly.video.downloader.content.analyzer.app;

import android.content.Context;

import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.app.DouyinUser;
import com.fly.video.downloader.bean.app.DouyinVideo;
import com.fly.video.downloader.contract.VideoParser;
import com.fly.video.downloader.core.contract.AbstractSingleton;
import com.fly.video.downloader.core.contract.Jsonable;
import com.fly.video.downloader.core.exception.URLInvalidException;
import com.fly.video.downloader.exception.VideoException;
import com.fly.video.downloader.util.Helpers;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;
import java.util.Iterator;

public class DouyinV4 extends VideoParser {

    public DouyinV4(Context context) throws AbstractSingleton.SingletonException {
        super(context);
    }

    public static DouyinV4 getInstance(Context context){
        try {
            return AbstractSingleton.getInstance(DouyinV4.class, new Class<?>[]{Context.class}, new Object[]{context});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DouyinVideo get(String str) throws Throwable {
        String url = Helpers.stripUrl(str);
        if (url == null)
            throw new URLInvalidException(this.getString(R.string.exception_invalid_url));

        Pair<String, String> response = httpGet(url, false);

        return parseVideo(response.getKey(), response.getValue());
    }

    private DouyinVideo parseVideo(String url, String html) throws Throwable {
        Document dom = Jsoup.parse(html);
        String jsonStr = URLDecoder.decode(dom.select("#RENDER_DATA").text(), "UTF-8");

        if (jsonStr.equals("")) {
            throw new VideoException(this.getString(R.string.exception_html));
        }

        JSONObject jsonObject = new JSONObject(jsonStr);
        Iterator<String> keys = jsonObject.keys();

        DouyinVideo video = new DouyinVideo();

        while (keys.hasNext()) {
            String key = keys.next();
            Object childNode = jsonObject.get(key);
            if (childNode instanceof JSONObject) {
                JSONObject obj = (JSONObject) childNode;
                if (obj.has("aweme") && obj.has("awemeId")) {
                    record record = Jsonable.fromJson(record.class, obj.toString());

                    video.setAweme_id(record.awemeId);
                    // video.setId();
                    video.setUrl(record.aweme.detail.video.playApi);
                    video.setCoverUrl(record.aweme.detail.video.originCover);
                    video.setDynamicCoverUrl(record.aweme.detail.video.dynamicCover);
                    video.setTitle(record.aweme.detail.desc);
                    video.setContent(video.getTitle());
                    video.setWidth(record.aweme.detail.video.width);
                    video.setHeight(record.aweme.detail.video.height);

                    DouyinUser user = new DouyinUser();
                    user.setNickname(record.aweme.detail.authorInfo.nickname);
                    user.setAvatarUrl(record.aweme.detail.authorInfo.avatarUri);
                    user.setId(record.aweme.detail.authorInfo.uid);

                    video.setUser(user);

                }
            }
        }

        return video;
    }

    public static class record extends Jsonable {
        public String awemeId;
        public aweme aweme;

        public static class aweme {
            public detail detail;

            public static class detail {
                public String awemeId;
                public String groupId;
                public authorInfo authorInfo;
                public String desc;
                public video video;

                public static class authorInfo {
                    public String uid;
                    public String nickname;
                    public String avatarUri;
                }

                public static class video {
                    public int width;
                    public int height;
                    public String ratio;
                    public int duration;
                    public String playApi;
                    public String cover;
                    public String originCover;
                    public String dynamicCover;
                    public String aiCover;
                }

            }
        }

    }
}
