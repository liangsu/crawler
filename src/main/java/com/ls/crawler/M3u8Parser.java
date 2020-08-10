package com.ls.crawler;

import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class M3u8Parser {

    public static List<String> parseUrls(String content) throws IOException {
        List<String> urls = new ArrayList<>(100);

        StringReader stringReader = new StringReader(content);
        BufferedReader br = new BufferedReader(stringReader);

        String line = null;
        while ((line = br.readLine()) != null){
            if(line.contains(".ts")){
                urls.add(line);
            }
        }

        return urls;
    }

    public static void main(String[] args) throws Exception {
//        File file = new File("C:\\Users\\warho\\Desktop\\aa\\刚认识的02年艺校校花约酒店啪啪露脸，超高清，中国国产麻豆秘书高颜值美女SWAG");
        File file = new File("G:\\video\\0");


        Set<java.lang.String> set = new HashSet<java.lang.String>();
        for (File listFile : file.listFiles()) {

            if(listFile.getName().endsWith("ts")){

                FileInputStream fis = new FileInputStream(listFile);
                byte[] data = new byte[2];
                fis.read(data);

                if(data[0] == 0x47 && data[1] == 0x40){
                    System.out.println(true);
                }

                char[] chars = Hex.encodeHex(data);
                set.add(java.lang.String.valueOf(chars));
            }

        }

        System.out.println("---------------");
        for (String s : set) {
            System.out.println(s);
        }

    }

}
