package com.ls.download.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * @author liangsu
 * @version v1.0
 * @Description
 * @Date 2021/3/10 13:21
 * @since
 */
public class DefaultHttpClient implements HttpClient {

    CloseableHttpClient httpclient = null;
    HttpClientContext context;
    Map<String, String> headers;

    public DefaultHttpClient(Map<String, String> headers){
        this.headers = headers;
        httpclient = HttpClients.createDefault();
        context = HttpClientContext.create();
    }

    private void initHttpReq(AbstractHttpMessage httpRequestBase, Map<String, String> headerMap){
        if(headerMap != null) {
            for(String key : headerMap.keySet()) {
                httpRequestBase.setHeader(key, headerMap.get(key));
            }
        }
    }

    @Override
    public byte[] getBytes(String url, Map<String, String> headerMap) {
        try {
            HttpGet httpget = new HttpGet(url);
            initHttpReq(httpget, this.headers);
            initHttpReq(httpget, headerMap);

            CloseableHttpResponse response = httpclient.execute(httpget,context);
            try {
                return EntityUtils.toByteArray(response.getEntity());
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public String getString(String url, Map<String, String> headerMap) {
        return new String(getBytes(url, headerMap));
    }

    @Override
    public byte[] postBytes(String url, Map<String, String> headerMap) {
        return new byte[0];
    }

    @Override
    public String postString(String url, Map<String, String> headerMap) {
        return null;
    }
}
