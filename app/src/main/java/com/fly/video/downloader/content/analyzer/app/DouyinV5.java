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
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

public class DouyinV5 extends VideoParser {

    public DouyinV5(Context context) throws SingletonException {
        super(context);
    }

    public static DouyinV5 getInstance(Context context){
        try {
            return AbstractSingleton.getInstance(DouyinV5.class, new Class<?>[]{Context.class}, new Object[]{context});
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

        Pair<String, String> response = httpGet(url, true);

        return parseVideo(response.getKey(), response.getValue());
    }

    private DouyinVideo parseVideo(String url, String html) throws Throwable {
        Document dom = Jsoup.parse(html);
        String jsonStr = URLDecoder.decode(dom.select("#RENDER_DATA").html(), "UTF-8");

        if (jsonStr.equals("")) {
            throw new VideoException(this.getString(R.string.exception_html));
        }

        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray items = jsonObject.getJSONObject("app").getJSONObject("videoInfoRes").getJSONArray("item_list");

        DouyinVideo video = new DouyinVideo();
        for (int i = 0; i < items.length(); i++) {
            Object childNode = items.get(i);
            if (childNode instanceof JSONObject) {
                JSONObject obj = (JSONObject) childNode;
                if (obj.has("video") && obj.has("author")) {
                    record record = Jsonable.fromJson(record.class, obj.toString());

                    video.setAweme_id(record.aweme_id);
                    video.setId(record.video.vid);
                    video.setUrl(record.video.play_addr.getVideoUrl());
                    video.setOriginalUrl(url);
                    video.setCoverUrl(record.video.cover.getUrl());
                    video.setDynamicCoverUrl(record.video.dynamic_cover.getUrl());
                    video.setTitle(record.desc);
                    video.setContent(video.getTitle());
                    video.setWidth(record.video.width);
                    video.setHeight(record.video.height);

                    DouyinUser user = new DouyinUser();
                    user.setNickname(record.author.nickname);
                    user.setAvatarUrl(record.author.avatar_larger.getUrl());
                    user.setId(record.author.uid);

                    video.setUser(user);
                    break;
                }
            }
        }

        return video;
    }

    public static class record extends Jsonable {
        public String aweme_id;
        public video video;
        public author author;
        public String desc;

        public static class video {
            public int width;
            public int height;
            public String vid;
            public String ratio;
            public int duration;
            public url_list cover;
            public url_list play_addr;
            public url_list origin_cover;
            public url_list dynamic_cover;
        }

        public static class author {
            public String uid;
            public String short_id;
            public String nickname;
            public String signature;
            public url_list avatar_larger;
        }

        public static class url_list {
            public String uri;
            public List<String> url_list;

            public String getUrl() {
                if (url_list != null && !url_list.isEmpty()) {
                    return url_list.get(0);
                }
                return "";
            }

            public String getVideoUrl() {
                String url = getUrl();
                return url.replaceAll("playwm", "play");
            }
        }

    }
}
