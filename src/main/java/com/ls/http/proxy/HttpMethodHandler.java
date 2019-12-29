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
import java.util.Map;

public class HttpMethodHandler {
    private String url;
    private Map<Integer, String> urlIndexNameMap;
    private Map<Integer, String> indexNameMap;// key:name, value: parameter index

    private RequestMethod method;
    private Class[] parameterTypes;

    public Object request(Object[] args, HttpRequestContext httpRequestContext){
        // 解析url
        String realUrl = this.url;
        if(urlIndexNameMap != null && urlIndexNameMap.size() > 0){
            for(Map.Entry<Integer, String> entry : urlIndexNameMap.entrySet()){
                int index = entry.getKey();
                String name = entry.getValue();
                realUrl = realUrl.replaceAll("\\{" + name + "\\}", String.valueOf(args[index]));
            }
        }

        String result = null;
        if(RequestMethod.GET == method){
            // 拼接url参数
            StringBuilder urlBuilder = new StringBuilder(realUrl);
            int count = 0;
            for(Map.Entry<Integer, String> entry : indexNameMap.entrySet()){
                int index = entry.getKey();
                String name = entry.getValue();
                if(urlIndexNameMap.containsKey(index)){
                    continue;
                }
                if(count == 0){
                    urlBuilder.append("?");
                    count++;
                }else{
                    urlBuilder.append("&");
                }
                urlBuilder.append(name+"="+ String.valueOf(args[index]));
            }
            realUrl = urlBuilder.toString();
            System.out.println("url:" + realUrl);

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
                result = HttpUtils.resovleResponse2String(httpResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(RequestMethod.POST == method){



        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println(result);
        return  result;
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


    public Map<Integer, String> getUrlIndexNameMap() {
        return urlIndexNameMap;
    }

    public void setUrlIndexNameMap(Map<Integer, String> urlIndexNameMap) {
        this.urlIndexNameMap = urlIndexNameMap;
    }

    public Map<Integer, String> getIndexNameMap() {
        return indexNameMap;
    }

    public void setIndexNameMap(Map<Integer, String> indexNameMap) {
        this.indexNameMap = indexNameMap;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public RequestMethod getMethod() {
        return method;
    }
    public void setMethod(RequestMethod method) {
        this.method = method;
    }
}
