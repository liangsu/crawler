package com.ls.download.m3u8;

import com.ls.http.FileUtils;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

class M3U8UtilsTest {

    @Test
    void parse() throws Exception {
        String rootPath = M3U8UtilsTest.class.getClassLoader().getResource("").getPath();
        rootPath = "F:\\dev\\workspace_idea\\crawler\\src\\test\\java\\";

        byte[] bytes = FileUtils.readFileToByteArray(rootPath + "com\\ls\\download\\m3u8\\a.m3u8");
        M3u8 m3u8 = M3u8Parser.parse(new String(bytes));
        System.out.println(m3u8.isHasSameTsName());
    }

    @Test
    void getTsNameFromM3u8Url() throws Exception {
        String rootPath = M3U8UtilsTest.class.getClassLoader().getResource("").getPath();
        rootPath = "F:\\dev\\workspace_idea\\crawler\\src\\test\\java\\";
        byte[] bytes = FileUtils.readFileToByteArray(rootPath + "com\\ls\\download\\m3u8\\m3u8.txt");
        M3u8 m3u8 = M3u8Parser.parse(new String(bytes));

        for(M3u8Url url : m3u8.getSubUrls()){
            System.out.println(M3u8Utils.getTsNameFromM3u8Url(url));
        }
    }

    @Test
    void getTsNameFromM3u8Url2() throws Exception {
        String rootPath = M3U8UtilsTest.class.getClassLoader().getResource("").getPath();
        rootPath = "F:\\dev\\workspace_idea\\crawler\\src\\test\\java\\";
        byte[] bytes = FileUtils.readFileToByteArray(rootPath + "com\\ls\\download\\m3u8\\a.m3u8");
        M3u8 m3u8 = M3u8Parser.parse(new String(bytes));

        for(M3u8Url url : m3u8.getSubUrls()){
            System.out.println(M3u8Utils.getTsNameFromM3u8Url(url));
        }
    }

    @Test
    void hex() throws Exception {
        byte[] bytes = Hex.decodeHex("00000000000000000000000000000000".toCharArray());
        System.out.println(bytes);
    }
}