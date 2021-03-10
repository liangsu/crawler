package com.ls.download.m3u8;

import com.ls.download.URLUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class M3u8Parser {
    private final static Logger logger = LoggerFactory.getLogger(M3u8Parser.class);

    public static M3u8 parse(String content) {
        M3u8 m3u8 = new M3u8();

        StringReader stringReader = new StringReader(content);
        BufferedReader br = new BufferedReader(stringReader);
        try{
            String line = null;
            M3u8Url m3u8Url = null;
            String preTsName = null;
            boolean sameTsName = false;
            while ((line = br.readLine()) != null){
                if(startWith("#", line)){ // "#"号开头
                    if(startWith("#EXT-X-VERSION", line)){ // 版本号
                        m3u8.setVersion(getValue(line));

                    }else if(startWith("#EXT-X-TARGETDURATION", line)){
                        m3u8.setTargetDuration(Integer.parseInt(getValue(line)));

                    }else if(startWith("#EXT-X-MEDIA-SEQUENCE", line)){
                        m3u8.setMediaSequence(getValue(line));

                    }else if(startWith("#EXT-X-PLAYLIST-TYPE", line)){
                        m3u8.setPlayListType(getValue(line));

                    }else if(startWith("#EXTINF", line)){ // 时长信息
                        if(m3u8Url == null){
                            m3u8Url = new M3u8Url();
                            m3u8Url.setM3u8(m3u8);
                            m3u8.getSubUrls().add(m3u8Url);
                        }

                        String extInfo = getValue(line);
                        if(StringUtils.isNotBlank(extInfo)){
                            String[] infos = extInfo.split(",");
                            if(infos != null){
                                m3u8Url.setDuration(Float.parseFloat(infos[0]));
                                if(infos.length > 1){
                                    m3u8Url.setTitle(infos[1]);
                                }
                            }
                        }

                    }else if(startWith("#EXT-X-KEY", line)){
                        if(m3u8Url == null){
                            m3u8Url = new M3u8Url();
                            m3u8Url.setM3u8(m3u8);
                            m3u8.getSubUrls().add(m3u8Url);
                        }
                        M3u8Key key = parseKey(line);
                        m3u8Url.setKey(key);

                    }else if(startWith("#EXT-X-ENDLIST", line)){ // 结束标记
                        break;
                    }

                }else{ // 不是#号开头，则是ts文件的url地址
                    m3u8Url.setUrl(line);
                    m3u8Url = null; // 开始新行的解析

                    if(!sameTsName){
                        int end = line.indexOf("?");
                        String tsName = line.substring(0, end);
                        if(preTsName == null){
                            preTsName = tsName;
                        }else{
                            sameTsName = preTsName.equals(tsName);
                        }
                    }

                }

                m3u8.setHasSameTsName(sameTsName);
            }
        }catch (IOException e){
            logger.error("解析m3u8文件出错！", e);
        }

        otherParse(m3u8);

        return m3u8;
    }

    /**
     *
     * @param m3u8
     */
    private static void otherParse(M3u8 m3u8) {
        List<M3u8Url> urls = m3u8.getSubUrls();
        if(urls == null || urls.size() <= 0){
            return;
        }

        Map<String, String> map = new HashMap<>();
        String repeat = "value-repeat";
        for (int i = 0; i < urls.size(); i++) {
            M3u8Url url = urls.get(i);
            // 解析url上的参数
            HashMap<String, String> params = URLUtils.getParams(url.getUrl());
            url.setUrlParams(params);

            // 判断参数是否重复
            for(Map.Entry<String, String> entry : params.entrySet()){
                String old = map.get(entry.getKey());
                if(old == null){
                    map.put(entry.getKey(), entry.getValue());
                }else if(old.equals(repeat)){ // 重复
                    continue;
                }
                else if(old.equals(entry.getValue())){ //
                    map.put(entry.getKey(), repeat);
                }
            }
        }
        // url的参数中，value不重复的字段名
        List<String> notRepeatKeys = new ArrayList<>();
        for(Map.Entry<String, String> entry : map.entrySet()){
            if(!entry.getKey().equals(repeat)){
                notRepeatKeys.add(entry.getKey());
            }
        }
        m3u8.setSubUrlNotRepeatParamKey(notRepeatKeys);
    }

    private static M3u8Key parseKey(String line) {
        M3u8Key key = new M3u8Key();

        String pairs = line.substring("#EXT-X-KEY:".length());

        String[] pairArray = pairs.split(",");

        if(pairArray != null){
            for (String pair : pairArray) {
                if(startWith("METHOD", pair)){
                    key.setMethod(pair.split("=")[1]);

                }else if(startWith("URI", pair)){
                    key.setUri(pair.substring("URI=\"".length(), pair.length() - 1));

                }else if(startWith("IV", pair)){
                    key.setIv(pair.split("=")[1]);
                }
            }
        }

        return key;
    }

    private static boolean startWith(String start, String line) {
        return line != null && line.startsWith(start);
    }

    private static String getValue(String line) {
        String[] keyPairs = line.split(":");
        if(keyPairs != null && keyPairs.length > 1){
            return keyPairs[1];
        }
        return null;
    }

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
