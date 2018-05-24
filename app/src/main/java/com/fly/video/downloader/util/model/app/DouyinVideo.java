package com.fly.video.downloader.util.model.app;

import com.fly.video.downloader.util.model.Video;

public class DouyinVideo extends Video {

    protected String dynamicCoverUrl = null;
    protected int media_type = 0;
    protected long group_id = 0;
    protected long aweme_id = 0;
    //protected Date create_time = null;

    public String getDynamicCoverUrl() {
        return dynamicCoverUrl;
    }

    public void setDynamicCoverUrl(String animateCoverUrl) {
        this.dynamicCoverUrl = animateCoverUrl;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public long getAweme_id() {
        return aweme_id;
    }

    public void setAweme_id(long aweme_id) {
        this.aweme_id = aweme_id;
    }

    /*public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }*/

    public String[] getUrls() {
        return new String[]{
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + id + "&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://api.amemv.com/aweme/v1/play/?video_id=" + id + "&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + id + "&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://api.amemv.com/aweme/v1/play/?video_id=" + id + "&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
        };
    }

    public String getUrl() {
        String[] urls = getUrls();
        return urls[0];
    }

}
