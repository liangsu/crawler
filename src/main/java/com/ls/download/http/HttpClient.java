package com.ls.download.http;

import java.util.Map;

/**
 * @author liangsu
 * @version v1.0
 * @Description
 * @Date 2021/3/10 13:18
 * @since
 */
public interface HttpClient {

    byte[] getBytes(String url, Map<String, String> headerMap);

    String getString(String url, Map<String, String> headerMap);

    byte[] postBytes(String url, Map<String, String> headerMap);

    String postString(String url, Map<String, String> headerMap);

}
