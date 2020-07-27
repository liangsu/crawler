package com.ls.crawler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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


}
