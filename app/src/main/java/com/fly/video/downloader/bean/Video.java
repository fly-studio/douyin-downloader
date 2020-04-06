package com.fly.video.downloader.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fly.video.downloader.core.contract.Jsonable;

abstract public class Video extends Jsonable {

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

    @JsonIgnore
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

}
