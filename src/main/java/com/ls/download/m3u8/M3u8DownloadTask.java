package com.ls.download.m3u8;

import com.ls.crawler.HttpClientUtil;
import com.ls.download.DownloadTask;
import com.ls.download.Downloader;
import com.ls.download.URLUtils;
import com.ls.http.FileUtils;
import com.ls.http.VideoUtils;
import org.apache.commons.lang3.StringUtils;
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

    private List<TsDownloadTask> errors = new ArrayList<>();

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
        logger.info("开始下载：" + url);
        // 创建文件夹
        FileUtils.mkdirs(saveDir);

        // 下载m3u8文件
        String m3u8Path = saveDir + "m3u8.txt";
        File m3u8File = new File(m3u8Path);
        String m3u8Content = null;
        if(!m3u8File.exists() || m3u8File.length() == 0){ // 下载文件
            byte[] body = HttpClientUtil.get(url, null, "UTF-8");
            m3u8Content = new String(body);
            logger.info("m3u8文件内容: \n{}", m3u8Content);
            FileUtils.writeToFile(m3u8Content, m3u8Path);
        }else{ // 读取文件缓存
            try {
                m3u8Content = FileUtils.readFileToString(m3u8Path);
            } catch (IOException e) {
                logger.error("读取m3u8.txt失败", e);
            }
        }
        if(StringUtils.isBlank(m3u8Content)){
            logger.error("m3u8文件内容为空");
            return;
        }

        // 解析m3u8文件
        M3u8 m3u8 = M3u8Parser.parse(m3u8Content);
        if(m3u8 == null || m3u8.getSubUrls() == null || m3u8.getSubUrls().size() == 0){
            logger.error("没有解析到url地址：\n" + m3u8Content);
            return;
        }

        // 下载ts文件
        String prefixUrl = URLUtils.getUrlPrefix(url); // url地址前缀
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

        // 创建合并的文件
        try {
            M3u8Utils.createFileList(saveDir + "fileList.txt", m3u8);
        } catch (IOException e) {
            logger.error("", e);
        }

        // 等待文件下载完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("", e);
        }

        // 合并文件
        if(progress() == 1){
            logger.info("开始合并ts文件");
            VideoUtils.concatVideo(saveDir, "fileList.txt", fileName);
            logger.info("合并ts文件完成");
        }

        logger.info("下载完成：" + url);

        errors.forEach(e -> {
            logger.error(e.getM3u8Url().getUrl());
        });
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
        errors.add(tsDownloadTask);
    }

    public static void main(String[] args) {
        String url = "https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/543be04a5285890806723545107/drm/voddrm.token.dWluPTU5NDk4NTUwODt2b2RfdHlwZT0wO2NpZD0yOTAwMTc1O3Rlcm1faWQ9MTAzMDEyMzE1O3Bsc2tleT0wMDA0MDAwMDUzMDRmYTk5OGI5ODQ1NGM0YmUwY2FhYWU1ZjFhYTBhOGFjYThkYjRiMWQ1MzAwNjA4NmExYjBiYjlhMzAyYjA4ZTNiY2JiOWQxMTU4MmZmO3Bza2V5PXUySkhnYTJsVFR3czhUTDVlcFdqc3lvUG44bGs1Qi1lZFFaeFRDQ1g0SElf.v.f30739.m3u8?exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822";
        url = "https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/500bef865285890805978200104/drm/voddrm.token.dWluPTU5NDk4NTUwODt2b2RfdHlwZT0wO2NpZD0zMjM2MzU7dGVybV9pZD0xMDI5MTc0MTE7cGxza2V5PTAwMDQwMDAwOTk4N2JiY2VhYjU1N2UyOTMyZDhjZDllMmQ1N2RkOTYzNjhkNWI1ZjUzMzY4ZDc0OThmZTAyOTMzODcxNzVkY2IxZmU2ZGUxMjAyZDQ3NGM7cHNrZXk9NUN3WGMycjZDSDNYNXR1N2EyTm5ESXBLKkNDcjB4NlZOWS00NGl2c2E2a18=.v.f56150.m3u8?exper=0&sign=a3fc023b9dd7d8a894a7fb0f554f9aa0&t=606ebde7&us=6566600199859607066";
        M3u8DownloadTask task = new M3u8DownloadTask("C:\\Users\\warho\\Desktop\\aa\\", url);
        task.fileName = "jvm.mp4";
        Downloader.getInstance().submitTask(task);

//        String str = task.getFileNameByTsUrl("v.f30739.ts?start=0&end=283327&type=mpegts&exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822");
//        System.out.println(str);
    }

}

