package com.ls.download.m3u8;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考： https://www.jianshu.com/p/e97f6555a070
 *
 */
public class M3u8 {
    /**
     * EXT-X-VERSION
     */
    private String version;

    /**
     * #EXT-X-TARGETDURATION:<s>
     * 表示每个视频分段最大的时长（单位秒）
     */
    private Integer targetDuration;

    /**
     * #EXT-X-MEDIA-SEQUENCE:<number>
     * 表示播放列表第一个 URL 片段文件的序列号。
     * 每个媒体片段 URL 都拥有一个唯一的整型序列号。
     * 每个媒体片段序列号按出现顺序依次加 1。
     * 如果该标签未指定，则默认序列号从 0 开始。
     * 媒体片段序列号与片段文件名无关。
     */
    private String mediaSequence;

    /**
     * #EXT-X-PLAYLIST-TYPE:VOD
     * 表明流媒体类型。全局生效。
     * 该标签为可选标签。
     *
     * VOD：即 Video on Demand，表示该视屏流为点播源，因此服务器不能更改该 m3u8 文件；
     *
     * EVENT：表示该视频流为直播源，因此服务器不能更改或删除该文件任意部分内容（但是可以在文件末尾添加新内容）。
     * 注：VOD 文件通常带有 EXT-X-ENDLIST 标签，因为其为点播源，不会改变；而 EVEVT 文件初始化时一般不会有 EXT-X-ENDLIST 标签，
     * 暗示有新的文件会添加到播放列表末尾，因此也需要客户端定时获取该 m3u8 文件，以获取新的媒体片段资源，直到访问到 EXT-X-ENDLIST 标签才停止）
     */
    private String playListType;

    /**
     * ts文件信息
     */
    private List<M3u8Url> subUrls = new ArrayList<>();

    private boolean hasSameTsName;
    /**
     * url参数中，参数的值不重复的key
     * 如： aa.ts?name=xiaoming&age=30、 bb.ts?name=xiaoming&age=40 返回 age
     */
    private List<String> subUrlNotRepeatParamKey;

    public boolean isHasSameTsName() {
        return hasSameTsName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(Integer targetDuration) {
        this.targetDuration = targetDuration;
    }

    public String getMediaSequence() {
        return mediaSequence;
    }

    public void setMediaSequence(String mediaSequence) {
        this.mediaSequence = mediaSequence;
    }

    public String getPlayListType() {
        return playListType;
    }

    public void setPlayListType(String playListType) {
        this.playListType = playListType;
    }

    public List<M3u8Url> getSubUrls() {
        return subUrls;
    }

    public void setHasSameTsName(boolean hasSameTsName) {
        this.hasSameTsName = hasSameTsName;
    }

    public List<String> getSubUrlNotRepeatParamKey() {
        return subUrlNotRepeatParamKey;
    }

    public void setSubUrlNotRepeatParamKey(List<String> subUrlNotRepeatParamKey) {
        this.subUrlNotRepeatParamKey = subUrlNotRepeatParamKey;
    }
}
