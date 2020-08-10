package com.ls.crawler;

import com.ls.http.JsoupUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class PornHubTest {

    @Test
    public void detailPage() throws UnsupportedEncodingException {

        PornHubCrawer pornHubCrawer = new PornHubCrawer();
        String detailPage = pornHubCrawer.detailPage("https://cn.pornhub.com/view_video.php?viewkey=ph5e4d64602a8b7");
        System.out.println(detailPage);

        Document document = Jsoup.parse(detailPage);

        System.out.println("-----------------------");
        System.out.println("-----------------------");
        System.out.println("-----------------------");
        Element player = document.getElementById("player");
        Element element =  player.getElementsByTag("script ").first();
        System.out.println(element.data());

        ScriptEngine engine = JSUtils.runJs("var playerObjList = {}; " + element.data());
        System.out.println(engine.get("media_8"));

//        System.out.println(element);
    }

    @Test
    public void parseM3u8UrlFromDetailPage() throws UnsupportedEncodingException {
        PornHubCrawer pornHubCrawer = new PornHubCrawer();
        String detailPage = pornHubCrawer.detailPage("https://cn.pornhub.com/view_video.php?viewkey=ph5e4d64602a8b7");
        String m3u8URL = pornHubCrawer.parseM3u8UrlFromDetailPage(detailPage);
        System.out.println(m3u8URL);
    }

    @Test
    public void parseTitleFromDetailPage() throws UnsupportedEncodingException {
        PornHubCrawer pornHubCrawer = new PornHubCrawer();
        String detailPage = pornHubCrawer.detailPage("https://cn.pornhub.com/view_video.php?viewkey=ph5e4d64602a8b7");
        String m3u8URL = pornHubCrawer.parseTitleFromDetailPage(detailPage);
        System.out.println(m3u8URL);
    }

    @Test
    public void str() {
        String str = "https://cn.pornhub.com/view_video.php?viewkey=ph5e4d64602a8b7";
        System.out.println(str.substring(str.lastIndexOf("=")));

        str = "G:\\video\\贵在真实年轻大学生情侣周日校外开房啪啪啪小妹子叫的很给力表情销魂.316\\";

        System.out.println(str.lastIndexOf(File.separator));
        str.indexOf(File.separator);

    }

}
