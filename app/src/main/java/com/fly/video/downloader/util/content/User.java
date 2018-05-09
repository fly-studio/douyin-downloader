package com.fly.video.downloader.util.content;

public class User {
    protected String id = null;
    protected String nickname = null;
    protected String avatarUrl = null;
    protected String avatarThumbUrl = null;
    protected String signature = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarThumbUrl() {
        return avatarThumbUrl;
    }

    public void setAvatarThumbUrl(String avatarThumbUrl) {
        this.avatarThumbUrl = avatarThumbUrl;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
