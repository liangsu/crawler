package com.ls.crawler;

import com.ls.http.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PornHubCrawer {

    ExecutorService executorService = Executors.newFixedThreadPool(40);

    /**
     * 获取m3u8文件的内容
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getM3u8Content(String url) throws UnsupportedEncodingException {
        byte[] bytes = HttpClientUtil.getWithProxy(url, null, "utf-8");
        return new String(bytes, "utf-8");
    }

    public void downloadByM3u8(String m3u8Url, String path) throws IOException {
        // 创建文件夹
        FileUtils.mkdirs(path);

        // url地址前缀
        String prefixUrl = m3u8Url.substring(0, m3u8Url.lastIndexOf("/") + 1);

        // 获取m3u8链接的内容
        String content = getM3u8Content(m3u8Url);
        // 解析出m3u8中的ts文件路径
        List<String> urls = M3u8Parser.parseUrls(content);

        // 创建ffmepg合并使用的文件
        createFileList(path, urls);

//        rename(path, urls);
        download(prefixUrl, path, urls, true);
        executorService.shutdown();
    }

    public void rename(String path, List<String> urls){
        for (int i = 0; i < urls.size(); i++) {
            File file = new File(path + i + ".ts");
            if(file.exists()){
                file.renameTo(new File(getFileNameByTsUrl(urls.get(i))));
            }
        }

    }

    public void download(String prefixUrl, String path, List<String> urls, boolean retryOnError) throws IOException{
        List<String> errorUrls = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(urls.size());

        // 下载ts文件
        for (int i = 0; i < urls.size(); i++) {
            final int index = i;
            final String url = urls.get(i);

            executorService.submit(() -> {
                boolean success = false; // 下载是否成功
                // 下载文件
                try{
                    String fileName = getFileNameByTsUrl(url);
                    String filePath = path + fileName;
                    File file = new File(filePath);

                    if(!file.exists() || file.length() == 0){
                        byte[] data = HttpClientUtil.getWithProxy(prefixUrl + url, null, "utf-8");
                        if(data != null){
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(data);
                            fos.flush();
                            fos.close();
                            success = true;
                        }
                    }else{
                        success = true;
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

            });
        }

        // 等待文件下载完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 有错误文件
        if(errorUrls != null && errorUrls.size() > 0){
            if(retryOnError){
                download(prefixUrl, path, errorUrls, false);
            }else {
                BufferedWriter errorWriter = new BufferedWriter(new FileWriter(path + "errorList.txt"));
                for (String errorUrl : errorUrls) {
                    errorWriter.write(prefixUrl + errorUrl);
                    errorWriter.newLine();
                }
                errorWriter.flush();
                errorWriter.close();
            }
        }
    }

    public void downloadByErrorFile(String path) throws IOException{
        List<String> urls = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path + "errorList.txt"));
        String line = null;
        while ((line = br.readLine()) != null){
            urls.add(line);
        }

        download("", path, urls, true);
    }

    public void createFileList(String path, List<String> urls) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + "fileList.txt"));

        String pathbak = path.replaceAll("\\\\", "\\\\\\\\");

        for (int i = 0; i < urls.size(); i++) {
            String fileName = getFileNameByTsUrl(urls.get(i));
            bw.write("file '" + pathbak + fileName +"'");
            bw.newLine();
        }
        bw.flush();
    }

    public String getFileNameByTsUrl(String url){
        int start = url.lastIndexOf("/") + 1;
        int end = url.indexOf("?");
        return url.substring(start, end);
    }

    public static void main(String[] args) throws IOException {
        PornHubCrawer pornHubCrawer = new PornHubCrawer();

//        String m3u8Url = "https://c1v-h.phncdn.com/hls/videos/201911/26/264264332/,1080P_4000K,720P_4000K,480P_2000K,240P_400K,_264264332.mp4.urlset/index-f2-v1-a1.m3u8?g4AAK8OUlpjkQEK94TKC-IrUC7n4IxurmY23scHEj6vk9Q5FapO7S7pixZDhec6RpVjI6FQz_mv0Ws-rsZK4BZ54k9mELgeK0Lrq6_C0DFFq_Tw4hTpVOeQnbFoDlwbY3PxBxprTuAQtbEHBgLRCJS0uF-ARPZSSCUzEOPanKtbmCyMG2ItBvBoqM8gjiixJIXMRAUcaUUCoC0Mw_gcO";
        String m3u8Url = "https://e1v-h.phncdn.com/hls/videos/202005/14/313631361/,720P_4000K,480P_2000K,240P_400K,_313631361.mp4.urlset/index-f1-v1-a1.m3u8?validfrom=1595765227&validto=1595772427&ip=66.98.121.250&hdl=-1&hash=W4KLF3JgJdoZGFa6HYzIEgs%2B7Cw%3D";

        String path = "C:\\Users\\warho\\Desktop\\aa\\dd\\";

        pornHubCrawer.downloadByM3u8(m3u8Url, path);

        // ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4

    }



}
