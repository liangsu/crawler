package com.ls.http;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsoupUtils {

    private static Logger logger = LoggerFactory.getLogger(JsoupUtils.class);

    public static String get(String url) throws IOException {
        Connection connection = Jsoup.connect(url);
        Connection.Response response = connection.method(Connection.Method.GET).execute();
        String body = response.body();

        logger.debug("地址{}的页面内容:\n {}", url, body);
        return response.body();
    }

    public static Document getDocument(String url) throws IOException {
        Connection connection = Jsoup.connect(url);
        Document document = connection.method(Connection.Method.GET).get();
        logger.debug("地址{}的页面内容:\n {}", url, document);
        return document;
    }

    public static String substring(String content, String begin, String end){
        int beginPos = content.indexOf(begin);
        int endPos = content.indexOf(end);
        return content.substring(beginPos + begin.length(), endPos);
    }

    public static void main(String[] args) throws IOException {
        String content = get("https://ting55.com/book/1090-3");
        System.out.println(substring(content, "var a={mp3:\"", "\"};ting55_play"));
//        HttpUtils.get("https://www.baidu.com/s");
    }
}
