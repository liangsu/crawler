package com.ls.download.m3u8;

public class M3u8Url {
    private M3u8 m3u8;
    /**
     * #EXTINF:<duration>,[<title>]
     * 时长
     */
    private float duration;
    private String title;

    /**
     * EXT-X-KEY：媒体片段可以进行加密，而该标签可以指定解密方法。
     */
    private M3u8Key key;

    /**
     * ts文件url地址
     */
    private String url;

    public M3u8 getM3u8() {
        return m3u8;
    }

    public void setM3u8(M3u8 m3u8) {
        this.m3u8 = m3u8;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public M3u8Key getKey() {
        return key;
    }

    public void setKey(M3u8Key key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
