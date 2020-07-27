package com.ls.http.proxy;


import com.ls.http.HttpUtils;
import com.ls.http.annotation.RequestMethod;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpMethodHandler {
    private MethodMetadata methodMetadata;

    public HttpMethodHandler(MethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    public Object request(Object[] args, HttpRequestContext httpRequestContext){

        // 表单值
        Map<String, String> formData = new HashMap<>();
        Map<Integer, String> indexNameMap = methodMetadata.getIndexNameMap();
        for (int i = 0; args != null && i < args.length; i++) {
            String name = indexNameMap.get(i);
            Object value = args[i];
            if(value != null){
                formData.put(name, String.valueOf(args[i]));
            }else{
                formData.put(name, "");
            }
        }

        // 解析url
        String realUrl = methodMetadata.getUrl();
        List<String> urlParams = methodMetadata.getUrlParams();
        if(urlParams != null && urlParams.size() > 0){
            for (String name : urlParams) {
                String value = formData.get(name);
                formData.remove(name); // 从form中移除
                realUrl = realUrl.replaceAll("\\{" + name + "\\}", value);
            }
        }

        String result = null;
        if(RequestMethod.GET == methodMetadata.getMethod()){
            result = executeGet(httpRequestContext, formData, realUrl);

        }else if(RequestMethod.POST == methodMetadata.getMethod()){


        }

        System.out.println("\n\n\n\n\n");
        System.out.println(result);
        return  result;
    }

    private String executeGet(HttpRequestContext httpRequestContext, Map<String, String> formData, String realUrl) {
        // 拼接url参数
        StringBuilder urlBuilder = new StringBuilder(realUrl);
        int count = 0;
        for(Map.Entry<String, String> entry : formData.entrySet()){
            String name = entry.getKey();
            String value = entry.getValue();
            if(count == 0){
                urlBuilder.append("?");
                count++;
            }else{
                urlBuilder.append("&");
            }
            urlBuilder.append(name+"="+ value);
        }
        realUrl = urlBuilder.toString();

        // 执行请求
        try {
            CloseableHttpClient httpClient = null;
            if(httpRequestContext.isSsl()){
                httpClient = HttpClients.custom().setSSLContext(createIgnoreVerifySSL()).build();
            }else {
                httpClient = HttpClients.createDefault();
            }
            HttpGet httpGet = new HttpGet(realUrl);
            for(Map.Entry<String, String> entry : httpRequestContext.getHeaders().entrySet()){
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            return HttpUtils.resovleResponse2String(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }


}
