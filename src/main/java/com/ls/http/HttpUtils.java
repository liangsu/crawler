package com.ls.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	public static String get(String url) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
//        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        try {
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
            return result;
        } finally {
            httpResponse.close();
        }
    }

    public static String getSSL(String url) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
//        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        try {
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
            return result;
        } finally {
            httpResponse.close();
        }
    }

	public static String postJSON(String url, String jsonBody) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonBody));

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try {
            HttpEntity entity = httpResponse.getEntity();
            String result = null;
            if(entity != null){
            	result = EntityUtils.toString(entity);
            }
//            System.out.println(result);
            return result;
        } finally {
            httpResponse.close();
        }
    }

	public static String put(String url) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPut httpPut = new HttpPut(url);

        CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
        try {
            HttpEntity entity = httpResponse.getEntity();
            String result = null;
            if(entity != null){
            	result = EntityUtils.toString(entity);
            }
//            System.out.println(result);
            return result;
        } finally {
            httpResponse.close();
        }
    }
}