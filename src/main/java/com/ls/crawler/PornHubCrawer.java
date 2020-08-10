package com.ls.crawler;

import com.ls.http.FileUtils;
import com.ls.http.VideoUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class PornHubCrawer {

    private static Logger logger = LoggerFactory.getLogger(Ting55Crawler.class);

    ExecutorService executorService = new ThreadPoolExecutor(40, 80,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue(10000));

    public String detailPage(String url) throws UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
        headers.put("Referer", "https://cn.pornhub.com/");
        byte[] bytes = HttpClientUtil.getWithProxy(url, headers, "utf-8");
        return new String(bytes, "utf-8");
    }

    public String downloadDetailPageIfNecessary(String url, String path) throws UnsupportedEncodingException {
        File file = new File(path );

        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        String content;
        if(!file.exists()){
            content = detailPage(url);
            FileUtils.writeToFile(content, path );
        }else{
            content = FileUtils.readFromFile(path );
        }

        return content;
    }

    public String parseM3u8UrlFromDetailPage(String detailPageContent) throws UnsupportedEncodingException {
        Document document = Jsoup.parse(detailPageContent);
        Element player = document.getElementById("player");
        Element element =  player.getElementsByTag("script ").first();
        ScriptEngine engine = JSUtils.runJs("var playerObjList = {}; " + element.data());

        Object m = engine.get("media_8");
        if(m == null){
            m = engine.get("media_7");
        }
        if(m == null){
            m = engine.get("media_6");
        }

        String m3u8URL = m.toString();
//        System.out.println(m3u8URL);
        return m3u8URL.replaceAll("master.m3u8", "index-f2-v1-a1.m3u8");
    }

    public String parseTitleFromDetailPage(String detailPageContent) throws UnsupportedEncodingException {
        Document document = Jsoup.parse(detailPageContent);
        Element titleEle = document.getElementsByTag("title").first();

        String title = titleEle.html();
        title = title.replaceAll(" - Pornhub.com", "");
        title = title.replaceAll(" ", "");

        return title;
    }

    /**
     * 获取m3u8文件的内容
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getM3u8Content(String url) throws UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
        headers.put("Referer", "https://cn.pornhub.com/");
//        headers.put("", "");
//        headers.put("", "");
//        headers.put("", "");

        byte[] bytes = HttpClientUtil.getWithProxy(url, headers, "utf-8");
        return new String(bytes, "utf-8");
    }

    public String downloadM3u8IfNecessary(String m3u8Url, String path) throws UnsupportedEncodingException {
        File file = new File(path + "m3u8.txt");
        String content = null;
        if(!file.exists()){
            content = getM3u8Content(m3u8Url);
            if(validM3u8File(content)){
                FileUtils.writeToFile(content, path + "m3u8.txt");
            }else{
                FileUtils.writeToFile(content, path + "m3u8Error.txt");
            }

        }else{
            content = FileUtils.readFromFile(path + "m3u8.txt");
        }

        return content;
    }

    private boolean validM3u8File(String content) {
        return content != null && content.contains(".ts");
    }

    public void downloadByM3u8(String m3u8Url, String rootPath, String targetFileName) throws IOException {
        String path = rootPath + targetFileName + File.separator;
        // 创建文件夹
       FileUtils.mkdirs(path);

        // url地址前缀
        String prefixUrl = m3u8Url.substring(0, m3u8Url.lastIndexOf("/") + 1);

        // 获取m3u8链接的内容
        String content = downloadM3u8IfNecessary(m3u8Url, path);

        // 解析出m3u8中的ts文件路径
        List<String> urls = M3u8Parser.parseUrls(content);

        // 创建ffmepg合并使用的文件
        String fileListName = "fileList.txt";
        createFileList(path, fileListName, urls);

        //
        List<String> errorUrls = download(prefixUrl, path, urls, true);

        // 有错误文件
        int retryNum = 1;
        while(errorUrls != null && errorUrls.size() > 0 && retryNum-- > 0){
            BufferedWriter errorWriter = new BufferedWriter(new FileWriter(path + "errorList.txt"));
            for (String errorUrl : errorUrls) {
                errorWriter.write(prefixUrl + errorUrl);
                errorWriter.newLine();
            }
            errorWriter.flush();

            errorUrls = download(prefixUrl, path, errorUrls, false);
        }



        if(errorUrls != null && errorUrls.size() > 0){
            urls.removeAll(errorUrls);

            fileListName = "fileListPart.txt";
            createFileList(path, fileListName, urls);
        }

        // 合成一个视频
        VideoUtils.concatVideo(path, fileListName, targetFileName);

//        executorService.shutdown();
    }

    public List<String> download(String prefixUrl, String path, List<String> urls, boolean retryOnError) throws IOException{
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

            });
        }

        // 等待文件下载完成
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return errorUrls;
    }

    private boolean validTs(byte[] data) {
        return data[0] == 0x47 && data[1] == 0x40;
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

    public void createFileList(String path, String fileListName, List<String> urls) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + fileListName));

        String pathbak = path.replaceAll("\\\\", "\\\\\\\\");

        for (int i = 0; i < urls.size(); i++) {
            String fileName = getFileNameByTsUrl(urls.get(i));
//            bw.write("file '" + pathbak + fileName +"'");
            bw.write("file '" + fileName +"'");
            bw.newLine();
        }
        bw.flush();
    }

    public void createFileList2(String path, String fileListName) throws IOException {
        File file = new File(path);

        List<String> tsFileNames = new ArrayList<>();
        for (File subFile : file.listFiles()) {
            if(subFile.getName().endsWith("ts")){
                FileInputStream fis = new FileInputStream(subFile);
                byte[] data = new byte[2];
                fis.read(data);
                if(validTs(data)){
                    tsFileNames.add(subFile.getName());
                }
            }
        }

        tsFileNames.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = Integer.parseInt(o1.split("-")[1]);
                int i2 = Integer.parseInt(o2.split("-")[1]);
                return i1 - i2;
            }
        });

        System.out.println(tsFileNames);

        BufferedWriter bw = new BufferedWriter(new FileWriter(path + fileListName));
        String pathbak = path.replaceAll("\\\\", "\\\\\\\\");
        for (int i = 0; i < tsFileNames.size(); i++) {
            bw.write("file '" + pathbak + tsFileNames.get(i) +"'");
            bw.write("file '" + tsFileNames.get(i) +"'");
            bw.newLine();
        }
        bw.flush();
    }

    public String getFileNameByTsUrl(String url){
        int start = url.lastIndexOf("/") + 1;
        int end = url.indexOf("?");
        return url.substring(start, end);
    }

    public void batchDownload(List<DownloadInfo> downloadInfos, String path){
        for (DownloadInfo downloadInfo : downloadInfos) {
            try {
                downloadByM3u8(downloadInfo.getM3u8URL(), path, downloadInfo.getDirName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void batchDownloadByM3u8File(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = null;
        int i = 1;

        List<DownloadInfo> downloadInfos = new ArrayList<>();
        DownloadInfo downloadInfo = null;
        while ((line = br.readLine()) != null){
            if(StringUtils.isBlank(line)){
                continue;
            }
            if(line.startsWith("#")){
                continue;
            }

            if(i == 1){
                downloadInfo = new DownloadInfo();
                downloadInfo.setDirName(line.trim());
                i++;
            }else if(i == 2){
                downloadInfo.setM3u8URL(line.trim());
                downloadInfos.add(downloadInfo);
                i = 1;
            }
        }

        batchDownload(downloadInfos, path.substring(0, path.lastIndexOf("\\") + 1));
    }

    public void batchDownloadByDetailFile(String detailFilePath) throws IOException {
        String path = detailFilePath.substring(0, detailFilePath.lastIndexOf("\\") + 1);

        BufferedReader br = new BufferedReader(new FileReader(detailFilePath));
        String line = null;
        int i = 1;

        List<DownloadInfo> downloadInfos = new ArrayList<>();

        while ((line = br.readLine()) != null){
            if(StringUtils.isBlank(line)){
                continue;
            }
            if(line.startsWith("#")){
                continue;
            }
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setDetailPageURL(line.trim());
            downloadInfos.add(downloadInfo);
        }

        for (DownloadInfo di : downloadInfos) {
            try {
                String urlIndex = di.getDetailPageURL().substring(di.getDetailPageURL().lastIndexOf("=") + 1);
                // 详情页面
                String detailPageContent = downloadDetailPageIfNecessary(di.getDetailPageURL(), path + "temp\\" + urlIndex +".html");
                // m3u8URL
                String m3u8URL = parseM3u8UrlFromDetailPage(detailPageContent);
                if(!m3u8URL.contains(".m3u8")){
                    logger.error("{} grap m3u8Url error: {}", di.getDetailPageURL(), m3u8URL);
                    continue;
                }
                di.setM3u8URL(m3u8URL);

                // 标题
                String title = parseTitleFromDetailPage(detailPageContent);
                if(StringUtils.isBlank(title) || "null".equals(title)){
                    title = urlIndex;
                }
                di.setTitle(title);

                downloadByM3u8(di.getM3u8URL(), path, di.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }

    public static void main(String[] args) throws IOException {
        PornHubCrawer pornHubCrawer = new PornHubCrawer();
        String path = "G:\\video\\";

        // ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
        // ffmpeg -f concat -safe 0 -i filelistPart.txt -c copy output.mp4 -y
//        https://all.mk-proxy.tk/-----https://github.com/istio/istio/releases/download/1.6.3/istioctl-1.6.3-osx.tar.gz

        // https://c1v-h.phncdn.com/hls/videos/202008/04/339434961/,1080P_4000K,720P_4000K,480P_2000K,240P_400K,_339434961.mp4.urlset/index-f2-v1-a1.m3u8?0i6Jg9GQhF3--PeND4mWz13YkaBxoMJLKcT8I_xHmmhV0tV_nVL41XWN6PfNos9MHDkBwcgv-MLMsfkYQpPNWpIsTF4dWt_hu_3KSK5FenIeb8JOCEDPoKb74Mhe5gfSiybe-1dtHQFzk7-jXCYum54cxGGniURwQQzmksCH6T9BT8WJByxqkC2TATq_Cb4LE6QbLhcUwixo8uW1Sgtm

//        pornHubCrawer.batchDownloadByM3u8File("G:\\video\\batchM3u8.txt");
        pornHubCrawer.batchDownloadByDetailFile("G:\\video\\batchDetail.txt");
    }



}

class DownloadInfo{
    private String detailPageURL;
    private String dirName;
    private String m3u8URL;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailPageURL() {
        return detailPageURL;
    }

    public void setDetailPageURL(String detailPageURL) {
        this.detailPageURL = detailPageURL;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getM3u8URL() {
        return m3u8URL;
    }

    public void setM3u8URL(String m3u8URL) {
        this.m3u8URL = m3u8URL;
    }
}
