package com.ls.download;

import com.ls.crawler.HttpClientUtil;
import com.ls.crawler.M3u8Parser;
import com.ls.crawler.PornHubCrawer;
import com.ls.http.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class M3u8DownloadTask implements DownloadTask{

    private static Logger logger = LoggerFactory.getLogger(PornHubCrawer.class);

    private String saveDir;
    private String url;

    public M3u8DownloadTask(String saveDir, String url) {
        this.saveDir = saveDir;
        this.url = url;
    }

    @Override
    public void download() {
        System.out.println("开始下载：" + url);

        FileUtils.mkdirs(saveDir);

        byte[] body = HttpClientUtil.get(url, null, "UTF-8");
        String content = new String(body);
        System.out.println(content);

        List<String> urls = null;
        try {
            urls = M3u8Parser.parseUrls(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(urls);

        if(urls != null && urls.size() > 0){
            try {
                String prefix = "https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/543be04a5285890806723545107/drm/";
                download2(prefix, saveDir, urls, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> download2(String prefixUrl, String path, List<String> urls, boolean retryOnError) throws IOException{
        List<String> errorUrls = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(urls.size());

        // 下载ts文件
        for (int i = 0; i < urls.size(); i++) {
            final int index = i;
            final String url = urls.get(i);

            DownloadTask downloadTask = new DownloadTask(){

                @Override
                public void download() {
                    boolean success = false; // 下载是否成功
                    // 下载文件
                    try{
                        String fileName = getFileNameByTsUrl(url);
                        String filePath = path + fileName;
                        File file = new File(filePath);

                        if(!file.exists() || file.length() == 0){
                            byte[] data = HttpClientUtil.get(prefixUrl + url, null, "utf-8");
                            if(data != null){
                                if(validTs(data)){
                                    FileOutputStream fos = new FileOutputStream(file);
                                    fos.write(data);
                                    fos.flush();
                                    fos.close();
                                    success = true;

                                }
                            }
                        }else{
                            success = true;
                        }

                        // 日志
                        if(success){
                            logger.info("{} success", fileName);
                        }else{
                            logger.info("{} error", fileName);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    // 如果下载失败，写入失败文件列表
                    if(!success){
                        synchronized (this){
                            errorUrls.add(url);
                        }
                    }

                    //
                    latch.countDown();
                }
            };

            Downloader.getInstance().submitTask(downloadTask);
        }

        // 等待文件下载完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return errorUrls;
    }

    public void createFileList(String path, String fileListName, List<String> urls) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + fileListName));

        String pathbak = path.replaceAll("\\\\", "\\\\\\\\");

        for (int i = 0; i < urls.size(); i++) {
            String fileName = getFileNameByTsUrl(urls.get(i));
            bw.write("file '" + fileName +"'");
            bw.newLine();
        }
        bw.flush();
    }

    public String getFileNameByTsUrl(String url){
        int start = url.indexOf("end=") + 4;
        int end = url.indexOf("&type");
        return url.substring(start, end) + ".ts";
    }

    private boolean validTs(byte[] data) {
        return true;
//        return data[0] == 0x47 && data[1] == 0x40;
    }

    public static void main(String[] args) {
        String url = "https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/543be04a5285890806723545107/drm/voddrm.token.dWluPTU5NDk4NTUwODt2b2RfdHlwZT0wO2NpZD0yOTAwMTc1O3Rlcm1faWQ9MTAzMDEyMzE1O3Bsc2tleT0wMDA0MDAwMDUzMDRmYTk5OGI5ODQ1NGM0YmUwY2FhYWU1ZjFhYTBhOGFjYThkYjRiMWQ1MzAwNjA4NmExYjBiYjlhMzAyYjA4ZTNiY2JiOWQxMTU4MmZmO3Bza2V5PXUySkhnYTJsVFR3czhUTDVlcFdqc3lvUG44bGs1Qi1lZFFaeFRDQ1g0SElf.v.f30739.m3u8?exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822";
        M3u8DownloadTask task = new M3u8DownloadTask("C:\\Users\\warho\\Desktop\\aa\\", url);
        Downloader.getInstance().submitTask(task);

//        String str = task.getFileNameByTsUrl("v.f30739.ts?start=0&end=283327&type=mpegts&exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822");
//        System.out.println(str);
    }

}

