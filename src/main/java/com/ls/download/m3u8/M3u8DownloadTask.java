package com.ls.download.m3u8;

import com.ls.crawler.HttpClientUtil;
import com.ls.download.*;
import com.ls.download.http.DefaultHttpClient;
import com.ls.download.http.HttpClient;
import com.ls.http.FileUtils;
import com.ls.http.VideoUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin2.message.HeartbeatMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class M3u8DownloadTask implements DownloadTask {
    private final static Logger logger = LoggerFactory.getLogger(M3u8DownloadTask.class);
    private String rootDir;
    private String subDir;
    private String url;
    private String fileName;

    private CountDownLatch latch;
    private AtomicInteger completedTask = new AtomicInteger(0);
    private Integer totalTask = 0;

    private List<TsDownloadTask> errors = new ArrayList<>();

    private Executor executor = Downloader.getInstance();

    private HttpClient httpClient;

    public M3u8DownloadTask(String path, String url) {
        this(path, url, -1);
    }

    public M3u8DownloadTask(String path, String url, int limit) {
        this.url = url;

        // 文件路径
        this.fileName = FileUtils.getFileNameFromPath(path);
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件路径不正确");
        }
        this.rootDir = FileUtils.getDirectoryFromPath(path);
        this.subDir = rootDir + FileUtils.getFileName(fileName) + File.separator;

        // 子任务线程池
        if(limit > 0){
            executor = new ThreadLimiter3(limit);
        }
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
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
        logger.info("线程[{}]开始下载文件：{}", Thread.currentThread().getName(), fileName);
        // 创建文件夹
        FileUtils.mkdirs(subDir);

        // 下载m3u8文件
        String m3u8Path = subDir + "m3u8.txt";
        File m3u8File = new File(m3u8Path);
        String m3u8Content = null;
        if(!m3u8File.exists() || m3u8File.length() == 0){ // 下载文件
            byte[] body = httpClient.getBytes(url, null);
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
            tsDownloadTask.setSaveDir(subDir);
            tsDownloadTask.setHttpClient(httpClient);
            getExecutor().execute(tsDownloadTask);
        }

        // 创建合并的文件
        try {
            M3u8Utils.createFileList(subDir + "fileList.txt", m3u8);
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
            VideoUtils.concatVideo(subDir +"fileList.txt", rootDir + fileName);
            logger.info("合并ts文件完成");
        }

        logger.info("下载完成：" + url);

        errors.forEach(e -> {
            logger.error(e.getM3u8Url().getUrl());
        });
    }

    public Executor getExecutor(){
        return executor;
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
        url = "https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/002aab135285890790575137580/drm/voddrm.token.dWluPTU5NDk4NTUwODt2b2RfdHlwZT0wO2NpZD0zMDYzNDQ7dGVybV9pZD0xMDA0Njc2Mjg7cGxza2V5PTAwMDQwMDAwOTk4N2JiY2VhYjU1N2UyOTMyZDhjZDllMmQ1N2RkOTYzNjhkNWI1ZjUzMzY4ZDc0OThmZTAyOTMzODcxNzVkY2IxZmU2ZGUxMjAyZDQ3NGM7cHNrZXk9NUN3WGMycjZDSDNYNXR1N2EyTm5ESXBLKkNDcjB4NlZOWS00NGl2c2E2a18=.v.f30741.m3u8?exper=0&sign=c83c08eed4848068d55d6868b5d6f34f&t=606ff3c7&us=5754236442062380904";
//        url = "http://139.9.247.165/fileServer/attachment/viewM3u8/1287211710716841986/index.m3u8";
        String dir = "C:\\Users\\Administrator\\Desktop\\aa\\算法入门.mp4";
        M3u8DownloadTask task = new M3u8DownloadTask(dir, url, 1);

        Map<String, String> headers = new HashMap<>();
//        headers.put("token", "804c32d1-2434-423c-9709-7048c66f1917");

        task.setHttpClient(new DefaultHttpClient(headers));
//        task.fileName = "jvm.mp4";
        Downloader.getInstance().submitTask(task);

//        String str = task.getFileNameByTsUrl("v.f30739.ts?start=0&end=283327&type=mpegts&exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822");
//        System.out.println(str);

//         https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/002aab135285890790575137580/drm/v.f30741.ts?start=12666032&end=13789903&type=mpegts&exper=0&sign=c83c08eed4848068d55d6868b5d6f34f&t=606ff3c7&us=5754236442062380904


    }


}

