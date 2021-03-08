package com.ls.download;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class URLUtils {

    public static String getUrlPrefix(String url){
        String prefixUrl = url.substring(0, url.lastIndexOf("/") + 1);
        return prefixUrl;
    }

    private static HashMap<String, String> emptyMap = new HashMap(0);

    /**
     *
     * @param url aa.ts?name=xxx&age=22
     * @return
     */
    public static HashMap<String, String> getParams(String url){
        if(StringUtils.isBlank(url)){
            return emptyMap;
        }

        int pos = url.lastIndexOf("?");
        if(pos < 0){
            return emptyMap;
        }

        HashMap<String, String> map = new HashMap(0);

        String paramString = url.substring(pos + 1);
        String[] pairs = paramString.split("&");
        if(pairs != null){
            for (String pair : pairs) {
                String[] keys = pair.split("=");
                map.put(keys[0], keys[1]);
            }
        }

        return map;
    }

}
