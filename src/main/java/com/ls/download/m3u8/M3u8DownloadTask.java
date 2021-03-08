package com.ls.download.m3u8;

import com.ls.crawler.HttpClientUtil;
import com.ls.download.DownloadTask;
import com.ls.download.Downloader;
import com.ls.download.URLUtils;
import com.ls.http.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class M3u8DownloadTask implements DownloadTask {
    private final static Logger logger = LoggerFactory.getLogger(M3u8DownloadTask.class);
    private String saveDir;
    private String url;
    private String fileName;

    private CountDownLatch latch;
    private AtomicInteger completedTask = new AtomicInteger(0);
    private Integer totalTask = 0;

    private List<DownloadTask> errors = new ArrayList<>();

    public M3u8DownloadTask(String saveDir, String url) {
        this.saveDir = saveDir;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public void download() {
        logger.debug("开始下载：" + url);

        FileUtils.mkdirs(saveDir);

        byte[] body = HttpClientUtil.get(url, null, "UTF-8");
        String content = new String(body);
        logger.debug("m3u8文件内容: \n{}", content);

        M3u8 m3u8 = M3u8Parser.parse(content);
        if(m3u8 == null || m3u8.getSubUrls() == null || m3u8.getSubUrls().size() == 0){
            logger.error("没有解析到url地址：\n" + content);
            return;
        }

        String prefixUrl = URLUtils.getUrlPrefix(url);

        // 下载ts文件
        List<M3u8Url> urls = m3u8.getSubUrls();
        totalTask = urls.size();
        latch = new CountDownLatch(urls.size());
        for (M3u8Url m3u8Url : urls) {
            TsDownloadTask tsDownloadTask = new TsDownloadTask();
            tsDownloadTask.setM3u8Url(m3u8Url);
            tsDownloadTask.setMainTask(this);
            tsDownloadTask.setPrefixUrl(prefixUrl);
            tsDownloadTask.setSaveDir(saveDir);
            Downloader.getInstance().submitTask(tsDownloadTask);
        }

        // 等待文件下载完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("", e);
        }

        logger.debug("下载完成：" + url);
    }

    @Override
    public Integer progress() {
        return totalTask == 0 ? 0 : completedTask.get() / totalTask;
    }


    public void completedTask(TsDownloadTask tsDownloadTask){
        completedTask.incrementAndGet();
        latch.countDown();
    }

    public void errorTask(TsDownloadTask tsDownloadTask){
        latch.countDown();
    }

    public static void main(String[] args) {
        String url = "https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/543be04a5285890806723545107/drm/voddrm.token.dWluPTU5NDk4NTUwODt2b2RfdHlwZT0wO2NpZD0yOTAwMTc1O3Rlcm1faWQ9MTAzMDEyMzE1O3Bsc2tleT0wMDA0MDAwMDUzMDRmYTk5OGI5ODQ1NGM0YmUwY2FhYWU1ZjFhYTBhOGFjYThkYjRiMWQ1MzAwNjA4NmExYjBiYjlhMzAyYjA4ZTNiY2JiOWQxMTU4MmZmO3Bza2V5PXUySkhnYTJsVFR3czhUTDVlcFdqc3lvUG44bGs1Qi1lZFFaeFRDQ1g0SElf.v.f30739.m3u8?exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822";
        M3u8DownloadTask task = new M3u8DownloadTask("C:\\Users\\warho\\Desktop\\aa\\", url);
        Downloader.getInstance().submitTask(task);

//        String str = task.getFileNameByTsUrl("v.f30739.ts?start=0&end=283327&type=mpegts&exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822");
//        System.out.println(str);
    }

}

