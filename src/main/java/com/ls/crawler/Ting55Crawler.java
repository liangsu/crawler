package com.ls.crawler;

import com.ls.http.JsoupUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  抓取网站： https://ting55.com
 */
public class Ting55Crawler {

    private static Logger logger = LoggerFactory.getLogger(Ting55Crawler.class);

    /**
     * 从目录页面获取详细列表页的url地址
     * @param url 如：https://ting55.com/book/1090
     * @return 返回地址形如： https://ting55.com/book/1090-1
     */
    public List<String> fromDirectoryGetDetailPageList(String url){
        List<String> urls = new ArrayList<>(100);

        String prefixUrl = url.substring(0, url.indexOf("com") + 3);

        Document document = null;
        try {
            document = JsoupUtils.getDocument(url);

            Elements plist = document.getElementsByClass("plist");

            Elements hrefs = plist.get(0).getElementsByClass("free");

            for(int i = 0; i < hrefs.size(); i++){
                Element href = hrefs.get(i);
                String directoryUrl = prefixUrl + href.attr("href");
                urls.add(directoryUrl);
            }

        } catch (IOException e) {
            logger.error("获取页面失败！", e);
        }

        logger.debug("从目录页面{}获取到的详情页面数量：{}", url, urls.size());

        return urls;
    }

    /**
     * 从【详情页面】获取【下载地址】
     * @param url
     * @return
     */
    public String crawlingDownloadURLFromDetailPageUrl(String url){
        String content = null;
        try {
            content = JsoupUtils.get(url);
        } catch (IOException e) {
            logger.error("获取页面失败！", e);
        }

        String downloadURL = JsoupUtils.substring(content, "var a={mp3:\"", "\"};ting55_play");
        logger.debug("从详情页面{}获取到的下载地址: {}", url, downloadURL);
        return downloadURL;
    }


    /**
     * 根据【目录页面】获取所有的【下载地址】
     * @param url
     * @return
     */
    public void throughDirectoryPageGetDownloadURLs(String url){
        // 根据目录页面获取所有的详细页面地址
        List<String> detailPageUrlList = fromDirectoryGetDetailPageList(url);

        List<String> downloadURLs = new ArrayList<>(detailPageUrlList.size());

        int start = 198;
        int errorCount = 0;
        for(int i = start; i < detailPageUrlList.size(); i++){
            // 从详情页面抓取下载地址
            try{
                String downloadUrl = crawlingDownloadURLFromDetailPageUrl(detailPageUrlList.get(i));
                downloadURLs.add(downloadUrl);
                errorCount = 0;

            }catch (Exception e){
                logger.error("从第{}个详情页面获取下载地址失败", i, e);

                writeDownloadUrlsToFile(downloadURLs);
                downloadURLs.clear();

                i--;
                errorCount++;
                try {
                    Thread.sleep(1000 * (errorCount + 10));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }
        }

        writeDownloadUrlsToFile(downloadURLs);
    }

    /**
     * 将下载地址写入文件
     * @param downlaodURLs
     */
    public void writeDownloadUrlsToFile(List<String> downlaodURLs){
        BufferedWriter bw = null;
        try {
            File file = new File("下载地址.txt");
            bw = new BufferedWriter(new FileWriter(file, true));

            for(String url : downlaodURLs){
                bw.append(url);
                bw.append("\r\n");
            }

            logger.info("将{}个下载地址写入文件：{}", downlaodURLs.size(), file.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Ting55Crawler crawler = new Ting55Crawler();

        crawler.throughDirectoryPageGetDownloadURLs("https://ting55.com/book/1090");
    }


}
