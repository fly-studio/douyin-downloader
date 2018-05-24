package com.fly.video.downloader.util.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import java.util.Date;

abstract public class Video {
    protected String id = null;
    protected String title = null;
    protected String content = null;
    protected String coverUrl = null;
    protected String url = null;
    protected int width = 0;
    protected int height = 0;
    protected User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEmpty()
    {
        return this.id == null || this.id.isEmpty();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public static <T extends Video> T fromJSON(final Class<T> clazz, String json)
    {
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter())
                .build();
        JsonAdapter<T> jsonAdapter = moshi.adapter(clazz);
        try {
            return jsonAdapter.fromJson(json);
        } catch (Exception e) {

        }
        return null;
    }

    public <T extends Video> String toJson(final Class<T> clazz)
    {
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter())
                .build();
        JsonAdapter<T> jsonAdapter = moshi.adapter(clazz);
        return jsonAdapter.toJson((T)this);
    }

    public <T extends Video> String toJson()
    {
        return toJson(this.getClass());
    }

}
