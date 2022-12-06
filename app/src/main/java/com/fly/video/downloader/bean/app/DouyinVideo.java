package com.fly.video.downloader.bean.app;

import com.fly.video.downloader.bean.Video;

import org.apache.commons.lang3.StringUtils;

public class DouyinVideo extends Video {

    protected String dynamicCoverUrl = null;
    protected String aweme_id = "0";

    public String getDynamicCoverUrl() {
        return dynamicCoverUrl;
    }

    public void setDynamicCoverUrl(String animateCoverUrl) {
        this.dynamicCoverUrl = animateCoverUrl;
    }

    public String getAweme_id() {
        return aweme_id;
    }

    public void setAweme_id(String aweme_id) {
        this.aweme_id = aweme_id;
    }

    protected String[] getUrls() {
        return new String[]{
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + id + "&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://api.amemv.com/aweme/v1/play/?video_id=" + id + "&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + id + "&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "https://api.amemv.com/aweme/v1/play/?video_id=" + id + "&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
        };
    }

    public String getUrl() {
        return StringUtils.isEmpty(url) ? getUrls()[0] : url;
    }

}
