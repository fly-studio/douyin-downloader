package com.fly.video.downloader.content.analyzer.app;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.fly.video.downloader.R;
import com.fly.video.downloader.bean.app.DouyinUser;
import com.fly.video.downloader.bean.app.DouyinVideo;
import com.fly.video.downloader.contract.VideoParser;
import com.fly.video.downloader.core.contract.AbstractSingleton;
import com.fly.video.downloader.core.contract.Jsonable;
import com.fly.video.downloader.core.exception.URLInvalidException;
import com.fly.video.downloader.exception.VideoException;
import com.fly.video.downloader.util.Helpers;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DouyinV3 extends VideoParser {

    public DouyinV3(Context context) throws SingletonException {
        super(context);
    }

    public static DouyinV3 getInstance(Context context){
        try {
            return AbstractSingleton.getInstance(DouyinV3.class, new Class<?>[]{Context.class}, new Object[]{context});
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

        String html = httpGet(url);

        if (!html.contains("douyin_falcon:page"))
            throw new VideoException(this.getString(R.string.exception_douyin_url));

        return parseVideo(html);
    }

    private DouyinVideo parseVideo(String html) throws Throwable {
        Document dom = Jsoup.parse(html);
        JSONObject json = getJson(html);

        String jsonStr = httpGet("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + json.getString("itemId") + "&dytk=" + json.getString("dytk"));
        Record record = Jsonable.fromJson(Record.class, jsonStr);

        DouyinVideo video = new DouyinVideo();

        if (record.isEmpty()) {
            String url = dom.select("#theVideo").attr("src");

            if (StringUtils.isEmpty(url))
                throw new VideoException(this.getString(R.string.exception_html));

            video.setAweme_id(json.getLong("itemId"));
            video.setId(String.valueOf(video.getAweme_id()));
            video.setUrl(url);
            video.setCoverUrl(dom.select("input[name=\"shareImage\"]").attr("value"));
            video.setTitle(dom.select("input[name=\"shareDesc\"]").attr("value"));
            video.setContent(video.getTitle());
            video.setWidth(json.getInt("videoWidth"));
            video.setHeight(json.getInt("videoHeight"));
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.maybe_watermark, Toast.LENGTH_LONG).show();
                }
            });

            DouyinUser user = new DouyinUser();
            user.setNickname(json.has("authorName") ? json.getString("authorName") : dom.select("#videoUser > .user-info > .user-info-name").text());
            user.setAvatarUrl(dom.select("#videoUser > .img-avator").attr("src"));
            user.setId(json.getString("uid"));

            video.setUser(user);

        } else {
            Record.Item item = record.getItem();

            video.setAweme_id(Long.parseLong(item.aweme_id));
            video.setId(item.video.vid);
            //video.setUrl(item.video.play_addr.getUrl());
            video.setCoverUrl(item.video.cover.getUrl());
            video.setDynamicCoverUrl(item.video.dynamic_cover.getUrl());
            video.setTitle(item.desc);
            video.setContent(video.getTitle());
            video.setWidth(item.video.width);
            video.setHeight(item.video.height);

            DouyinUser user = new DouyinUser();
            user.setNickname(item.author.nickname);
            user.setAvatarUrl(item.author.avatar_thumb.getUrl());
            user.setId(item.author.uid);

            video.setUser(user);
        }

        return video;
    }

    private JSONObject getJson(String html) throws JSONException {
        Pattern pattern = Pattern.compile("\\.init\\(\\{(.*?)\\}\\);", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String jsonStr = ("{" + matcher.group(1) + "}").replaceAll("[\\s]*([a-zA-Z0-9_]*?):", "\"$1\":");
            return new JSONObject(jsonStr);
        } else {
            throw new VideoException(this.getString(R.string.exception_douyin_url));
        }
    }

    public static class Record extends Jsonable {
        public List<Item> item_list = new ArrayList<>();

        public boolean isEmpty()
        {
            return item_list == null || item_list.isEmpty();
        }

        public Item getItem()
        {
            return item_list.get(0);
        }

        public static class Item {
            public String aweme_id;
            public Video video;
            public User author;
            public String desc;

            public static class Video {
                public Long duration;
                public Addr play_addr;
                public Addr cover;
                public Addr dynamic_cover;
                public Addr download_addr;
                public Addr play_addr_lowbr;
                public int width;
                public int height;
                public String vid;

            }

            public static class Addr {
                public String uri;
                public List<String> url_list;

                public String getUrl() {
                    return url_list != null && !url_list.isEmpty() ? url_list.get(0) : null;
                }
            }

            public static class User {
                public String uid;
                public String nickname;
                public Addr avatar_larger;
                public Addr avatar_thumb;
                public Addr avatar_medium;

            }
        }
    }



}
